package model;

import Interface.IOrder;
import Interface.IOrderItem;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Order implements IOrder {
    private int orderId; // Database dùng int
    private Date createdTime;
    private String status;
    private Staff staff;
    private String tableId;

    // Quan trọng: Sử dụng Interface IOrderItem trong List
    private List<IOrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
        this.createdTime = new Date();
        this.status = "PENDING";
    }

    // --- IMPLEMENTS IOrder ---

    @Override
    public String getOrderId() {
        // Chuyển int sang String cho đúng Interface
        return String.valueOf(orderId);
    }

    @Override
    public String getTableId() {
        return tableId;
    }

    @Override
    public List<IOrderItem> getItems() {
        return items;
    }

    @Override
    public double getTotalPrice() {
        double total = 0;
        if (items != null) {
            for (IOrderItem item : items) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    @Override
    public String getCreatedAt() {
        if (this.createdTime == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(this.createdTime);
    }

    @Override
    public boolean completeOrder() {
        this.status = "PAID"; // Hoặc trạng thái hoàn thành tương ứng
        return true;
    }

    @Override
    public void setListOrderItem(IOrderItem item) {
        // Hàm này trong interface tên hơi lạ (setList nhưng tham số là 1 item)
        // Mình sẽ hiểu nó là "addOrderItem"
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    // --- SETTERS & GETTERS KHÁC (Hỗ trợ DAO/Model) ---

    // Setter dùng int để DAO map dữ liệu
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
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

    // Setter để gán cả list (dùng trong DAO)
    public void setItems(List<IOrderItem> items) {
        this.items = items;
    }
}