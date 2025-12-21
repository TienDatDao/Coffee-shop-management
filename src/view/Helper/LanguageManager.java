package view.Helper;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private Locale currentLocale;
    private ResourceBundle bundle;

    private LanguageManager() {
        // Mặc định là tiếng Việt
        currentLocale = new Locale("vi", "VN");
        loadBundle();
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String langCode) {
        if (langCode.equals("en")) {
            currentLocale = new Locale("en", "US");
        } else {
            currentLocale = new Locale("vi", "VN");
        }
        loadBundle();
    }

    private void loadBundle() {
        // Đường dẫn tới thư mục chứa file properties: "i18n.messages"
        bundle = ResourceBundle.getBundle("i18n.message", currentLocale);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }
}