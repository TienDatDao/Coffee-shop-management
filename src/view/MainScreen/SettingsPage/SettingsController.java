package view.MainScreen.SettingsPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.AppConfig; // Import file Config mới tạo

import java.io.IOException;
import java.util.Locale;

public class SettingsController {

    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox darkModeToggle;

    @FXML
    public void initialize() {
        // 1. Setup ComboBox
        languageComboBox.getItems().addAll("Tiếng Việt (VN)", "English (US)");

        // Load trạng thái từ AppConfig
        if (AppConfig.currentLocale.getLanguage().equals("vi")) {
            languageComboBox.setValue("Tiếng Việt (VN)");
        } else {
            languageComboBox.setValue("English (US)");
        }

        // 2. Setup Dark Mode
        darkModeToggle.setSelected(AppConfig.isDarkMode);

        // 3. Logic đổi ngôn ngữ
        languageComboBox.setOnAction(e -> {
            if (languageComboBox.getValue().contains("English")) {
                AppConfig.currentLocale = new Locale("en", "US");
            } else {
                AppConfig.currentLocale = new Locale("vi", "VN");
            }
            Locale.setDefault(AppConfig.currentLocale);
        });
    }

    @FXML
    private void handleDarkModeToggle() {
        // Lưu trạng thái vào Config
        AppConfig.isDarkMode = darkModeToggle.isSelected();
        // Áp dụng ngay lập tức
        AppConfig.applyTheme(darkModeToggle.getScene());
    }

    @FXML
    private void openChangePasswordModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/SettingsPage/ChangePassword.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Thay đổi mật khẩu");
            modalStage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ cha
            modalStage.initOwner(darkModeToggle.getScene().getWindow());

            Scene scene = new Scene(root);
            // Quan trọng: Popup cũng phải theo chế độ Sáng/Tối
            AppConfig.applyTheme(scene);

            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Lỗi không thể mở cửa sổ đổi mật khẩu.");
            alert.show();
        }
    }

    @FXML
    private void goBack() {
        try {
            // Quay về MainView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) darkModeToggle.getScene().getWindow();

            Scene scene = new Scene(root);
            // Quan trọng: Áp dụng Theme đã cài đặt khi quay về
            AppConfig.applyTheme(scene);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}