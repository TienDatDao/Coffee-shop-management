package view.Wrapper;

import Interface.IMenuItem;
import Interface.IOrderItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;

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
    public SimpleDoubleProperty priceProperty() { return price; }

    @Override
    public Image getImage() {
        String imagePath = menuItem.getImagePath();
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        // 1. Ảnh trong resources
        if (imagePath.startsWith("/")) {
            URL url = getClass().getResource(imagePath);
            if (url == null) {
                System.err.println("Resource image not found: " + imagePath);
                return null;
            }
            return new Image(url.toExternalForm());
        }

        // 2. Ảnh upload (file hệ thống)
        File file = new File("storage", imagePath);
        if (!file.exists()) {
            System.err.println("Image not found: " + file.getAbsolutePath());
            return null;
        }

        return new Image(file.toURI().toString());
    }


    @Override
    public void setImagePath(String imp) {
        this.menuItem.setImagePath(imp);
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
    public String getImagePath() {
        return menuItem.getImagePath();
    }
}