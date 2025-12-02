package Interface;

import javafx.scene.image.Image;

/* Đại diện cho 1 dòng chi tiết order */
public interface IOrderItem {
    String getMenuItemId();     // id món trong menu
    int getQuantity();          // số lượng khách gọi
    double getSubtotal();       // quantity × price
    String getName();
    double getPrice();
    String getCategory();
    Image getImage(); // bổ sung thêm thuộc tính ảnh của món
}
