package view.LoginPage;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

// Import các Interface Dịch vụ
import Interface.IAuthService;
import Interface.IUser;
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

    // Dịch vụ (Dependency Injection đơn giản)
    private IAuthService authService;// = new AuthServiceMock();

    // Phương thức này được gọi ngay sau khi FXML loader tải các thành phần
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Có thể thực hiện các thiết lập ban đầu tại đây (nếu cần)
    }

    /**
     * Xử lý sự kiện khi nhấp vào nút Đăng nhập.
     */
    @FXML
    private void handleLoginAction() {
        String username = txtUsername.getText();

        // Lấy mật khẩu từ trường đang hiển thị thông qua phương thức đóng gói
        String password = getPasswordInput();

        lblMessage.getStyleClass().removeAll("success-message", "error-message");

        // Gọi dịch vụ đăng nhập
        //boolean loginSuccess = authService.login(username, password);

        if (username.equals("admin")&&password.equals("123")) {
           // IUser currentUser = authService.getCurrentUser();

           // lblMessage.setText("Đăng nhập thành công! Chào mừng " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
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

    /**
     * Xử lý sự kiện khi CheckBox hiển thị mật khẩu được thay đổi.
     */
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

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ ---

    /**
     * Trả về giá trị mật khẩu từ trường đang hiển thị/quản lý.
     */
    private String getPasswordInput() {
        if (txtPassword.isVisible()) {
            return txtPassword.getText();
        } else if (txtPasswordVisible.isVisible()) {
            return txtPasswordVisible.getText();
        }
        return "";
    }

    /**
     * Xóa nội dung của cả hai trường mật khẩu.
     */
    private void clearPasswordFields() {
        txtPassword.clear();
        txtPasswordVisible.clear();
    }
}