package Interface;

import javafx.scene.image.Image;

/* Đại diện cho 1 dòng chi tiết order */
public interface IOrderItem {
    int getQuantity();
    double getSubtotal();
    String getId();
    double getPrice();
    String getName();
    String getCategory();
    Image getImage();
}
