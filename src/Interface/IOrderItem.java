package Interface;

/* Đại diện cho 1 dòng chi tiết order */
public interface IOrderItem {
    String getMenuItemId();     // id món trong menu
    int getQuantity();          // số lượng khách gọi
    double getSubtotal();       // quantity × price
}
