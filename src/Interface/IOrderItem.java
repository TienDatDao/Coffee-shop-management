package Interface;

import javafx.scene.image.Image;

/* Đại diện cho 1 dòng chi tiết order */
public interface IOrderItem {
    String getId();
    Double getPrice();
    String getName();
    String getCategory();
    Image getImage();
    String getMenuItemId();     // id món trong menu
    int getQuantity();          // số lượng khách gọi
    double getSubtotal();       // quantity × price
}
