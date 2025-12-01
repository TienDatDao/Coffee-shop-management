package service;

import Interface.IMenuItem;
import Interface.IMenuService;
import model.MenuItem;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class MenuService implements IMenuService {
    private List<IMenuItem> menuList = new ArrayList<>();

    public MenuService() {
        loadDefaultMenu();
    }

    private void loadDefaultMenu() {
        menuList.add(new MenuItem("CF01", "Cà phê sữa", 25000, "coffee",
                new Image("file:images/cf_sua.png")));

        menuList.add(new MenuItem("CF02", "Cà phê đen", 20000, "coffee",
                new Image("file:images/cf_den.png")));

        menuList.add(new MenuItem("TE01", "Trà đào", 30000, "tea",
                new Image("file:images/tra_dao.png")));
    }

    @Override
    public List<IMenuItem> getAllItems() {
        return menuList;
    }

    @Override
    public IMenuItem getItemById(String id) {
        return menuList.stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean addMenuItem(IMenuItem item) {
        return menuList.add(item);
    }

    @Override
    public boolean updateMenuItem(IMenuItem item) {
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getId().equals(item.getId())) {
                menuList.set(i, item);
                return true; // cập nhật thành công
            }
        }
        return false; // không tìm thấy item để update
    }


    @Override
    public boolean deleteMenuItem(String id) {
        return menuList.removeIf(item -> item.getId().equals(id));
    }
}
