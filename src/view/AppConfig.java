package view;

import javafx.scene.Scene;
import java.net.URL;

public class AppConfig {

    // 1. Chỉ giữ lại biến isDarkMode
    // BỎ biến currentLocale vì đã có LanguageManager quản lý
    public static boolean isDarkMode = false;


    public static void applyTheme(Scene scene, String cssFilePath) {
        // Xóa sạch CSS cũ để tránh chồng chéo
        scene.getStylesheets().clear();

        // 1. Add CSS chính của màn hình (Login.css, Main.css, Payment.css...)
        if (cssFilePath != null && !cssFilePath.isEmpty()) {
            addCssSafely(scene, cssFilePath);
        }

        // 2. Add DarkMode.css (nếu đang bật chế độ tối)
        // Để nó ghi đè lên các màu sắc mặc định
        if (isDarkMode) {
            addCssSafely(scene, "/view/MainScreen/DarkMode.css");
        }
    }

    private static void addCssSafely(Scene scene, String path) {
        URL url = AppConfig.class.getResource(path);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        } else {
            System.err.println("Không tìm thấy file CSS tại đường dẫn: " + path);
        }
    }
}