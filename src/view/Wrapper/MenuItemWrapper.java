package view.Wrapper;

import Interface.IMenuItem;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;

public class MenuItemWrapper {

    private final IMenuItem original;

    private final StringProperty id;
    private final StringProperty name;
    private final DoubleProperty price;
    private final StringProperty category;
    private final ObjectProperty<Image> image;

    public MenuItemWrapper(IMenuItem original) {
        this.original = original;

        this.id = new SimpleStringProperty(original.getId());
        this.name = new SimpleStringProperty(original.getName());
        this.price = new SimpleDoubleProperty(original.getPrice());
        this.category = new SimpleStringProperty(original.getCategory());
        if (original.getImagePath() != null) {
            this.image = new SimpleObjectProperty<>(loadImage(original.getImagePath()));
        } else {
            this.image = new SimpleObjectProperty<>(null);
        }

        // sync Wrapper → Model
        name.addListener((o, ov, nv) -> original.setName(nv));
        price.addListener((o, ov, nv) -> original.setPrice(nv.doubleValue()));
        category.addListener((o, ov, nv) -> original.setCategory(nv));
    }
    // Hàm phụ trợ để tải ảnh
    public Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        // 1. Ảnh trong resources
        if (imagePath.startsWith("/")) {
            URL url = getClass().getResource(imagePath);
            if (url == null) {
                System.err.println("Resource image not found: " + imagePath);
                return null;
            }
            return new Image(url.toExternalForm());
        }

        // 2. Ảnh upload (file hệ thống)
        File file = new File("storage", imagePath);
        if (!file.exists()) {
            System.err.println("Image not found: " + file.getAbsolutePath());
            return null;
        }

        return new Image(file.toURI().toString());
    }



    /* ================= GETTERS (Controller dùng) ================= */

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public Image getImage() { return image.get(); }
    public ObjectProperty<Image> imageProperty() { return image; }

    public String getImagePath() { return original.getImagePath(); }

    /* ================= SETTERS ================= */

    public void setId(String id) {
        this.id.set(id);
        original.setId(id);
    }

    public void setImagePath(String path) {
        original.setImagePath(path);
        image.set(path != null ? new Image("file:" + path, true) : null);
    }

    /* ================= SYNC ================= */

    public void updateFromOriginal() {
        id.set(original.getId());
        name.set(original.getName());
        price.set(original.getPrice());
        category.set(original.getCategory());

        if (original.getImagePath() != null) {
            image.set(new Image("file:" + original.getImagePath(), true));
        }
    }

    public IMenuItem unwrap() {
        return original;
    }
}