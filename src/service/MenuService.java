package service;

import Interface.IMenuService;
import Interface.IMenuItem;
import model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuService implements IMenuService {
    private List<IMenuItem> menuList;

    public MenuService() {
        menuList = new ArrayList<>();
        menuList.add(new MenuItem("1", "Cà phê đen", 25000, "Drink", "/view/MainScreen/images/ca_phe_den.png"));
        menuList.add(new MenuItem("2", "Cà phê sữa", 30000, "Drink", "/view/MainScreen/images/ca_phe_sua.png"));
        menuList.add(new MenuItem("3", "Bạc xỉu", 35000, "Drink", "/view/MainScreen/images/bac_xiu.png"));
        menuList.add(new MenuItem("4", "Trà đào", 40000, "Drink", "/view/MainScreen/images/tra_dao.png"));
        menuList.add(new MenuItem("5", "Bánh Croissant", 20000, "Food", "/view/MainScreen/images/banh_croissant.png"));
        menuList.add(new MenuItem("6", "Bánh Tiramisu", 45000, "Food", "/view/MainScreen/images/banh_tiramisu.png"));

    }

    @Override
    public List<IMenuItem> getAllItems() {
        return menuList; // Trả về danh sách cho Controller
    }

    @Override
    public IMenuItem getItemById(String id) {
        return menuList.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean addMenuItem(IMenuItem item) {
        return menuList.add(item);
    }

    // Các hàm update/delete bạn có thể để trống (return false) nếu chưa dùng tới
    @Override public boolean updateMenuItem(IMenuItem item) { return false; }
    @Override public boolean deleteMenuItem(String id) { return false; }
}