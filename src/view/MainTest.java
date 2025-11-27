package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainTest extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Tải file FXML
        // Đảm bảo đường dẫn này khớp với vị trí của file Login.fxml của bạn
        URL fxmlUrl = getClass().getResource("/view/LoginPage/Login.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Không tìm thấy file FXML: Login.fxml");
        }

        StackPane root = FXMLLoader.load(fxmlUrl);

        // Kích thước của Scene
        Scene scene = new Scene(root, 1000, 600);

        // --- Liên kết File CSS ---
        // Đảm bảo file login.css nằm trong cùng thư mục view/LoginPage
        scene.getStylesheets().add(getClass().getResource("/view/LoginPage/Login.css").toExternalForm());

        primaryStage.setTitle("Quản lý Quán Cà phê - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}