package view.Wrapper;

import Interface.IMenuItem;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;

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
            System.out.println(original.getImagePath());
        } else {
            this.image = new SimpleObjectProperty<>(null);
        }

        // sync Wrapper → Model
        name.addListener((o, ov, nv) -> original.setName(nv));
        price.addListener((o, ov, nv) -> original.setPrice(nv.doubleValue()));
        category.addListener((o, ov, nv) -> original.setCategory(nv));
    }
    // Hàm phụ trợ để tải ảnh
    private Image loadImage(String path) {
        // 1. Nếu đường dẫn null hoặc rỗng -> Trả về ảnh mặc định (hoặc null)
        if (path == null || path.isEmpty()) {
            return null; // Hoặc new Image("/view/Helper/default_image.png");
        }

        try {
            // 2. Trường hợp A: Ảnh Mock (Nội bộ)
            // Dấu hiệu nhận biết: Bắt đầu bằng dấu "/" (ví dụ: /view/MainScreen/...)
            if (path.startsWith("/")) {
                // Sử dụng getClass().getResource để lấy ảnh từ resources/src
                return new Image(getClass().getResource(path).toExternalForm());
            }

            // 3. Trường hợp B: Ảnh User Upload (Bên ngoài)
            // Dấu hiệu: Không bắt đầu bằng "/" hoặc chứa "images/"
            else {
                // Kiểm tra file có thực sự tồn tại trong storage không
                File file = new File("storage/" + path); // Đường dẫn tương đối từ thư mục dự án
                if (file.exists()) {
                    return new Image(file.toURI().toString());
                } else {
                    // Nếu file storage bị xóa mất -> Thử tìm lại trong resources (fallback) hoặc trả về null
                    System.err.println("Không tìm thấy ảnh tại storage: " + path);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi load ảnh: " + path);
            e.printStackTrace();
            return null;
        }
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