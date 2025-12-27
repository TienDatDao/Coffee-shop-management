package model;

import Interface.IMenuItem;

public class MenuItem implements IMenuItem {

    private String id;
    private String name;
    private double price;
    private String category;
    private String imagePath;

    public MenuItem(String id, String name, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    @Override public String getId() { return id; }
    @Override public void setId(String id) { this.id = id; }

    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }

    @Override public double getPrice() { return price; }
    @Override public void setPrice(double price) { this.price = price; }

    @Override public String getCategory() { return category; }
    @Override public void setCategory(String category) { this.category = category; }

    @Override public String getImagePath() { return imagePath; }
    @Override public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
