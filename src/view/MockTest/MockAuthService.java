package view.MockTest;

import Interface.IAuthService;
import Interface.IUser;

public class MockAuthService implements IAuthService {

    // 1. NGƯỜI DÙNG ĐỂ SO SÁNH
    private static IUser MOCK_USER_CREDENTIALS;

    // 2. BIẾN TĨNH ĐỂ LƯU PHIÊN (USER ĐANG ĐĂNG NHẬP)
    private static IUser currentUser = null;
    private MockUser user;
    public  MockAuthService(){}

    public void setUser(MockUser user){
        MOCK_USER_CREDENTIALS =user;
    }

    // Tên phương thức nên là 'login'
    public static boolean login(String username, String password) {
        if(MOCK_USER_CREDENTIALS==null){
            return false;
        }
        // Kiểm tra xem username và password có khớp không
        if (username.equals(MOCK_USER_CREDENTIALS.getUsername()) && password.equals(MOCK_USER_CREDENTIALS.getPassWord())) {

            // Đăng nhập thành công, lưu đối tượng vào biến phiên tĩnh
            currentUser = MOCK_USER_CREDENTIALS;

            return true;
        }

        // Đăng nhập thất bại, trả về null
        return false;
    }

    @Override
    public void logout() {
        // Đặt lại phiên khi đăng xuất
        currentUser = null;
    }

    // SỬA ĐỂ TRẢ VỀ NGƯỜI DÙNG ĐÃ LƯU TRONG BIẾN PHIÊN
    public static IUser getCurrentUser() {
        return currentUser; // Trả về người dùng đang đăng nhập (có thể là null)
    }
}