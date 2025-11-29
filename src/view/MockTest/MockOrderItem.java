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

    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity, double subtotal){
        super(menuItemId, name, price, description);
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    public MockOrderItem(String menuItemId, String name, Double price, String description, int quantity, double subtotal, String imagePath){
        super(menuItemId, name, price, description, imagePath);
        this.quantity = quantity;
        this.subtotal = subtotal;
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
        return subtotal;
    }

    public String getName() { return super.getName(); }
    public double getPrice() { return super.getPrice(); }
    public String getCategory() { return super.getCategory(); }
    public String getImageUrl(){return null;}
}
