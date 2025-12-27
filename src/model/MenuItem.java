package model;

import Interface.IMenuItem;
import javafx.scene.image.Image;

public class MenuItem implements IMenuItem {
    private String id;
    private String name;
    private double price;
    private String category;
    private Image image;

    public MenuItem(String id, String name, double price, String category, Image image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public double getPrice() { return price; }

    @Override
    public String getCategory() { return category; }

    @Override
    public Image getImage() { return image; }

    @Override
    public Image setImage(Image image) {
        this.image = image;
        return this.image;
    }

    @Override
    public void setId(String id) {
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
        this.category = category;
    }

    @Override
    public void updateFromOriginal() {

    }
}
