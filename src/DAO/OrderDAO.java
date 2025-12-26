//File này để là để kết nối giữa file java và thao tác trên database
package DAO;

import model.Order;
import model.OrderItem;

import java.sql.*;

public class OrderDAO {
    private final String url = "jdbc:sqlite:src/storage/coffee_shop.db";

    public int saveOrder(Order order) {
        String sqlOrder = "INSERT INTO Orders(orderId, createdTime, status, staff, tableId) VALUES(?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO OrderItem(orderId, orderItemId, orderMenuId, quantity, note) VALUES(?, ?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            conn.setAutoCommit(false);

            // --- BƯỚC 1: INSERT VÀO BẢNG 'Orders' ---
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                long time = (order.getCreatedTime() != null) ? order.getCreatedTime().getTime() : System.currentTimeMillis();
                pstmt.setTimestamp(1, new Timestamp(time));

                pstmt.setString(2, order.getStatus());

                if (order.getStaff() != null) {
                    pstmt.setString(3, order.getStaff().getUsername());
                } else {
                    pstmt.setNull(3, Types.VARCHAR);
                }

                try {
                    pstmt.setInt(4, Integer.parseInt(order.getTableId()));
                } catch (NumberFormatException e) {
                    pstmt.setInt(4, 0);
                }

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Tạo đơn hàng thất bại, không có dòng nào được thêm.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        order.setOrderId(generatedId);
                    } else {
                        throw new SQLException("Tạo đơn hàng thất bại, không lấy được ID.");
                    }
                }
            }

            // --- BƯỚC 2: INSERT VÀO BẢNG 'OrderItem' ---
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                    for (OrderItem item : order.getItems()) {
                        if (item.getMenuItem() != null) {
                            pstmtDetail.setInt(1, generatedId);

                            try {
                                int menuIdInt = Integer.parseInt(item.getMenuItem().getId());
                                pstmtDetail.setInt(2, menuIdInt);
                            } catch (NumberFormatException e) {
                                System.err.println("Bỏ qua món: ID món không hợp lệ (" + item.getMenuItem().getId() + ")");
                                continue;
                            }

                            pstmtDetail.setInt(3, item.getQuantity());
                            pstmtDetail.setString(4, item.getNote());

                            pstmtDetail.addBatch();
                        }
                    }
                    pstmtDetail.executeBatch();
                }
            }

            conn.commit();
            System.out.println("-> Đã lưu Order thành công. ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Lỗi Transaction SaveOrder: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return generatedId;
    }

}