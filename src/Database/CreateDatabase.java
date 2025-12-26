package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class CreateDatabase {

    private static final String URL = "jdbc:sqlite:src/storage/coffee_shop.db";

    public static void main(String[] args) {
        // 1. Tạo thư mục nếu chưa tồn tại
        File storageDir = new File("src/storage");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        try (Connection conn = DriverManager.getConnection(URL)) {
            System.out.println("Ket noi SQLite thanh cong");

            Statement stmt = conn.createStatement();

            // --- Bật ràng buộc khóa ngoại cho SQLite (mặc định SQLite tắt tính năng này) ---
            stmt.execute("PRAGMA foreign_keys = ON;");

            // --- Lệnh 1: Tạo bảng MenuItem ---
            // Lưu ý: Đổi INT thành INTEGER PRIMARY KEY AUTOINCREMENT nếu bạn muốn id tự tăng
            String sqlMenuItem = """
                CREATE TABLE IF NOT EXISTS MenuItem (
                    menuId INTEGER PRIMARY KEY,
                    name VARCHAR(255),
                    price DOUBLE PRECISION,
                    category VARCHAR(100),
                    image VARCHAR(500)
                );
            """;
            stmt.execute(sqlMenuItem);
            System.out.println("-> Da tao bang MenuItem");

            // --- Lệnh 2: Tạo bảng Orders ---
            String sqlOrders = """
                CREATE TABLE IF NOT EXISTS Orders (
                    orderId INTEGER PRIMARY KEY,
                    createdTime DATE,
                    status VARCHAR(50),
                    staff VARCHAR(50),
                    tableId INT
                );
            """;
            stmt.execute(sqlOrders);
            System.out.println("-> Da tao bang Orders");

            // --- Lệnh 3: Tạo bảng OrderItem ---
            // Bảng này phải tạo CUỐI CÙNG vì nó tham chiếu khóa ngoại tới 2 bảng trên
            String sqlOrderItem = """
                CREATE TABLE IF NOT EXISTS OrderItem (
                    orderId INT,
                    orderItemId INTEGER,
                    orderMenuId INT,
                    quantity INT,
                    note TEXT,
                    FOREIGN KEY (orderId) REFERENCES Orders(orderId),
                    FOREIGN KEY (orderMenuId) REFERENCES MenuItem(menuId)
                );
            """;
            stmt.execute(sqlOrderItem);
            System.out.println("-> Da tao bang OrderItem");

            System.out.println("Tao tat ca cac bang thanh cong!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}