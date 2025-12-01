package view.MockTest;

import Interface.IMenuService;
import Interface.IMenuItem;
import java.util.ArrayList;
import java.util.List;

public class MockMenuService implements IMenuService {
    private List<IMenuItem> menuList;

    public MockMenuService() {
        menuList = new ArrayList<>();
        menuList.add(new MockMenuItem("1", "Cà phê đen", 25000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_den.png"));
        menuList.add(new MockMenuItem("2", "Cà phê sữa", 30000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_sua.png"));
        menuList.add(new MockMenuItem("3", "Bạc xỉu", 35000.0, "Drink", "/view/MainScreen/MainScreenImages/bac_xiu.png"));
        menuList.add(new MockMenuItem("4", "Trà đào", 40000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_dao.png"));
        menuList.add(new MockMenuItem("5", "Bánh Croissant", 20000.0, "Food", "/view/MainScreen/MainScreenImages/banh_croissant.png"));
        menuList.add(new MockMenuItem("6", "Bánh Tiramisu", 45000.0, "Food", "/view/MainScreen/MainScreenImages/banh_tiramisu.png"));

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