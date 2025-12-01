package view.Wrapper;

import Interface.IMenuItem;
import Interface.IOrderItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class OrderItemWrapper implements IOrderItem {
    private IMenuItem menuItem;
    private SimpleStringProperty name;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty price;
    private SimpleDoubleProperty subtotal;
    private String size; // S, M, L (mặc định M cho ví dụ này)

    public OrderItemWrapper(IMenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.size = "M"; // Mặc định, logic chọn size sẽ ở màn hình 2
        this.name = new SimpleStringProperty(menuItem.getName());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(menuItem.getPrice());
        this.subtotal = new SimpleDoubleProperty(calculateSubtotal());

        // Listener: Khi số lượng thay đổi, tự động tính lại thành tiền
        this.quantity.addListener((obs, oldVal, newVal) -> {
            this.subtotal.set(calculateSubtotal());
        });
    }

    private double calculateSubtotal() {
        return this.quantity.get() * this.price.get();
    }

    public void increaseQuantity() {
        this.quantity.set(this.quantity.get() + 1);
    }

    public void decreaseQuantity() {
        if (this.quantity.get() > 1) {
            this.quantity.set(this.quantity.get() - 1);
        }
    }

    // Property Getters cho TableView
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleDoubleProperty subtotalProperty() { return subtotal; }

    public IMenuItem getMenuItem() { return menuItem; }

    @Override
    public String getId() {
        return menuItem.getId();
    }

    @Override
    public String getMenuItemId() {
        return menuItem.getId();
    }

    @Override
    public int getQuantity() {
        return quantity.get();
    }

    @Override
    public double getSubtotal() {
        return subtotal.get();
    }

    @Override
    public String getName() {
        return name.getName();
    }

    @Override
    public double getPrice() {
        return price.get();
    }

    @Override
    public String getCategory() {
        return menuItem.getCategory();
    }

    @Override
    public Image getImage() {
        return menuItem.getImage();
    }
}