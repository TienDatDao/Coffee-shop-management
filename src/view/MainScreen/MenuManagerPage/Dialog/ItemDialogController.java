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
import model.MenuItem;
import view.Helper.LanguageManager;
import view.Helper.SaveImage;
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

    /* ================= EDIT ================= */

    public void setEditing(IMenuItem item) {
        this.editing = item;

        if (item != null) {
            titleLabel.setText(LanguageManager.getInstance().getString("dia.edit"));

            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getPrice()));
            categoryChoice.setValue(item.getCategory());

            // ⭐ LOAD IMAGE TỪ PATH
            chosenImagePath = item.getImagePath();
            if (chosenImagePath != null) {
                imagePreview.setImage(
                        new Image("file:storage/" + chosenImagePath, true)
                );
            }

        } else {
            titleLabel.setText(LanguageManager.getInstance().getString("dia.add"));
        }
    }

    /* ================= UPLOAD IMAGE ================= */

    @FXML
    private void onUploadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(LanguageManager.getInstance().getString("dia.choiceP"));
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Window w = dialogRoot.getScene() != null ? dialogRoot.getScene().getWindow() : null;
        if (w == null) return;

        File f = chooser.showOpenDialog(w);
        if (f == null) return;

        try {
            // preview
            imagePreview.setImage(new Image(f.toURI().toString()));

            // ⭐ copy vào storage/images
            chosenImagePath = SaveImage.copyToStorage(f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= CONFIRM ================= */

    @FXML
    private void onConfirm() {

        String name = nameField.getText();
        String priceText = priceField.getText();
        String category = categoryChoice.getValue();

        if (name == null || name.isBlank()) return;

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (Exception e) {
            return;
        }

        if (editing != null) {
            editing.setName(name.trim());
            editing.setPrice(price);
            editing.setCategory(category);
            editing.setImagePath(chosenImagePath);

            dialogRoot.getScene().setUserData(editing);

        } else {
            IMenuItem newItem = new MenuItem(
                    null,
                    name.trim(),
                    price,
                    category,
                    chosenImagePath
            );
            dialogRoot.getScene().setUserData(newItem);
        }

        dialogRoot.getScene().getWindow().hide();
    }
    @FXML
    private void onCancel() {
        if (dialogRoot.getScene() != null) {
            dialogRoot.getScene().getWindow().hide();
        }
    }
}