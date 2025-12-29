package view.MainScreen.SettingsPage;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import view.Helper.LanguageManager;
import view.Main;
// lớp để thay đổi mật khẩu/ demo thay đổi mật khẩu

public class ChangePasswordController {
    @FXML private PasswordField currentPassField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField confirmPassField;

    // Các fx:id mới thêm từ FXML
    @FXML private Label lblTitle;
    @FXML private Label lblDesc;
    @FXML private Label lblCurrent;
    @FXML private Label lblNew;
    @FXML private Label lblConfirm;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML
    public void initialize() {
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();
        lblTitle.setText(lm.getString("change.edit_password"));
        lblDesc.setText(lm.getString("change.warning"));

        lblCurrent.setText(lm.getString("change.current"));
        lblNew.setText(lm.getString("change.new"));
        lblConfirm.setText(lm.getString("change.confirm"));

        btnSave.setText(lm.getString("change.confirm_now"));
        btnCancel.setText(lm.getString("btn.cancel")); // Dùng key chung
    }

    // hàm này để lưu thay đổi( demo)
    @FXML
    private void handleSave() {
        LanguageManager lm = LanguageManager.getInstance();
        String currentPass = currentPassField.getText();
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();

        // Validate cơ bản
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    lm.getString("cPass.tb"), // Tiêu đề
                    lm.getString("cPass.warning")); // Nội dung
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.WARNING,
                    lm.getString("cPass.tb"),
                    lm.getString("cPass.warningPass"));
            return;
        }

        // ---LOGIC ĐỔI MẬT KHẨU ---
        // Ở đây giả lập là thành công luôn:
        boolean success = true;

        if (success) {
            showAlert(Alert.AlertType.INFORMATION,
                    lm.getString("cPass.tb"),
                    lm.getString("cPass.success"));
            closeWindow();
        } else {
            // Trường hợp mật khẩu cũ sai
            showAlert(Alert.AlertType.ERROR,
                    lm.getString("msg.error"),
                    "Mật khẩu hiện tại không đúng!");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}