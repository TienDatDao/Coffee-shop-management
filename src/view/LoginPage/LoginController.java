package view.LoginPage;
import Interface.IUser;
import view.MockTest.MockAuthService;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

// Import các Interface Dịch vụ
import Interface.IAuthService;
//import Service.AuthServiceMock;
// Thay thế bằng AuthService thực tế khi cần

public class LoginController implements Initializable {

    // Ánh xạ các thành phần UI từ FXML
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblMessage;

    private IAuthService authService = new MockAuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Có thể thực hiện các thiết lập ban đầu tại đây (nếu cần)
    }
    //


    //Xử lý sự kiện khi nhấp vào nút Đăng nhập.

    @FXML
    private void handleLoginAction() {
        String username = txtUsername.getText();

        // Lấy mật khẩu từ trường đang hiển thị
        String password = getPasswordInput();

        lblMessage.getStyleClass().removeAll("success-message", "error-message");

        // Gọi dịch vụ đăng nhập
        boolean loginSuccess = MockAuthService.login(username, password);

        if (loginSuccess) {
           IUser currentUser = authService.getCurrentUser();

            lblMessage.setText("Login successful!");
            lblMessage.getStyleClass().add("success-message");

            // Xóa dữ liệu sau khi thành công
            txtUsername.clear();
            clearPasswordFields();

            // TODO: Chuyển sang màn hình chính
        } else {
            lblMessage.setText("Wrong, please check your password and username!");
            lblMessage.getStyleClass().add("error-message");
        }
    }


    // * Xử lý sự kiện khi CheckBox hiển thị mật khẩu được thay đổi.

    @FXML
    private void handleShowPasswordToggle() {
        if (chkShowPassword.isSelected()) {
            // Hiển thị mật khẩu: Chuyển text từ PasswordField sang TextField
            txtPasswordVisible.setText(txtPassword.getText());

            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            // Ẩn mật khẩu: Chuyển text từ TextField sang PasswordField
            txtPassword.setText(txtPasswordVisible.getText());

            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }
    // trả về mật khẩu trong textfield
    private String getPasswordInput() {
        if (txtPassword.isVisible()) {
            return txtPassword.getText();
        } else if (txtPasswordVisible.isVisible()) {
            return txtPasswordVisible.getText();
        }
        return "";
    }


     // Xóa nội dung của cả hai trường mật khẩu.

    private void clearPasswordFields() {
        txtPassword.clear();
        txtPasswordVisible.clear();
    }
}