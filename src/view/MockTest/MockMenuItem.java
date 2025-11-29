package view.MockTest;

import Interface.IMenuItem;
import javafx.scene.image.Image;

public class MockMenuItem implements IMenuItem {
    private String name;
    private String id;
    private String description;
    private double price;
    private String imagePath;

    public MockMenuItem(String id, String name, Double price, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public MockMenuItem(String id, String name, Double price, String description, String imagePath){
        this(id, name, price, description);
        this.imagePath = imagePath;
    }

    public Image getImage() {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Load từ resources (ưu tiên)
        var url = getClass().getResource(imagePath);
        if (url != null) {
            return new Image(url.toExternalForm());
        }

        // Load từ đường dẫn file tuyệt đối
        return new Image("file:" + imagePath);
    }

    public void setImage(String imagePath){
        this.imagePath = imagePath;
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
        return description;
    }
}
