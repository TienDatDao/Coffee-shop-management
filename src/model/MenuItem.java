package model;

import Interface.IMenuItem; // Import interface
import javafx.scene.image.Image;

// THÊM: implements IMenuItem
public class MenuItem implements IMenuItem {
    private String id;
    private String name;
    private double price;
    private String category;
    private String imagePath;

    // Constructor giữ nguyên
    public MenuItem(String id, String name, double price, String category, String imagePath) {
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

    // Xử lý logic load ảnh ở đây để khớp với Interface
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

    // Hàm phụ riêng của class này (nếu cần)
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() { return name; }
}