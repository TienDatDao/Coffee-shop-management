package Interface;

import java.time.LocalDateTime;
import java.util.List;

/* Đại diện cho 1 hóa đơn/order */
public interface IOrder {
    String getOrderId();            // số hóa đơn
    String getTableId();            // số bàn
    List<IOrderItem> getItems();    // danh sách order items
    double getTotalPrice();         // tổng tiền cuối cùng
    String getCreatedAt();          // thời gian order
    boolean completeOrder();                      // đánh dấu đã thanh toán với một đơn hàng
    void setListOrderItem(IOrderItem item);
    LocalDateTime getLocalDateTime();
}