package view.MainScreen.MenuManagerPage;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class StageHelper {

    // Hiển thị dialog modal
    public static void showDialog(Parent root, String title, Window owner) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 400, 500));
        stage.initOwner(owner);         // gắn cửa sổ chính
        stage.initModality(Modality.WINDOW_MODAL); // modal: không tương tác với window khác
        stage.showAndWait();            // chặn đến khi dialog đóng
    }
}
