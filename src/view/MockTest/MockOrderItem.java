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
    public Image getImage(){
        return MenuItem.getImage();
    }
    public void setImage(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public String getMenuItemId() {
        return MenuItem.getId(); // từ MockMenuItem
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
    public Double getPrice() { return MenuItem.getPrice(); }
    public String getCategory() { return MenuItem.getCategory(); }
}
