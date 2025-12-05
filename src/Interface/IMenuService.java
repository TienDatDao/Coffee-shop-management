package Interface;
import java.util.List;

/* Dịch vụ xử lý menu (thêm/xóa/sửa/lấy món) */
public interface IMenuService {
    List<IMenuItem> getAllItems();
    IMenuItem getItemById(String id);
    boolean addMenuItem(IMenuItem item);
    boolean updateMenuItem(IMenuItem item);
    boolean deleteMenuItem(String id);
    List<IMenuItem> search(String keyword);
}

/*
Lấy tất cả món để hiển thị lên UI
Tìm món bằng Id
Thêm món mới
Sửa món
Xóa món
*/