package view;

import javafx.scene.Scene;
import java.util.Locale;

public class AppConfig {
    // Biến lưu trạng thái
    public static boolean isDarkMode = false;
    public static Locale currentLocale = new Locale("vi", "VN");

    public static void applyTheme(Scene scene) {
        // 1. Xóa sạch các CSS hiện có để tránh chồng chéo sai lệch
        scene.getStylesheets().clear();

        // 2. LUÔN LUÔN add Main.css trước (chứa layout, size, padding cơ bản)
        // Lưu ý: Đảm bảo đường dẫn tới file css là chính xác
        scene.getStylesheets().add(AppConfig.class.getResource("/view/MainScreen/Main.css").toExternalForm());

        // 3. Nếu là Dark Mode, add thêm DarkMode.css vào SAU
        // (CSS add sau sẽ ghi đè CSS add trước nếu trùng selector)
        if (isDarkMode) {
            scene.getStylesheets().add(AppConfig.class.getResource("/view/MainScreen/DarkMode.css").toExternalForm());
        }
    }
}