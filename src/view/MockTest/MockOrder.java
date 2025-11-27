package view.MockTest;

import Interface.IOrder;
import Interface.IOrderItem;

import java.util.List;

public class MockOrder implements IOrder {
    private String orderId;
    private String tableId;
    private List<IOrderItem> IOrderItemList;
    public MockOrder(String tableId, List<IOrderItem> IOrderItemList, String orderId){
        this.IOrderItemList = IOrderItemList;
        this.tableId = tableId;
        this.orderId = orderId;
    }
    @Override
    public String getOrderId() {
        return "1";
    }

    @Override
    public String getTableId() {
        return "1";
    }

    @Override
    public List<IOrderItem> getItems() {
        return List.of(new MockOrderItem("1", 2, "none"),
                new MockOrderItem("2", 3, "none")
                );
    }

    @Override
    public double getTotalPrice() {
        return 0;
    }

    @Override
    public String getCreatedAt() {
        return null;
    }
}
