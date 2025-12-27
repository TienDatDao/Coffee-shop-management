package model;

public class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private String note;

    public OrderItem(MenuItem menuItem, int quantity, String note) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.note = note;
    }

    public String getName() {
        return menuItem != null ? menuItem.getName() : "Unknown";
    }

    public double getPrice() {
        return menuItem != null ? menuItem.getPrice() : null;
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
}