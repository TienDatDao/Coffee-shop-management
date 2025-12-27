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
import view.AppConfig;
import view.Helper.LanguageManager;

import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsController {

    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox darkModeToggle;
    @FXML
    public void initialize() {
        // 1. Setup ComboBox
        languageComboBox.getItems().addAll("Tiếng Việt (VN)", "English (US)");

        // 2. Lấy ngôn ngữ hiện tại từ LanguageManager để hiển thị đúng
        String currentLang = LanguageManager.getInstance().getBundle().getLocale().getLanguage();
        if (currentLang.equals("en")) {
            languageComboBox.setValue("English (US)");
        } else {
            languageComboBox.setValue("Tiếng Việt (VN)");
        }

        // 3. Setup Dark Mode (Lấy từ AppConfig)
        darkModeToggle.setSelected(AppConfig.isDarkMode);

        // 4. Logic đổi ngôn ngữ
        languageComboBox.setOnAction(e -> handleLanguageChange());
    }

    private void handleLanguageChange() {
        String selected = languageComboBox.getValue();

        // Xác định mã ngôn ngữ
        String langCode = selected.contains("English") ? "en" : "vi";

        LanguageManager.getInstance().setLanguage(langCode);

        reloadMainScreen();
    }

    private void reloadMainScreen() {
        try {
            // Lấy Bundle
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            // Load lại MainView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));
            loader.setResources(bundle); // Truyền Bundle mới vào

            Parent root = loader.load();
            Stage stage = (Stage) languageComboBox.getScene().getWindow();

            Scene scene = new Scene(root);

            // Giữ nguyên trạng thái theme (Sáng/Tối)
            AppConfig.applyTheme(scene, "/view/MainScreen/Main.css");

            stage.setScene(scene);

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("ERROR: " + ex.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleDarkModeToggle() {

        // Lưu trạng thái vào Config
        AppConfig.isDarkMode = darkModeToggle.isSelected();
        // Áp dụng ngay lập tức cho Scene hiện tại
        AppConfig.applyTheme(darkModeToggle.getScene(), "/view/MainScreen/SettingsPage/Settings.css");
    }

    @FXML
    private void openChangePasswordModal() {
        try {
            // Lấy Bundle để Modal cũng có đa ngôn ngữ
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/SettingsPage/ChangePassword.fxml"));
            loader.setResources(bundle); // Truyền ngôn ngữ vào Modal

            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("setting.change")); // Tiêu đề lấy từ file properties
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(darkModeToggle.getScene().getWindow());

            Scene scene = new Scene(root);
            AppConfig.applyTheme(scene, null);

            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void menuManager() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/MainScreen/MenuManagerPage/MenuManager.fxml"),
                    bundle
            );
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
    @FXML
    private void mainScreen() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/MainScreen/MainView.fxml"),
                    bundle
            );
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
    private void logout(){
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/LoginPage/Login.fxml"),
                    bundle
            );
            Parent root = loader.load();
            Stage stage = (Stage) languageComboBox.getScene().getWindow();

            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LanguageManager.getInstance().getString("mainc.notnot"));
        }
    }

}