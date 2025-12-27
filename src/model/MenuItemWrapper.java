package model;

import Interface.IMenuItem; // Import interface
import javafx.scene.image.Image;

public class MenuItemWrapper implements IMenuItem {
    private String id;
    private String name;
    private double price;
    private String category;
    private String imagePath;

    // Constructor giữ nguyên
    public MenuItemWrapper(String id, String name, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // --- CÁC HÀM CỦA INTERFACE ---
    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public double getPrice() { return price; }

    @Override
    public String getCategory() { return category; }

    @Override
    public Image getImage() {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                return new Image(getClass().getResourceAsStream(imagePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Hoặc trả về ảnh mặc định
    }

    @Override
    public Image setImage(Image image) {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setPrice(double price) {

    }

    @Override
    public void setCategory(String category) {

    }

    @Override
    public void updateFromOriginal() {

    }

    // Hàm phụ riêng của class này (nếu cần)
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() { return name; }
}