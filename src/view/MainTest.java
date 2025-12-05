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

public class MainTest extends Application {
    public static final MockAuthService MOCK_AUTH_SERVICE = new MockAuthService();
    public static final IMenuService SHARED_MENU_SERVICE = new MockMenuService();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //  trang đăng nhập
        URL fxmlUrl = getClass().getResource("/view/LoginPage/Login.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Không tìm thấy file FXML: Login.fxml");
        }

        StackPane root = FXMLLoader.load(fxmlUrl);

        Scene scene = new Scene(root, 1000, 600);

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
        // trang đăng nhập
//        // trang thanh toán cuối cùng
//        BorderPane root = FXMLLoader.load(getClass().getResource("/view/PaymentPage/Payment.fxml"));
//        Scene scene = new Scene(root, 1000, 600);
//        primaryStage.setTitle("Payment Page - POS");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//        // Giả sử 'scene' là đối tượng Scene của bạn
//        scene.getStylesheets().add(getClass().getResource("/view/PaymentPage/Payment.css").toExternalForm());
//        // trang thanh toán cuối cùng
//        StackPane root = FXMLLoader.load(getClass().getResource("/view/MenuManagerPage/MenuManager.fxml"));
//        Scene scene = new Scene(root, 1000, 600);
//        primaryStage.setTitle("Coffee management app");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//        scene.getStylesheets().add(getClass().getResource("/view/MenuManagerPage/MenuManager.css").toExternalForm());
//        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainScreen/MainView.fxml"));
//        Scene scene = new Scene(root, 1000, 600);
//        primaryStage.setTitle("Coffee management app");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//        scene.getStylesheets().add(getClass().getResource("/view/MainScreen/Main.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
