package view.MainScreen.SettingsPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.AppConfig;
import view.Helper.LanguageManager;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsController {

    @FXML private BorderPane rootPane;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox darkModeToggle;

    // --- CÁC ID MỚI ---
    @FXML private Label lblAppTitle;
    @FXML private Button btnSell;
    @FXML private Button btnManage;
    @FXML private Button btnSetting;
    @FXML private Button btnLogout;

    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSubtitle;

    // Card Giao diện
    @FXML private Label lblInterfaceTitle;
    @FXML private Label lblInterfaceDesc;
    @FXML private Label lblLanguage;
    @FXML private Label lblDarkMode;
    @FXML private Label lblDarkModeHint;

    // Card Bảo mật
    @FXML private Label lblSecurityTitle;
    @FXML private Label lblSecurityDesc;
    @FXML private Label lblSecurityStatus;
    @FXML private Button btnChangePassword;


    @FXML
    public void initialize() {
        // 1. Setup ComboBox
        languageComboBox.getItems().addAll("Tiếng Việt", "English");

        // Set giá trị mặc định dựa trên LanguageManager
        if (LanguageManager.getInstance().getBundle().getLocale().getLanguage().equals("en")) {
            languageComboBox.setValue("English");
        } else {
            languageComboBox.setValue("Tiếng Việt");
        }

        // Sự kiện đổi ngôn ngữ
        languageComboBox.setOnAction(e -> {
            String selected = languageComboBox.getValue();
            if ("English".equals(selected)) {
                LanguageManager.getInstance().setLanguage("en");
            } else {
                LanguageManager.getInstance().setLanguage("vi");
            }
            // Cập nhật text ngay lập tức
            updateLanguage();
        });

        // 2. Setup Dark Mode
        darkModeToggle.setSelected(AppConfig.isDarkMode);
        if (AppConfig.isDarkMode) {
            rootPane.getStyleClass().add("dark-theme");
        } else {
            rootPane.getStyleClass().remove("dark-theme");
        }

        // 3. Cập nhật ngôn ngữ lần đầu khi vào trang
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();

        // Sidebar
        lblAppTitle.setText(lm.getString("menu.pos")); // Hoặc key menu.pos
        btnSell.setText("🛒  " + lm.getString("menu.sell"));
        btnManage.setText("👪 " + lm.getString("menu.manage"));
        btnSetting.setText("⚙  " + lm.getString("menu.setting"));
        btnLogout.setText("🚪  " + lm.getString("menu.logout"));

        // Header
        lblPageTitle.setText(lm.getString("setting.title"));
        lblPageSubtitle.setText(lm.getString("setting.subtitle"));

        // Card 1: Interface
        lblInterfaceTitle.setText(lm.getString("setting.cardtitle"));
        lblInterfaceDesc.setText(lm.getString("setting.carddesc"));
        lblLanguage.setText(lm.getString("setting.language"));
        lblDarkMode.setText(lm.getString("setting.darktheme"));
        lblDarkModeHint.setText(lm.getString("setting.notice"));

        // Card 2: Security
        lblSecurityTitle.setText(lm.getString("setting.security"));
        lblSecurityDesc.setText(lm.getString("setting.securitytext"));
        lblSecurityStatus.setText(lm.getString("setting.securitystt"));
        btnChangePassword.setText(lm.getString("setting.change"));
    }

    @FXML
    private void handleDarkModeToggle() {
        AppConfig.isDarkMode = darkModeToggle.isSelected();
        Node root = darkModeToggle.getScene().getRoot();

        if (AppConfig.isDarkMode) {
            if (!rootPane.getStyleClass().contains("dark-theme")) {
                rootPane.getStyleClass().add("dark-theme");
            }
        } else {
            rootPane.getStyleClass().remove("dark-theme");
        }
        AppConfig.applyTheme(darkModeToggle.getScene(), "/view/MainScreen/SettingsPage/Settings.css");
    }

    @FXML
    private void openChangePasswordModal() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/SettingsPage/ChangePassword.fxml"));
            loader.setResources(bundle); // Truyền bundle cho popup

            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(LanguageManager.getInstance().getString("change.edit_password"));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(darkModeToggle.getScene().getWindow());

            Scene scene = new Scene(root);
            AppConfig.applyTheme(scene, null); // Áp dụng dark mode nếu có

            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Lỗi không thể mở cửa sổ đổi mật khẩu.");
            alert.show();
        }
    }

    // --- NAVIGATION (Đã sửa để truyền ResourceBundle) ---

    @FXML
    private void mainScreen() {
        changeScene("/view/MainScreen/MainView.fxml", "/view/MainScreen/Main.css");
    }

    @FXML
    private void menuManager() {
        changeScene("/view/MainScreen/MenuManagerPage/MenuManager.fxml", "/view/MainScreen/MenuManagerPage/MenuManager.css");
    }

    private void changeScene(String fxmlPath, String cssPath) {
        try {
            // Lấy bundle hiện tại
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setResources(bundle); // Quan trọng: Truyền ngôn ngữ sang trang mới

            Parent root = loader.load();
            Stage stage = (Stage) darkModeToggle.getScene().getWindow();
            Scene scene = new Scene(root);

            AppConfig.applyTheme(scene, cssPath);

            stage.setTitle(LanguageManager.getInstance().getString("app.title"));
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/Login.fxml"));
            loader.setResources(bundle);

            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Scene scene = new Scene(root, 700, 475);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setMaximized(false);
            stage.setFullScreen(false);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setTitle(LanguageManager.getInstance().getString("login.title"));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải trang đăng nhập.");
        }
    }
}