package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class CreateDatabase {

    private static final String URL = "jdbc:sqlite:storage/coffee_shop.db";
    private static Connection connection;

    // Lấy connection dùng cho toàn app
    public static Connection getConnection() {
        if (connection == null) {
            initDatabase();
        }
        return connection;
    }

    // Khởi tạo DB + bảng
    private static void initDatabase() {
        try {
            // tạo thư mục storage nếu chưa có
            File storageDir = new File("storage");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            connection = DriverManager.getConnection(URL);
            Statement stmt = connection.createStatement();

            stmt.execute("PRAGMA foreign_keys = ON;");

            String sqlMenuItem = """
                CREATE TABLE IF NOT EXISTS MenuItem (
                    menuId TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    price REAL NOT NULL,
                    category TEXT,
                    imagePath TEXT
                );
            """;

            String sqlOrders = """
                CREATE TABLE IF NOT EXISTS Orders (
                    orderId TEXT PRIMARY KEY,
                    createdTime TEXT NOT NULL,
                    status TEXT,
                    staff TEXT,
                    tableId INTEGER
                );
            """;

            String sqlOrderItem = """
                        CREATE TABLE IF NOT EXISTS OrderItem (
                                    orderItemId TEXT PRIMARY KEY,
                                    orderId TEXT,
                                    orderMenuId TEXT NOT NULL,
                                    quantity INTEGER NOT NULL,
                                    note TEXT,
                                    FOREIGN KEY (orderId) REFERENCES Orders(orderId) ON DELETE CASCADE,
                                    FOREIGN KEY (orderMenuId) REFERENCES MenuItem(menuId) ON DELETE CASCADE
                                );
                    """;

            stmt.execute(sqlMenuItem);
            stmt.execute(sqlOrders);
            stmt.execute(sqlOrderItem);

            System.out.println("Database san sang");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}