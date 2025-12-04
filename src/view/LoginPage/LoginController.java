package view.LoginPage;

import Interface.IAuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import view.MockTest.MockAuthService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
    // sự kiện khi nhấn đăng kí dạng hyperlink
    @FXML
    private void handleRegisterAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginPage/RegisterPage/Register.fxml"));
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        // 4. Tạo Scene mới và thiết lập Stage
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(
                getClass().getResource("/view/LoginPage/RegisterPage/Register.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }



    //Xử lý sự kiện khi nhấp vào nút Đăng nhập.

    @FXML
    private void handleLoginAction() {
        String username = txtUsername.getText();
        String password = getPasswordInput();
        lblMessage.getStyleClass().removeAll("success-message", "error-message");

        boolean success= MockAuthService.login(username, password);

        if (success) {

            lblMessage.setText("Login successful!");
            lblMessage.getStyleClass().add("success-message");

            // Xóa dữ liệu sau khi thành công
            txtUsername.clear();
            clearPasswordFields();

            // >>> BẮT ĐẦU PHẦN CHUYỂN TRANG <<<
            try {
                // 1. Lấy Stage hiện tại (từ bất kỳ thành phần nào trên Scene)
                Stage currentStage = (Stage) txtUsername.getScene().getWindow();

                // 2. Tải FXML của màn hình chính
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));

                // 3. Tải Root Node
                Parent root = loader.load();

                // 4. Tạo Scene mới và thiết lập Stage
                Scene scene = new Scene(root);
                scene.getStylesheets().add(
                        getClass().getResource("/view/MainScreen/Main.css").toExternalForm()
                );

                //  Đặt tiêu đề mới cho cửa sổ
                currentStage.setTitle("Coffee Shop Management - Welcome ");

                currentStage.setScene(scene);
                currentStage.show();

            } catch (Exception e) {
                e.printStackTrace();
                // Xử lý lỗi nếu không tải được FXML
                lblMessage.setText("Lỗi tải màn hình chính!");
                lblMessage.getStyleClass().add("error-message");
            }
            // >>> KẾT THÚC PHẦN CHUYỂN TRANG <<<
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
