package view;

import Interface.IMenuService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.MockTest.MockAuthService;
import view.MockTest.MockMenuService;

import java.io.IOException;
import java.net.URL;


public class Main extends Application {

    public static final MockAuthService MOCK_AUTH_SERVICE = new MockAuthService();
    public static final IMenuService SHARED_MENU_SERVICE = new MockMenuService();

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load trang đăng nhập
        URL fxmlUrl = getClass().getResource("/view/LoginPage/Login.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Không tìm thấy file FXML: Login.fxml");
        }

        StackPane root = FXMLLoader.load(fxmlUrl);

        Scene scene = new Scene(root, 700, 475);

        scene.getStylesheets()
                .add(getClass().getResource("/view/LoginPage/Login.css").toExternalForm());

        primaryStage.setTitle("Quản lý Quán Cà phê - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Kích hoạt animation CSS (pseudo-class :shown)
        Platform.runLater(() -> {
            root.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("shown"),
                    true
            );
        });
    }

    public static void main(String[] args) {
        Application.setUserAgentStylesheet(null);
        java.util.logging.Logger.getLogger("javafx").setLevel(java.util.logging.Level.SEVERE);

        launch(args);
    }
}
