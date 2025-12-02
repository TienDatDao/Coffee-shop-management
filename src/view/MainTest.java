package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //  trang đăng nhập
        URL fxmlUrl = getClass().getResource("/view/LoginPage/Login.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Không tìm thấy file FXML: Login.fxml");
        }

        StackPane root = FXMLLoader.load(fxmlUrl);

        Scene scene = new Scene(root, 600, 400);

        scene.getStylesheets()
                .add(getClass().getResource("/view/LoginPage/Login.css").toExternalForm());

        primaryStage.setTitle("Quản lý Quán Cà phê - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();

        // === Kích hoạt animation trong css ===
        Platform.runLater(() -> {
            root.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("shown"),
                    true
            );
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}