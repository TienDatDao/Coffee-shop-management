package view.MainScreen;

public class MenuItem {
    private String id;
    private String name;
    private double price;
    private String category; // "Drink" hoặc "Food"
    private String imagePath; // URL hoặc đường dẫn local

    public MenuItem(String id, String name, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Getters và Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() {
        return name;
    }
}
