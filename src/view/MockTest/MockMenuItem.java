package view.MockTest;

import Interface.IMenuItem;
import javafx.scene.image.Image;

public class MockMenuItem implements IMenuItem {
    private String name;
    private String id;
    private String category;
    private double price;
    private String imagePath;
    private Image image;

    public MockMenuItem(String id, String name, Double price, String category){
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public MockMenuItem(String id, String name, Double price, String category, String imagePath){
        this(id, name, price, category);
        this.imagePath = imagePath;
    }
    public MockMenuItem(String id, String name, Double price, String category, Image image){
        this(id, name, price, category);
        this.image = image;
    }


    @Override
    public void setImagePath(String imp) {
        this.imagePath = imp;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getCategory() {
        return category;
    }
    public void setId(String id){
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public void setPrice(double price) {
this.price = price;
    }

    @Override
    public void setCategory(String category) {
this.category =category;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }
}
