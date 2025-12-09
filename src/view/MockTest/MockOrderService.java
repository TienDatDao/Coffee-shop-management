package view.MockTest;

import Interface.IOrder;
import Interface.IOrderItem;
import Interface.IOrderService;

import java.util.ArrayList;
import java.util.List;

public class MockOrderService implements IOrderService {
    private List<IOrder> orderList = new ArrayList<>();
    @Override
    public IOrder createOrder(String tableId, List<IOrderItem> items, String orderId) {
        MockOrder mockOrder = new MockOrder(tableId, items, orderId);
        orderList.add(mockOrder);
        return mockOrder;
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
    public List<IOrder> getOrderListFull(){
        return orderList;
    }

}
