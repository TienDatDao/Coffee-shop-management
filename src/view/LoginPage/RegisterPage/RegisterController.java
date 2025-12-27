package view.LoginPage.RegisterPage;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.Helper.LanguageManager;
import view.Main;
import view.MockTest.MockUser;

import java.util.ResourceBundle;

public class RegisterController {

    // Khai báo các fx:id mới thêm
    @FXML private Label lblTitle;
    @FXML private Label lblRole;

    // Các fx:id cũ
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ChoiceBox<String> choiceRole;
    @FXML private Button btnRegister;
    @FXML private Button btnCancel;
    @FXML private Label lblMessage;

    private MockUser user;

    @FXML
    public void initialize() {
        choiceRole.getSelectionModel().selectFirst();

        // Gọi hàm cập nhật ngôn ngữ ngay khi mở form
        updateLanguage();
    }

    // Hàm mới: Set text dựa trên ngôn ngữ hiện tại
    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();

        lblTitle.setText(lm.getString("re.title"));
        txtUsername.setPromptText(lm.getString("re.username"));
        txtPassword.setPromptText(lm.getString("re.password"));
        lblRole.setText(lm.getString("re.role"));
        btnRegister.setText(lm.getString("re.re"));
        btnCancel.setText(lm.getString("re.cancel"));
    }

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = choiceRole.getValue();

        // Check rỗng -> dùng key: re.warning
        if (username.isEmpty() || password.isEmpty()) {
            showMessage(LanguageManager.getInstance().getString("re.warning"));
            return;
        }

        // Check độ dài mật khẩu (Optional - dựa trên file properties bạn có key re.warningPass)
        if (password.length() < 8) {
            showMessage(LanguageManager.getInstance().getString("re.warningPass"));
            return;
        }

        // Thông báo thành công -> dùng key: re.success
        showMessage(LanguageManager.getInstance().getString("re.success"));

        // Cập nhật dữ liệu
        user = new MockUser(username, password, role);
        Main.MOCK_AUTH_SERVICE.setUser(user);
        delayThenRun(0.5, () -> loadLoginPage());
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
            // Lấy bundle hiện tại để truyền vào FXMLLoader (quan trọng để Login cũng đúng ngôn ngữ)
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();

            // Load FXML và truyền bundle resource vào
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/Login.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();

            Scene scene = new Scene(root, 700, 475);
            scene.getStylesheets().add(
                    getClass().getResource("/view/LoginPage/Login.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Dùng key lỗi chung hoặc để text cứng nếu chưa có key phù hợp
            showMessage(LanguageManager.getInstance().getString("msg.error"));
        }
    }

    private void showMessage(String message) {
        lblMessage.setText(message);
    }
}