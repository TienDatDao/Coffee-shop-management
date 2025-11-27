package view.MockTest;

import Interface.IMenuItem;

public class MockMenuItem implements IMenuItem {
    private String name;
    private String id;
    private String description;
    private double price;
    public MockMenuItem(String id, String name, Double price, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public double getPrice() {
        return 0;
    }

    @Override
    public String getCategory() {
        return null;
    }

}
