package DAO;

import Interface.IOrderItem;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderDAO {
    // Đường dẫn DB phải khớp với file CreateDatabase
    private final String url = "jdbc:sqlite:src/storage/coffee_shop.db";

    /**
     * Lưu đơn hàng mới vào Database
     */
    public int saveOrder(Order order) {
        // 1. Insert Order: orderId tự tăng nên không cần insert
        String sqlOrder = "INSERT INTO Orders(createdTime, status, staff, tableId) VALUES(?, ?, ?, ?)";

        // 2. Insert OrderItem: orderItemId tự tăng nên không cần insert
        String sqlDetail = "INSERT INTO OrderItem(orderId, orderMenuId, quantity, note) VALUES(?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(url)) {
            // Bật ràng buộc khóa ngoại
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            conn.setAutoCommit(false); // Bắt đầu Transaction

            // --- BƯỚC 1: INSERT ORDERS ---
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                // ?1: createdTime
                long time = (order.getCreatedTime() != null) ? order.getCreatedTime().getTime() : System.currentTimeMillis();
                pstmt.setTimestamp(1, new Timestamp(time));

                // ?2: status
                pstmt.setString(2, order.getStatus());

                // ?3: staff
                if (order.getStaff() != null) {
                    pstmt.setString(3, order.getStaff().getUsername());
                } else {
                    pstmt.setNull(3, Types.VARCHAR);
                }

                // ?4: tableId
                try {
                    pstmt.setInt(4, Integer.parseInt(order.getTableId()));
                } catch (NumberFormatException e) {
                    pstmt.setInt(4, 0);
                }

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Tạo đơn hàng thất bại, không có dòng nào được thêm.");
                }

                // Lấy ID tự sinh (orderId)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        order.setOrderId(generatedId);
                    } else {
                        throw new SQLException("Tạo đơn hàng thất bại, không lấy được ID.");
                    }
                }
            }

            // --- BƯỚC 2: INSERT ORDER ITEM ---
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                // Thêm RETURN_GENERATED_KEYS để lấy ID của từng món
                try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail, Statement.RETURN_GENERATED_KEYS)) {
                    for (IOrderItem itemInterface : order.getItems()) {

                        if (itemInterface instanceof OrderItem) {
                            OrderItem item = (OrderItem) itemInterface;

                            // Kiểm tra ID món ăn
                            if (item.getMenuItem() == null || item.getMenuItem().getId() == null) continue;

                            // ?1: orderId
                            pstmtDetail.setInt(1, generatedId);

                            // ?2: orderMenuId
                            try {
                                int menuIdInt = Integer.parseInt(item.getMenuItem().getId());
                                pstmtDetail.setInt(2, menuIdInt);
                            } catch (NumberFormatException e) {
                                System.err.println("Bỏ qua món: ID món không hợp lệ (" + item.getMenuItem().getId() + ")");
                                continue;
                            }

                            // ?3: quantity
                            pstmtDetail.setInt(3, item.getQuantity());

                            // ?4: note
                            pstmtDetail.setString(4, item.getNote());

                            // Thực thi insert từng dòng
                            int affectedItemRows = pstmtDetail.executeUpdate();

                            // Lấy orderItemId vừa sinh ra và gán lại vào object
                            if (affectedItemRows > 0) {
                                try (ResultSet itemKeys = pstmtDetail.getGeneratedKeys()) {
                                    if (itemKeys.next()) {
                                        int generatedItemId = itemKeys.getInt(1);
                                        item.setOrderItemId(generatedItemId);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            conn.commit(); // Xác nhận Transaction
            System.out.println("-> Đã lưu Order thành công. ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Lỗi Transaction SaveOrder: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return generatedId;
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(int orderId) {
        Order order = null;
        String sqlOrder = "SELECT * FROM Orders WHERE orderId = ?";
        String sqlDetail = "SELECT * FROM OrderItem WHERE orderId = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            // --- BƯỚC 1: Lấy Header ---
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
                pstmt.setInt(1, orderId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        order = new Order();
                        order.setOrderId(rs.getInt("orderId"));
                        order.setCreatedTime(rs.getTimestamp("createdTime"));
                        order.setStatus(rs.getString("status"));

                        int tableIdInt = rs.getInt("tableId");
                        order.setTableId(String.valueOf(tableIdInt));

                        String staffName = rs.getString("staff");
                        if (staffName != null) {
                            model.Staff s = new model.Staff();
                            s.setUsername(staffName);
                            order.setStaff(s);
                        }
                    }
                }
            }

            if (order == null) {
                return null;
            }

            // --- BƯỚC 2: Lấy Items ---
            List<IOrderItem> items = new ArrayList<>();

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                pstmtDetail.setInt(1, orderId);

                try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                    while (rsDetail.next()) {
                        // Tái tạo MenuItem
                        int menuId = rsDetail.getInt("orderMenuId");
                        MenuItem menuItem = new MenuItem(
                                String.valueOf(menuId), "", 0.0, "", null
                        );

                        // Lấy ID riêng của item (orderItemId)
                        int orderItemId = rsDetail.getInt("orderItemId");
                        int quantity = rsDetail.getInt("quantity");
                        String note = rsDetail.getString("note");

                        // Tái tạo OrderItem với đầy đủ ID
                        OrderItem item = new OrderItem(menuItem, quantity, note, orderItemId);

                        items.add(item);
                    }
                }
            }

            order.setItems(items);

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Order theo ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return order;
    }

    /**
     * Lấy danh sách đơn hàng trong một ngày
     */
    public List<Order> getOrdersByDate(java.sql.Date date) {
        List<Order> orders = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp startTime = new Timestamp(cal.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Timestamp endTime = new Timestamp(cal.getTimeInMillis());

        String sqlOrder = "SELECT * FROM Orders WHERE createdTime BETWEEN ? AND ?";
        String sqlDetail = "SELECT * FROM OrderItem WHERE orderId = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder)) {
                pstmt.setTimestamp(1, startTime);
                pstmt.setTimestamp(2, endTime);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Order order = new Order();
                        int currentOrderId = rs.getInt("orderId");

                        order.setOrderId(currentOrderId);
                        order.setCreatedTime(rs.getTimestamp("createdTime"));
                        order.setStatus(rs.getString("status"));
                        order.setTableId(String.valueOf(rs.getInt("tableId")));

                        String staffName = rs.getString("staff");
                        if (staffName != null) {
                            model.Staff s = new model.Staff();
                            s.setUsername(staffName);
                            order.setStaff(s);
                        }

                        // Lấy chi tiết
                        List<IOrderItem> items = new ArrayList<>();
                        try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                            pstmtDetail.setInt(1, currentOrderId);

                            try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                                while (rsDetail.next()) {
                                    int menuId = rsDetail.getInt("orderMenuId");
                                    MenuItem menuItem = new MenuItem(
                                            String.valueOf(menuId), "", 0.0, "", null
                                    );

                                    int orderItemId = rsDetail.getInt("orderItemId");
                                    OrderItem item = new OrderItem(
                                            menuItem,
                                            rsDetail.getInt("quantity"),
                                            rsDetail.getString("note"),
                                            orderItemId
                                    );

                                    items.add(item);
                                }
                            }
                        }
                        order.setItems(items);
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách Order theo ngày: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }
}