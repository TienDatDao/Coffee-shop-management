package view.LoginPage.RegisterPage;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.Helper.LanguageManager;
import view.MainTest;
import view.MockTest.MockUser;

import java.util.ResourceBundle;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ChoiceBox<String> choiceRole;
    @FXML private Label lblMessage;
    private MockUser user;

    @FXML
    public void initialize() {
        choiceRole.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = choiceRole.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(password.length()<8){
            showMessage("Mật khẩu phải từ 8 kí tự trở lên, vui lòng nhập lại!");
            return;
        }

        // thông báo thành công
        showMessage("Đăng ký thành công");
        // cập nhật dữ liệu
        user = new MockUser(username, password, role);
        MainTest.MOCK_AUTH_SERVICE.setUser(user);
        delayThenRun(1, ()->loadLoginPage());

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
            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Không thể tải trang đăng nhập.");
        }
    }

    private void showMessage(String message) {
        lblMessage.setText(message);
    }
}
