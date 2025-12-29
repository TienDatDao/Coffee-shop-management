package view.MainScreen.MenuManagerPage.Dialog;

import Interface.IMenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.MenuItem;
import view.Helper.LanguageManager;
import view.Helper.SaveImage;

import java.io.File;

public class ItemDialogController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private ImageView imagePreview;
    @FXML private javafx.scene.layout.VBox dialogRoot;
    @FXML private javafx.scene.control.Label titleLabel;

    private IMenuItem editing; // null = add
    private String chosenImagePath; // CHỈ LƯU PATH

    /* ================= INIT ================= */

    @FXML
    public void initialize() {
        if (categoryChoice.getValue() == null) {
            categoryChoice.setValue("Drink");
        }
    }

    /* ================= EDIT ================= */

    public void setEditing(IMenuItem item) {
        this.editing = item;

        if (item != null) {
            titleLabel.setText(LanguageManager.getInstance().getString("dia.edit"));

            nameField.setText(item.getName());
            priceField.setText(String.valueOf(item.getPrice()));
            categoryChoice.setValue(item.getCategory());

            // LOAD IMAGE TỪ PATH
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

            // copy vào storage/images
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

    /* ================= CANCEL ================= */

    @FXML
    private void onCancel() {
        if (dialogRoot.getScene() != null) {
            dialogRoot.getScene().getWindow().hide();
        }
    }
}