package Interface;
import java.util.List;

public interface IMenuService {
    List<IMenuItem> getAllItems();
    IMenuItem getItemById(String id);
    boolean addMenuItem(IMenuItem item);
    boolean updateMenuItem(IMenuItem item);
    boolean deleteMenuItem(String id);
}
