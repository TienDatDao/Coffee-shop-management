package view.MainScreen.MenuManagerPage.Dialog;

import Interface.IMenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import view.MockTest.MockMenuItem;

import java.io.File;
import java.io.FileInputStream;

public class ItemDialogController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private ImageView imagePreview;
    @FXML private javafx.scene.layout.VBox dialogRoot;
    @FXML private javafx.scene.control.Label titleLabel;

    private IMenuItem editing; // null if adding
    private Image chosenImage;
    private String chosenImagePath;

    // ===========================================
    // 1. KHỞI TẠO (Initialization)
    // ===========================================
    @FXML
    public void initialize() {
        // Khởi tạo ChoiceBox với các Category

        // Đặt giá trị mặc định khi khởi tạo
        if (categoryChoice.getValue() == null) {
            categoryChoice.setValue("Drink");
        }
    }

    public void setEditing(IMenuItem item) {
        this.editing = item;
        if (item != null) {
            titleLabel.setText("✏️ Sửa món");
            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getPrice()));
            // Đảm bảo giá trị Category tồn tại trong danh sách để tránh lỗi
            if (categoryChoice.getItems().contains(item.getCategory())) {
                categoryChoice.setValue(item.getCategory());
            } else {
                categoryChoice.setValue("Khác"); // Giá trị mặc định nếu không tìm thấy
            }

            // Lưu trữ ảnh hiện tại để không cần tải lại
            if (item.getImage() != null) {
                chosenImage = item.getImage();
                imagePreview.setImage(chosenImage);
            }
        } else {
            titleLabel.setText("➕ Thêm món mới");
            // Giá trị mặc định đã được đặt trong initialize()
        }
    }

    // ===========================================
    // 2. Tải ảnh (Image Upload)
    // ===========================================
    @FXML
    private void onUploadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh món");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Cần đảm bảo window không null trước khi gọi showOpenDialog
        Window w = dialogRoot.getScene() != null ? dialogRoot.getScene().getWindow() : null;
        if (w == null) return; // Bảo vệ nếu chưa có Scene

        File f = chooser.showOpenDialog(w);
        if (f != null) {
            try (FileInputStream fis = new FileInputStream(f)) {
                chosenImage = new Image(fis);
                imagePreview.setImage(chosenImage);
                chosenImagePath = f.getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
                // Thông báo lỗi nếu không tải được ảnh
                System.err.println("Lỗi tải ảnh: " + ex.getMessage());
            }
        }
    }

    // ===========================================
    // 3. Hủy (Cancel)
    // ===========================================
    @FXML
    private void onCancel() {
        // Đảm bảo đối tượng Scene tồn tại trước khi đóng
        if (dialogRoot.getScene() != null) {
            dialogRoot.getScene().getWindow().hide();
        }
    }

    // ===========================================
    // 4. Xác nhận (Confirm)
    // ===========================================
    @FXML
    private void onConfirm() {
        String name = nameField.getText();
        String priceText = priceField.getText();
        String category = categoryChoice.getValue();

        if (name == null || name.isBlank()) { nameField.requestFocus(); return; }
        Double price = 0.0;
        try { price = Double.parseDouble(priceText); } catch (NumberFormatException e) { priceField.requestFocus(); return; }
        if (category == null) category = "Drink";

        if (editing != null) {

            editing.setName(name.trim());
            editing.setPrice(price);
            editing.setCategory(category);
            //  LUÔN GỌI SETTER để kích hoạt listener trong Wrapper
            editing.setImage(chosenImage);

            dialogRoot.getScene().setUserData(editing);

        } else {
            IMenuItem newItem = new MockMenuItem(null, name.trim(), price, category, chosenImage);

            dialogRoot.getScene().setUserData(newItem);
        }

        System.out.println("da thuc hien confirm!");
        dialogRoot.getScene().getWindow().hide();
    }

}