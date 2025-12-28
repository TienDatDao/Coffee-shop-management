package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class SeedItem {
    String id;
    String name;
    double price;
    String category;
    String imagePath;

    public SeedItem(String id, String name, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }
}

public class DataSeeder {

    // Đường dẫn DB của bạn (Sửa lại cho khớp với cấu hình hiện tại)
    private static final String DB_URL = "jdbc:sqlite:storage.coffee_shop.db";

    public static void seedMenuData() {
        // 1. Danh sách dữ liệu mẫu
        List<SeedItem> menuList = new ArrayList<>();
        menuList.add(new SeedItem("1", "Cà phê đen", 25000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_den.png"));
        menuList.add(new SeedItem("2", "Cà phê sữa", 30000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_sua.png"));
        menuList.add(new SeedItem("3", "Cappuchino", 35000.0, "Drink", "/view/MainScreen/MainScreenImages/cappuchino.png"));
        menuList.add(new SeedItem("4", "Bạc xỉu", 35000.0, "Drink", "/view/MainScreen/MainScreenImages/bac_xiu.png"));
        menuList.add(new SeedItem("5", "Trà chanh", 25000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_chanh.png"));
        menuList.add(new SeedItem("6", "Trà đào", 40000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_dao.png"));
        menuList.add(new SeedItem("7", "Trà vải", 30000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_vai.png"));
        menuList.add(new SeedItem("8", "Bánh Croissant", 20000.0, "Food", "/view/MainScreen/MainScreenImages/banh_croissant.png"));
        menuList.add(new SeedItem("9", "Bánh Tiramisu", 45000.0, "Food", "/view/MainScreen/MainScreenImages/banh_tiramisu.png"));
        menuList.add(new SeedItem("10", "Bánh Flan", 40000.0, "Food", "/view/MainScreen/MainScreenImages/banh_flan.png"));
        menuList.add(new SeedItem("11", "Bánh Muffin", 40000.0, "Food", "/view/MainScreen/MainScreenImages/banh_muffin.png"));

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                // 2. Tạo bảng nếu chưa có (Phòng hờ)
                createTableIfNotExists(conn);

                // 3. Kiểm tra xem bảng đã có dữ liệu chưa
                if (isTableEmpty(conn)) {
                    System.out.println("⚠️ Database trống. Đang khởi tạo dữ liệu mẫu...");
                    insertData(conn, menuList);
                    System.out.println("✅ Đã thêm " + menuList.size() + " món vào Database!");
                } else {
                    System.out.println("ℹ️ Database đã có dữ liệu. Bỏ qua bước khởi tạo.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi Database Seeder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm tạo bảng (Sửa câu SQL này khớp với cột trong DB thực tế của bạn)
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS MenuItem ("
                + "menuId TEXT,"
                + "name TEXT NOT NULL,"
                + "price REAL,"
                + "category TEXT,"
                + "imagePath TEXT"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Hàm kiểm tra bảng có trống không
    private static boolean isTableEmpty(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MenuItem";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0; // Trả về true nếu count = 0
            }
        }
        return true;
    }

    // Hàm chèn dữ liệu
    private static void insertData(Connection conn, List<SeedItem> list) throws SQLException {
        String sql = "INSERT INTO MenuItem(menuId, name, price, category, imagePath) VALUES(?, ?, ?, ?, ?)";

        // Tắt auto-commit để chèn nhanh hơn (Transaction)
        conn.setAutoCommit(false);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (SeedItem item : list) {
                pstmt.setString(1, item.id);
                pstmt.setString(2, item.name);
                pstmt.setDouble(3, item.price);
                pstmt.setString(4, item.category);
                pstmt.setString(5, item.imagePath);

                pstmt.addBatch(); // Gom lệnh lại
            }
            pstmt.executeBatch(); // Thực thi một lần
            conn.commit(); // Lưu thay đổi
        } catch (SQLException e) {
            conn.rollback(); // Hoàn tác nếu lỗi
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}