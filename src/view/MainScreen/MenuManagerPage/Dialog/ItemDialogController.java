package view.MainScreen.MenuManagerPage.Dialog;

import Interface.IMenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import view.Helper.LanguageManager;
import view.MockTest.MockMenuItem;

import java.io.File;
import java.io.FileInputStream;

public class ItemDialogController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private ImageView imagePreview;
    @FXML private VBox dialogRoot;
    @FXML private Label titleLabel;

    // Các fx:id mới thêm từ FXML
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Label lblCategory;
    @FXML private Button btnUpload;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private IMenuItem editing; // null if adding
    private Image chosenImage;
    private String chosenImagePath;

    @FXML
    public void initialize() {
        if (categoryChoice.getValue() == null) {
            categoryChoice.setValue("Drink");
        }
        // Gọi hàm cập nhật ngôn ngữ ngay khi khởi tạo
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();

        // Cập nhật các Label tĩnh
        lblName.setText(lm.getString("dia.name"));
        lblPrice.setText(lm.getString("dia.price"));
        lblCategory.setText(lm.getString("dia.danh"));

        // Cập nhật Prompt Text (Gợi ý trong ô nhập)
        nameField.setPromptText(lm.getString("dia.ex")); // "VD: Cà phê sữa..."

        // Cập nhật Buttons
        btnUpload.setText(lm.getString("dia.load"));
        btnCancel.setText(lm.getString("dia.cancel"));
        btnConfirm.setText(lm.getString("dia.confirm"));
    }

    public void setEditing(IMenuItem item) {
        this.editing = item;
        LanguageManager lm = LanguageManager.getInstance();

        if (item != null) {
            // "Sửa món"
            titleLabel.setText("✏️ " + lm.getString("dia.edit"));
            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getPrice()));

            if (categoryChoice.getItems().contains(item.getCategory())) {
                categoryChoice.setValue(item.getCategory());
            } else {
                categoryChoice.setValue("Other");
            }

            if (item.getImage() != null) {
                chosenImage = item.getImage();
                imagePreview.setImage(chosenImage);
            }
        } else {
            // "Thêm món mới"
            titleLabel.setText("➕ " + lm.getString("dia.add"));
        }
    }

    @FXML
    private void onUploadImage() {
        FileChooser chooser = new FileChooser();
        // "Chọn ảnh món"
        chooser.setTitle(LanguageManager.getInstance().getString("dia.choiceP"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Window w = dialogRoot.getScene() != null ? dialogRoot.getScene().getWindow() : null;
        if (w == null) return;

        File f = chooser.showOpenDialog(w);
        if (f != null) {
            try (FileInputStream fis = new FileInputStream(f)) {
                chosenImage = new Image(fis);
                imagePreview.setImage(chosenImage);
                chosenImagePath = f.getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Lỗi tải ảnh: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void onCancel() {
        if (dialogRoot.getScene() != null) {
            dialogRoot.getScene().getWindow().hide();
        }
    }

    @FXML
    private void onConfirm() {
        String name = nameField.getText();
        String priceText = priceField.getText();
        String category = categoryChoice.getValue();

        if (name == null || name.isBlank()) { nameField.requestFocus(); return; }

        Double price = 0.0;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            priceField.requestFocus();
            return;
        }

        if (category == null) category = "Drink";

        if (editing != null) {
            editing.setName(name.trim());
            editing.setPrice(price);
            editing.setCategory(category);
            editing.setImage(chosenImage);
            dialogRoot.getScene().setUserData(editing);
        } else {
            IMenuItem newItem = new MockMenuItem(null, name.trim(), price, category, chosenImage);
            dialogRoot.getScene().setUserData(newItem);
        }

        dialogRoot.getScene().getWindow().hide();
    }
}