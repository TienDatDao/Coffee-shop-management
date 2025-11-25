package Interface;
import java.util.List;

public interface IOrder {
    String getOrderId();
    String getTableId();
    List<IOrderItem> getItems();
    double getTotalPrice();
    String getCreatedAt();
}