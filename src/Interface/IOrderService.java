package Interface;

import java.util.List;

/* Dịch vụ xử lý order và thanh toán */
public interface IOrderService {
    IOrder createOrder(String tableId, List<IOrderItem> items, String orderId); // tạo hóa đơn khi khách gọi món
    IOrder getOrderById(String orderId);                        // xem hóa đơn
    List<IOrder> getOrdersForToday();                           // xem danh sách hóa đơn trong ngày
    boolean completeOrder(String orderId);                      // đánh dấu đã thanh toán
}
