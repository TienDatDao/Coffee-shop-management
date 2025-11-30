package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Coffee POS System");
        // Kích thước tối thiểu để không bị vỡ giao diện
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        Scene scene = new Scene(root);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}