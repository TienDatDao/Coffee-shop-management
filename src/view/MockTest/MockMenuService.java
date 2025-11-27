package view.MockTest;

import Interface.IMenuItem;
import Interface.IMenuService;

import java.util.List;

public class MockMenuService implements IMenuService {

    @Override
    public List<IMenuItem> getAllItems() {
        return List.of(new MockMenuItem("1", "Coffee", 150000.0, "Delicious"),
                new MockMenuItem("2", "tea", 130000.0, "Delicious")
                );
    }

    @Override
    public IMenuItem getItemById(String id) {
        return new MockMenuItem(id, "Coffee", 150000.0, "Delicious");
    }

    @Override
    public boolean addMenuItem(IMenuItem item) {
        return true;
    }

    @Override
    public boolean updateMenuItem(IMenuItem item) {
        return true;
    }

    @Override
    public boolean deleteMenuItem(String id) {
        return true;
    }
}
