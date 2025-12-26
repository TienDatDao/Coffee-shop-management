import java.sql.*;

public class Test {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:src/storage/coffee_shop.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Ket noi DB thanh cong");

            String sql = """
                INSERT INTO MenuItem (menuid, name, price, category, image) VALUES
                (1, 'Cà phê đen', 20000, 'Coffee', 'ca_phe_den.jpg'),
                (2, 'Cà phê sữa', 25000, 'Coffee', 'ca_phe_sua.jpg'),
                (3, 'Bạc xỉu', 30000, 'Coffee', 'bac_xiu.jpg');
            """;

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}