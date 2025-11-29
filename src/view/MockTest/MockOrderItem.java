package view.MockTest;

import Interface.IOrderItem;

import javafx.scene.image.Image;

public class MockOrderItem extends MockMenuItem implements IOrderItem {
    private int quantity;
    private double subtotal;
    private String menuItemId;
    private String name;
    private Double price;
    private String description;
    private String imagePath;

    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity){
        super(menuItemId, name, price, description);
        this.quantity = quantity;
    }
    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity, String imagePath){
        super(menuItemId, name, price, description, imagePath);
        this.quantity = quantity;
        this.imagePath = imagePath;
    }
    @Override
    public Image getImage(){
        return super.getImage();
    }
    public void setImage(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public String getMenuItemId() {
        return super.getId(); // từ MockMenuItem
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getSubtotal() {
        return getPrice()*getQuantity();
    }

    public String getName() { return super.getName(); }
    public double getPrice() { return super.getPrice(); }
    public String getCategory() { return super.getCategory(); }
    public String getImageUrl(){return null;}
}
