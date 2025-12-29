package DAO;

import Interface.IOrderItem;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class OrderDAO {
    // Đường dẫn DB phải khớp với file CreateDatabase
    private final String url = "jdbc:sqlite:storage/coffee_shop.db";

    /**
     * Lưu đơn hàng mới vào Database
     */
    public boolean saveOrder(Order order) {

        String sqlOrder = """
        INSERT INTO Orders(orderId, createdTime, status, staff, tableId)
        VALUES(?, ?, ?, ?, ?)
    """;

        String sqlDetail = """
        INSERT INTO OrderItem(orderItemId, orderId, orderMenuId, quantity, note)
        VALUES(?, ?, ?, ?, ?)
    """;

        try (Connection conn = DriverManager.getConnection(url)) {

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            conn.setAutoCommit(false);

            // ===== INSERT ORDERS =====
            String orderId = UUID.randomUUID().toString();

            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {

                ps.setString(1, orderId);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setString(3, order.getStatus());
                ps.setString(4,
                        order.getStaff() != null ? order.getStaff().getUsername() : null
                );
                ps.setNull(5, Types.INTEGER); // không quan tâm tableId

                ps.executeUpdate();
            }

            // ===== INSERT ORDER ITEM =====
            if (order.getItems() != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {

                    for (IOrderItem i : order.getItems()) {
                        if (!(i instanceof OrderItem item)) continue;
                        if (item.getMenuItem() == null) continue;

                        ps.setString(1, UUID.randomUUID().toString()); // orderItemId
                        ps.setString(2, orderId);                      // orderId
                        ps.setString(3, item.getMenuItem().getId());   // menuId (TEXT)
                        ps.setInt(4, item.getQuantity());
                        ps.setString(5, item.getNote());

                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
                        order.setOrderId(rs.getString("orderId"));
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
                        String currentOrderId = rs.getString("orderId");

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
                            pstmtDetail.setString(1, currentOrderId);

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
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        // Câu lệnh lấy đơn hàng
        String sqlOrder = "SELECT * FROM Orders ORDER BY createdTime DESC";
        // Câu lệnh lấy chi tiết món của đơn hàng đó (Join với MenuItem để lấy giá và tên món)
        String sqlDetail = """
        SELECT oi.*, mi.name, mi.price, mi.category 
        FROM OrderItem oi
        JOIN MenuItem mi ON oi.orderMenuId = mi.menuId
        WHERE oi.orderId = ?
    """;

        try (Connection conn = DriverManager.getConnection(url)) {
            // 1. Lấy danh sách đơn hàng
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlOrder)) {

                while (rs.next()) {
                    Order order = new Order();
                    String orderId = rs.getString("orderId");
                    order.setOrderId(orderId);
                    order.setTableId(rs.getString("tableId"));
                    order.setStatus(rs.getString("status"));

                    String timeStr = rs.getString("createdTime");
                    if (timeStr != null) {
                        try {
                            long millis = Long.parseLong(timeStr);
                            order.setCreatedTime(new java.util.Date(millis));
                        } catch (Exception e) {
                            order.setCreatedTime(new java.util.Date());
                        }
                    }

                    // 2. Lấy chi tiết món cho đơn hàng này
                    List<IOrderItem> items = new ArrayList<>();
                    try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                        pstmtDetail.setString(1, orderId);
                        try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                            while (rsDetail.next()) {
                                // Tạo đối tượng MenuItem để chứa thông tin giá/tên
                                model.MenuItem menuItem = new model.MenuItem(
                                        rsDetail.getString("orderMenuId"),
                                        rsDetail.getString("name"),
                                        rsDetail.getDouble("price"),
                                        rsDetail.getString("category"),
                                        ""
                                );

                                // Tạo OrderItem và gán vào list
                                model.OrderItem item = new model.OrderItem(
                                        menuItem,
                                        rsDetail.getInt("quantity"),
                                        rsDetail.getString("note")
                                );
                                items.add(item);
                            }
                        }
                    }

                    // Gán danh sách món vào Đơn hàng
                    order.setItems(items);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}