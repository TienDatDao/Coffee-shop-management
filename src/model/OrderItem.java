package model;

public class OrderItem {
    private int orderItemId;
    private MenuItem menuItem;
    private int quantity;
    private String note;

    public OrderItem() {}

    public OrderItem(MenuItem menuItem, int quantity, String note, int orderItemId) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.note = note;
        this.orderItemId = orderItemId;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getName() {
        return menuItem != null ? menuItem.getName() : "Unknown";
    }

    public double getPrice() {
        return menuItem != null ? menuItem.getPrice() : 0.0;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNote() {
        return note;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}