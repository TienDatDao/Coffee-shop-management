package view.MockTest;

import Interface.IOrder;
import Interface.IOrderItem;

import java.util.List;

public class MockOrder implements IOrder  {
    private String orderId;
    private String tableId;
    private boolean status;
    private List<IOrderItem> IOrderItemList;
    public MockOrder(){
    }
    public MockOrder(String tableId, List<IOrderItem> IOrderItemList, String orderId){
        this.IOrderItemList = IOrderItemList;
        this.tableId = tableId;
        this.orderId = orderId;
        this.status =false;
    }
    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getTableId() {
        return tableId;
    }

    @Override
    public List<IOrderItem> getItems() {
        return List.of(new MockOrderItem("1", "cf", 140000.0, "delicious", 2, "/view/LoginPage/LoginLogo.png"),
                new MockOrderItem("1", "cf", 140000.0, "delicious", 2,  "/view/LoginPage/LoginLogo.png"),
                new MockOrderItem("1", "cf", 140000.0, "delicious", 2, "/view/LoginPage/LoginLogo.png"),
                new MockOrderItem("1", "cf", 140000.0, "delicious", 2,  "/view/LoginPage/LoginLogo.png"),
                new MockOrderItem("1", "cf", 140000.0, "delicious", 2, "/view/LoginPage/LoginLogo.png"),
                new MockOrderItem("1", "cf", 140000.0, "delicious", 2, "/view/LoginPage/LoginLogo.png")
                );
    }

    @Override
    public double getTotalPrice() {
        double sum = 0.0;
        for(IOrderItem iOrderItem: getItems()){
            sum+= iOrderItem.getSubtotal();
        }
        return sum;
    }

    @Override
    public String getCreatedAt() {
        return null;
    }

    @Override
    public boolean completeOrder() {
        return true;
    }
}
