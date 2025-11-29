package Interface;

import javafx.scene.image.Image;

/* Đại diện cho 1 dòng chi tiết order */
public interface IOrderItem extends IMenuItem {
    String getMenuItemId();     // id món trong menu
    int getQuantity();          // số lượng khách gọi
    double getSubtotal();       // quantity × price
    public String getName();
    public double getPrice();
    public String getCategory();
    public Image getImage();
}
