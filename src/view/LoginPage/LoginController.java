package view.LoginPage;

import Interface.IAuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.Helper.LanguageManager;
import view.MockTest.MockAuthService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblMessage;
    @FXML private Hyperlink linkRegister;


    private IAuthService authService = new MockAuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Cập nhật giao diện theo ngôn ngữ mặc định (hoặc ngôn ngữ đã lưu)
        updateLanguage();
    }


    // Hàm set text cho toàn bộ giao diện login
    private void updateLanguage() {
        LanguageManager lm = LanguageManager.getInstance();

        lblTitle.setText(lm.getString("login.title"));
        txtUsername.setPromptText(lm.getString("login.user"));

        String passText = lm.getString("login.pass");
        txtPassword.setPromptText(passText);
        txtPasswordVisible.setPromptText(passText);

        btnLogin.setText(lm.getString("login.btn"));
        linkRegister.setText(lm.getString("login.register"));

        lblMessage.setText("");
    }

    @FXML
    private void handleRegisterAction() throws IOException {
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage/RegisterPage/Register.fxml"));
        loader.setResources(bundle);
        Parent root = loader.load();
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        Scene scene = new Scene(root, 700, 475);
        scene.getStylesheets().add(getClass().getResource("/view/LoginPage/RegisterPage/Register.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLoginAction() {
        String username = txtUsername.getText();
        String password = getPasswordInput();
        boolean success = MockAuthService.login(username, password);
        if (success) {
            try {
                Stage currentStage = (Stage) txtUsername.getScene().getWindow();
                ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen/MainView.fxml"));
                loader.setResources(bundle);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/view/MainScreen/Main.css").toExternalForm());

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                currentStage.setX(screenBounds.getMinX());
                currentStage.setY(screenBounds.getMinY());
                currentStage.setWidth(screenBounds.getWidth());
                currentStage.setHeight(screenBounds.getHeight());
                currentStage.setTitle(LanguageManager.getInstance().getString("app.title"));
                currentStage.setScene(scene);
                currentStage.show();
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            lblMessage.setText(LanguageManager.getInstance().getString("msg.error"));
            lblMessage.getStyleClass().add("error-message");
        }
    }

    @FXML
    private void handleShowPasswordToggle() {
        if (chkShowPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    private String getPasswordInput() {
        if (txtPassword.isVisible()) return txtPassword.getText();
        else if (txtPasswordVisible.isVisible()) return txtPasswordVisible.getText();
        return "";
    }

    private void clearPasswordFields() {
        txtPassword.clear();
        txtPasswordVisible.clear();
    }
}