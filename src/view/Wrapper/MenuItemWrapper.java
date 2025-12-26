package view.Wrapper;

import Interface.IMenuItem;
import javafx.beans.property.*;
import javafx.scene.image.Image;

public class MenuItemWrapper implements IMenuItem{
    private final IMenuItem original; // Tham chiếu đến object gốc
    private final StringProperty id;
    private final StringProperty name;
    private final DoubleProperty price;
    private final StringProperty category;
    private final ObjectProperty<Image> image;

    public MenuItemWrapper(IMenuItem original) {
        this.original = original;

        // 1. Khởi tạo Properties với giá trị gốc
        this.id = new SimpleStringProperty(original.getId());
        this.name = new SimpleStringProperty(original.getName());
        this.price = new SimpleDoubleProperty(original.getPrice());
        this.category = new SimpleStringProperty(original.getCategory());
        this.image = new SimpleObjectProperty<>(original.getImage());

        // 2. Binding: khi Property thay đổi, cập nhật object gốc
        // Đây là Binding một chiều (Wrapper -> Original)
        this.name.addListener((obs, oldV, newV) -> original.setName(newV));
        this.price.addListener((obs, oldV, newV) -> original.setPrice(newV.doubleValue()));
        this.category.addListener((obs, oldV, newV) -> original.setCategory(newV));
        this.image.addListener((obs, oldV, newV) -> original.setImage(newV));
    }

    // =======================================================
    // Cần gọi phương thức này sau khi đối tượng gốc (original) bị thay đổi
    // =======================================================
    @Override
    public void updateFromOriginal() {
        // Gọi setter của Property để kích hoạt Binding và cập nhật UI
        this.name.set(original.getName());
        this.price.set(original.getPrice());
        this.category.set(original.getCategory());
        this.image.set(original.getImage());
        // KHÔNG cần cập nhật ID vì ID không đổi khi sửa.
    }

    // =================== Getters / Setters ===================
    public IMenuItem getOriginal() { return original; }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    @Override
    public void setId(String id) {
        this.id.set(id);
        original.setId(id); // Đồng bộ ID
    }

    public String getName() { return name.get(); }

    @Override
    public void setName(String name) { this.name.set(name); }

    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }

    @Override
    public void setPrice(double price) { this.price.set(price); }

    public DoubleProperty priceProperty() { return price; }

    public String getCategory() { return category.get(); }

    @Override
    public void setCategory(String category) { this.category.set(category); }
    public StringProperty categoryProperty() { return category; }

    public Image getImage() { return this.image.get(); }

    @Override
    public Image setImage(Image image) { this.image.set(image);
    return image;
    }

    public ObjectProperty<Image> imageProperty() { return this.image; }

    @Override
    public String toString() {
        return getName();
    }

    public IMenuItem unwrap() { return original; }
}