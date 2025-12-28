package view.MockTest;

import Interface.IMenuItem;
import Interface.IOrderItem;

import javafx.scene.image.Image;

public class MockOrderItem implements IOrderItem {
    IMenuItem MenuItem;
    private int quantity;
    private double subtotal;
    private String menuItemId;
    private String name;
    private Double price;
    private String description;
    private String imagePath;
    public MockOrderItem(IMenuItem MenuItem, int quantity){
        this.MenuItem = MenuItem;
        this.quantity = quantity;
    }
    public MockOrderItem(IMenuItem MenuItem){
        this.MenuItem = MenuItem;
    }

    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity, String imagePath){
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    @Override
    public void setImagePath(String imp) {
        this.MenuItem.setImagePath(imp);
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
        return null;
    }

    public void setImage(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getSubtotal() {
        return getPrice()*getQuantity();
    }

    @Override
    public String getId() {
        return null;
    }

    public String getName() { return MenuItem.getName(); }
    public double getPrice() { return MenuItem.getPrice(); }
    public String getCategory() { return MenuItem.getCategory(); }

    @Override
    public Image getImage() {
        return new Image(MenuItem.getImagePath());
    }
}