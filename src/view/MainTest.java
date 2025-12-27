package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.MenuService;
import view.Helper.LanguageManager;
import view.MockTest.MockAuthService;

import java.util.ResourceBundle;

public class MainTest extends Application {

    // Giả lập Service toàn cục (như code cũ của bạn)
    public static final MenuService MENU_SERVICE = MenuService.getInstance();
    public static final MockAuthService MOCK_AUTH_SERVICE = new MockAuthService();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Lấy ngôn ngữ hiện tại (Việt hoặc Anh)
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/Login.fxml"));

        // 3. Cài đặt ResourceBundle cho loader
        loader.setResources(bundle);

        // 4. Load giao diện
        Parent root = loader.load();

        // 5. Thiết lập Scene
        Scene scene = new Scene(root, 1000, 600);

        // Add CSS
        String css = this.getClass().getResource("/view/LoginPage/Login.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle(bundle.getString("app.title")); // Lấy title từ file ngôn ngữ luôn
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}