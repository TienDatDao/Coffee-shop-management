package view;

import javafx.scene.Scene;
import java.util.Locale;

public class AppConfig {
    public static boolean isDarkMode = false;
    public static Locale currentLocale = new Locale("vi", "VN");

    // Sửa hàm này: Thêm tham số 'extraCss'
    public static void applyTheme(Scene scene, String extraCss) {
        // 1. Xóa sạch CSS cũ
        scene.getStylesheets().clear();

        // 2. Add Main.css (Cơ bản)
        scene.getStylesheets().add(AppConfig.class.getResource("/view/MainScreen/Main.css").toExternalForm());

        // 3. Add CSS phụ (Ví dụ: Settings.css) nếu có
        if (extraCss != null && !extraCss.isEmpty()) {
            scene.getStylesheets().add(AppConfig.class.getResource(extraCss).toExternalForm());
        }

        // 4. Add DarkMode.css CUỐI CÙNG (Để đè lên tất cả)
        if (isDarkMode) {
            scene.getStylesheets().add(AppConfig.class.getResource("/view/MainScreen/DarkMode.css").toExternalForm());
        }
    }
}