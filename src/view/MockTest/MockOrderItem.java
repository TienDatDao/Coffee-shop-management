package view.MockTest;

import Interface.IMenuItem;
import Interface.IOrderItem;
import javafx.scene.image.Image;

public class MockOrderItem implements IOrderItem {

    private IMenuItem MenuItem;
    private int quantity;
    private String imagePath;

    // Constructor chuẩn dùng MenuItem thật
    public MockOrderItem(IMenuItem MenuItem, int quantity){
        this.MenuItem = MenuItem;
        this.quantity = quantity;
    }

    public MockOrderItem(IMenuItem MenuItem){
        this.MenuItem = MenuItem;
        this.quantity = 1;
    }

    // Constructor dùng mock data → tự tạo MockMenuItem
    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity, String imagePath){
        this.MenuItem = new MockMenuItem(menuItemId, name, price, description, imagePath);
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
        return MenuItem.getId();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getSubtotal() {
        return getPrice() * getQuantity();
    }

    @Override
    public String getId() {
        return MenuItem.getId();
    }

    public String getName() {
        return MenuItem.getName();
    }

    public double getPrice() {
        return MenuItem.getPrice();
    }

    public String getCategory() {
        return MenuItem.getCategory();
    }
}
