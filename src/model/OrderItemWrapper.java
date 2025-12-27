package model;

import Interface.IMenuItem;
import Interface.IOrderItem;
import javafx.beans.property.*;
import javafx.scene.image.Image;

public class OrderItemWrapper implements IOrderItem {
    // Giữ lại tham chiếu tới MenuItem gốc để lấy thông tin
    private IMenuItem menuItem;

    private SimpleStringProperty name;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty subtotal;

    public OrderItemWrapper(IMenuItem menuItem, int quantity) {
        this.menuItem = menuItem;

        this.name = new SimpleStringProperty(menuItem.getName());
        this.quantity = new SimpleIntegerProperty(quantity);
        // Tính subtotal ban đầu
        this.subtotal = new SimpleDoubleProperty(menuItem.getPrice() * quantity);

        // Listener tính lại tiền khi số lượng đổi
        this.quantity.addListener((obs, oldVal, newVal) -> {
            this.subtotal.set(newVal.intValue() * menuItem.getPrice());
        });
    }

    // --- LOGIC RIÊNG CHO UI (JAVA FX PROPERTIES) ---
    public void increaseQuantity() { this.quantity.set(this.quantity.get() + 1); }
    public void decreaseQuantity() {
        if(this.quantity.get() > 0) this.quantity.set(this.quantity.get() - 1);
    }


    public SimpleStringProperty nameProperty() { return name; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleDoubleProperty subtotalProperty() { return subtotal; }

    // --- THỰC THI INTERFACE IOrderItem ---
    @Override
    public String getMenuItemId() { return menuItem.getId(); }
    public IMenuItem getMenuItem(){
        return menuItem;
    }

    @Override
    public int getQuantity() { return quantity.get(); }

    @Override
    public double getSubtotal() { return subtotal.get(); }

    // --- THỰC THI INTERFACE IMenuItem (Do IOrderItem kế thừa IMenuItem) ---
    // Chúng ta ủy quyền (delegate) lại cho đối tượng menuItem gốc trả lời
    @Override public String getId() { return menuItem.getId(); }
    @Override public String getName() { return menuItem.getName(); }
    @Override public double getPrice() { return menuItem.getPrice(); }
    @Override public String getCategory() { return menuItem.getCategory(); }
    @Override public Image getImage() { return menuItem.getImage(); }

    @Override
    public Image setImage(Image image) {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setPrice(double price) {

    }

    @Override
    public void setCategory(String category) {

    }

    @Override
    public void updateFromOriginal() {

    }
}