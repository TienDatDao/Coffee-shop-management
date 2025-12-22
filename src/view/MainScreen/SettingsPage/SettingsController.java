package view.MainScreen.SettingsPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.AppConfig; // Import file Config mới tạo

import java.io.IOException;
import java.util.Locale;

public class SettingsController {

    @FXML private BorderPane rootPane;
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
        if (AppConfig.isDarkMode) {
            rootPane.getStyleClass().add("dark-theme");
        } else {
            rootPane.getStyleClass().remove("dark-theme");
        }

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
        AppConfig.isDarkMode = darkModeToggle.isSelected();

        // Lấy node gốc của scene (trong trường hợp này là BorderPane)
        Node root = darkModeToggle.getScene().getRoot();

        if (AppConfig.isDarkMode) {
            if (!rootPane.getStyleClass().contains("dark-theme")) {
                rootPane.getStyleClass().add("dark-theme");
            }
        } else {
            rootPane.getStyleClass().remove("dark-theme");
        }

        //Khi toggle ở trang Settings, phải nạp lại cả Settings.css
        AppConfig.applyTheme(darkModeToggle.getScene(), "/view/MainScreen/SettingsPage/Settings.css");
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
            AppConfig.applyTheme(scene, null);

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
    private void mainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) darkModeToggle.getScene().getWindow();
            Scene scene = new Scene(root);

            // >>> SỬA DÒNG NÀY: Quay về Main thì tham số thứ 2 là null (không cần Settings.css)
            AppConfig.applyTheme(scene, "/view/MainScreen/Main.css");

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void menuManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MenuManagerPage/MenuManager.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) darkModeToggle.getScene().getWindow();
            Scene scene = new Scene(root);

            // >>> SỬA DÒNG NÀY: Quay về Main thì tham số thứ 2 là null (không cần Settings.css)
            AppConfig.applyTheme(scene, "/view/MainScreen/MenuManagerPage/MenuManager.css");

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}