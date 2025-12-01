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
}
