package Interface;

import java.util.List;

public interface IOrderService {
    IOrder createOrder(String tableId, List<IOrderItem> items);
    IOrder getOrderById(String orderId);
    List<IOrder> getOrdersForToday();
    boolean completeOrder(String orderId);
}
