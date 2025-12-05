package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Order {
    private int orderId;
    private Date createdTime;
    private String status;
    private Staff staff;
    private String tableId;
    private List<OrderItem> items;

    public Order(int orderId, Staff staff, String tableId) {
        this.orderId = orderId;
        this.staff = staff;
        this.tableId = tableId;
        this.createdTime = new Date();
        this.status = "PENDING";  // "PENDING", "PREPARING", "SERVED", "CANCELLED"
        this.items = new ArrayList<>();
    }

    public String getCreatedAt() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(this.createdTime);
    }

    public double getTotalPrice() {
        double total = 0;
        if (items != null) {
            for (OrderItem item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
    }

    public String getOrderId() {
        return String.valueOf(orderId);
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}