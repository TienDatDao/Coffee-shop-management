package view.MockTest;

import Interface.IOrder;
import Interface.IOrderItem;
import Interface.IOrderService;

import java.util.List;

public class MockOrderService implements IOrderService {
    @Override
    public IOrder createOrder(String tableId, List<IOrderItem> items, String orderId) {
        return new MockOrder(tableId, items, orderId);
    }

    public IOrder createOrder(List<IOrderItem> items, String orderId) {
        return new MockOrder( items, orderId);
    }

    @Override
    public IOrder getOrderById(String orderId) {
        return null;
    }

    @Override
    public List<IOrder> getOrdersForToday() {
        return null;
    }

}
