package view.LoginPage.RegisterPage;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import view.Helper.LanguageManager;
import view.Main;

import java.util.ResourceBundle;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
    private User user;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = "Manager";

        if (username.isEmpty() || password.isEmpty()) {
            showMessage(LanguageManager.getInstance().getString("re.warning"));
            return;
        }
        if(password.length()<8){
            showMessage(LanguageManager.getInstance().getString("re.waringPass"));
            return;
        }

        // thông báo thành công
        showMessage(LanguageManager.getInstance().getString("re.success"));
        // cập nhật dữ liệu
        user = new User(username, password, role);
        Main.MOCK_AUTH_SERVICE.setUser(user);
        delayThenRun(0.5, ()->loadLoginPage());

    }

    @FXML
    private void handleCancel() {
        loadLoginPage();
    }
    private void delayThenRun(double seconds, Runnable action) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> action.run());
        pause.play();
    }

    private void loadLoginPage() {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/Login.fxml"));

            loader.setResources(bundle);

            Parent root = loader.load();
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root, 700, 475);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setMaximized(false);
            stage.setFullScreen(false);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();;
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Không thể tải trang đăng nhập.");
        }
    }

    private void showMessage(String message) {
        lblMessage.setText(message);
    }
}