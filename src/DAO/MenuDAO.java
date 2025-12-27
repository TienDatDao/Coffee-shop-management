package DAO;

import Interface.IMenuItem;
import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    private final Connection conn;

    public MenuDAO(Connection conn) {
        this.conn = conn;
    }

    /* ================= READ ================= */

    public List<IMenuItem> getAll() {
        List<IMenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM MenuItem";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public IMenuItem getById(String id) {
        String sql = "SELECT * FROM MenuItem WHERE menuId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ================= CREATE ================= */

    public boolean insert(IMenuItem item) {
        String sql = """
                INSERT INTO MenuItem(menuId, name, price, category, imagePath)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getName());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getImagePath());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= UPDATE ================= */

    public boolean update(IMenuItem item) {
        String sql = """
                UPDATE MenuItem
                SET name = ?, price = ?, category = ?, imagePath = ?
                WHERE menuId = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getImagePath());
            ps.setString(5, item.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= DELETE ================= */

    public boolean delete(String id) {
        String sql = "DELETE FROM MenuItem WHERE menuId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= SEARCH ================= */

    public List<IMenuItem> searchByName(String keyword) {
        List<IMenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM MenuItem WHERE LOWER(name) LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= Mapper ================= */

    private IMenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(
                rs.getString("menuId"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("imagePath")
        );
    }
}