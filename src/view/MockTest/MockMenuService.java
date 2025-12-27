package view.MockTest;

import Interface.IMenuItem;
import Interface.IMenuService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockMenuService implements IMenuService {
    private List<IMenuItem> menuList;

    public MockMenuService() {
        menuList = new ArrayList<>();
        menuList.add(new MockMenuItem("1", "Cà phê đen", 25000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_den.png"));
        menuList.add(new MockMenuItem("2", "Cà phê sữa", 30000.0, "Drink", "/view/MainScreen/MainScreenImages/ca_phe_sua.png"));
        menuList.add(new MockMenuItem("3", "Cappuchino", 35000.0, "Drink", "/view/MainScreen/MainScreenImages/cappuchino.png"));
        menuList.add(new MockMenuItem("4", "Bạc xỉu", 35000.0, "Drink", "/view/MainScreen/MainScreenImages/bac_xiu.png"));
        menuList.add(new MockMenuItem("5", "Trà chanh", 25000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_chanh.png"));
        menuList.add(new MockMenuItem("6", "Trà đào", 40000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_dao.png"));
        menuList.add(new MockMenuItem("7", "Trà vải", 30000.0, "Drink", "/view/MainScreen/MainScreenImages/tra_vai.png"));
        menuList.add(new MockMenuItem("8", "Bánh Croissant", 20000.0, "Food", "/view/MainScreen/MainScreenImages/banh_croissant.png"));
        menuList.add(new MockMenuItem("9", "Bánh Tiramisu", 45000.0, "Food", "/view/MainScreen/MainScreenImages/banh_tiramisu.png"));
        menuList.add(new MockMenuItem("10", "Bánh Flan", 40000.0, "Food", "/view/MainScreen/MainScreenImages/banh_flan.png"));
        menuList.add(new MockMenuItem("11", "Bánh Muffin", 40000.0, "Food", "/view/MainScreen/MainScreenImages/banh_muffin.png"));
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

    @Override
    public boolean updateMenuItem(IMenuItem item) {
        if (item == null || item.getId() == null) return false;

        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getId().equals(item.getId())) {
                menuList.set(i, item); // Cập nhật item
                return true;
            }
        }
        return false; // Không tìm thấy item
    }

    @Override
    public boolean deleteMenuItem(String id) {
        return menuList.removeIf(item -> item.getId().equals(id));
    }
    public List<IMenuItem> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllItems(); // Trả lại toàn bộ menu
        }
        String lower = keyword.toLowerCase();
        return getAllItems().stream()
                .filter(item -> item.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

}