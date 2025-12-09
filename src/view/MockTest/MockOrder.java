package view.MockTest;

import Interface.IOrder;
import Interface.IOrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockOrder implements IOrder  {
    private String orderId;
    private String tableId;
    private boolean status;
    private List<IOrderItem> IOrderItemList  = new ArrayList<>();
    private LocalDateTime localDateTime;
    public String randomId(){
        int length = 12; // độ dài chuỗi muốn tạo
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        String randomString = sb.toString();
        return randomString;
    }
    public MockOrder(){
        this.localDateTime = LocalDateTime.now();
    }
    public MockOrder(String tableId, List<IOrderItem> IOrderItemList, String orderId){
        this.IOrderItemList = IOrderItemList;
        this.tableId = tableId;
        this.orderId = orderId;
        localDateTime = LocalDateTime.now();
        this.status =false;
    }
    public MockOrder(List<IOrderItem> IOrderItemList, String orderId){
        this.IOrderItemList = IOrderItemList;
        this.orderId = orderId;
        localDateTime = LocalDateTime.now();
        this.status =false;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
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
        return this.IOrderItemList;
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

    @Override
    public void setListOrderItem(IOrderItem item) {
        this.IOrderItemList.add(item);
    }
}
