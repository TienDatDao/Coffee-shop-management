package service;

import DAO.MenuDAO;
import Database.CreateDatabase;
import Interface.IMenuItem;
import Interface.IMenuService;

import java.sql.Connection;
import java.util.List;

public class MenuService implements IMenuService {

    private final MenuDAO dao;
    private static final MenuService INSTANCE =
            new MenuService(CreateDatabase.getConnection());

    public static MenuService getInstance() {
        return INSTANCE;
    }

    public MenuService(Connection conn) {
        dao = new MenuDAO(conn);
    }

    @Override
    public List<IMenuItem> getAllItems() {
        return dao.getAll();
    }

    @Override
    public IMenuItem getItemById(String id) {
        return dao.getById(id);
    }

    @Override
    public boolean addMenuItem(IMenuItem item) {
        return dao.insert(item);
    }

    @Override
    public boolean updateMenuItem(IMenuItem item) {
        return dao.update(item);
    }

    @Override
    public boolean deleteMenuItem(String id) {
        return dao.delete(id);
    }

    @Override
    public List<IMenuItem> search(String keyword) {
        return dao.searchByName(keyword);
    }
}