package view.MainScreen.SettingsPage;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import view.Helper.LanguageManager;

public class ChangePasswordController {

    @FXML private PasswordField currentPassField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField confirmPassField;

    @FXML
    private void handleSave() {
        String cur = currentPassField.getText();
        String newP = newPassField.getText();
        String cfm = confirmPassField.getText();

        if (cur.isEmpty() || newP.isEmpty()) {
            showAlert(LanguageManager.getInstance().getString("cPass.warning"), Alert.AlertType.WARNING);
            return;
        }

        // TODO: Kiểm tra mật khẩu cũ có đúng không với Database/Service
        // if (!MockAuthService.checkPassword(cur)) { ... }

        if (!newP.equals(cfm)) {
            showAlert(LanguageManager.getInstance().getString("cPass.warningPass"), Alert.AlertType.ERROR);
            return;
        }

        // TODO: Gọi Service để cập nhật mật khẩu mới
        // AuthService.updatePassword(newP);

        showAlert(LanguageManager.getInstance().getString("cPass.success"), Alert.AlertType.INFORMATION);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) currentPassField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(LanguageManager.getInstance().getString("cPass.tb"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}