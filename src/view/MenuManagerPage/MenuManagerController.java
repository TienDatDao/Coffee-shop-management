package view.MenuManagerPage;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class MenuManagerController {

    @FXML
    private VBox menuList;

    @FXML
    private Button menuButton;

    private boolean menuVisible = false;

    @FXML
    public void initialize() {
        // Ẩn menu ban đầu (trượt ra khỏi màn hình)
        menuList.setTranslateX(-220);

        // Sự kiện nút hamburger
        menuButton.setOnAction(e -> toggleMenu());
    }

    private void toggleMenu() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), menuList);
        if (menuVisible) {
            slide.setToX(-menuList.getPrefWidth()); // ẩn menu
        } else {
            slide.setToX(0); // hiển thị menu sát cạnh trái
        }
        slide.play();
        menuVisible = !menuVisible;
    }
}
