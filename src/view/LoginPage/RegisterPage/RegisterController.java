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

    @FXML private Label lblTitle;

    // Đã xóa choiceRole và lblRole
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword; // Mới thêm

    @FXML private Button btnRegister;
    @FXML private Button btnCancel;
    @FXML private Label lblMessage;

    private MockUser user;

    @FXML
    public void initialize() {
        // Cập nhật ngôn ngữ
        updateLanguage();
    }

    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();

        lblTitle.setText(lm.getString("re.title"));
        txtUsername.setPromptText(lm.getString("re.username"));
        txtPassword.setPromptText(lm.getString("re.password"));

        // Bạn cần thêm key "re.confirm" vào file properties (VD: Nhập lại mật khẩu)
        // Nếu chưa có thì tạm thời để cứng chuỗi String ở đây
        try {
            txtConfirmPassword.setPromptText(lm.getString("re.confirm"));
        } catch (Exception e) {
            txtConfirmPassword.setPromptText("Nhập lại mật khẩu");
        }

        btnRegister.setText(lm.getString("re.re"));
        btnCancel.setText(lm.getString("re.cancel"));
    }

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPass = txtConfirmPassword.getText().trim();

        // 1. Check rỗng
        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showMessage(LanguageManager.getInstance().getString("re.warning"));
            return;
        }

        // 2. Check mật khẩu khớp nhau
        if (!password.equals(confirmPass)) {
            // Bạn cần thêm key "re.warningMatch" (Mật khẩu không khớp)
            try {
                showMessage(LanguageManager.getInstance().getString("re.warningMatch"));
            } catch (Exception e) {
                showMessage("Mật khẩu không khớp!");
            }
            return;
        }

        // 3. Check độ dài (Optional)
        if (password.length() < 8) {
            showMessage(LanguageManager.getInstance().getString("re.warningPass"));
            return;
        }

        // 4. Đăng ký thành công
        showMessage(LanguageManager.getInstance().getString("re.success"));

        // Mặc định quyền là "Staff" vì đã bỏ nút chọn
        user = new MockUser(username, password, "Staff");
        Main.MOCK_AUTH_SERVICE.setUser(user);

        // Tự động quay về trang Login sau 0.8 giây để người dùng kịp đọc thông báo thành công
        delayThenRun(0.8, () -> loadLoginPage());
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
            // Add CSS Login nếu cần
            scene.getStylesheets().add(getClass().getResource("/view/LoginPage/Login.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        lblMessage.setText(message);
        // Thêm hiệu ứng rung nhẹ hoặc đổi màu text nếu muốn
    }
}