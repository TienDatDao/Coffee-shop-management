package view.MockTest;

import Interface.IAuthService;
import Interface.IUser;

public class MockAuthService implements IAuthService {

    // 1. NGƯỜI DÙNG CỨNG DÙNG ĐỂ SO SÁNH
    private static IUser MOCK_USER_CREDENTIALS = new MockUser("admin", "123", "Quản lý");

    // 2. BIẾN TĨNH ĐỂ LƯU PHIÊN (USER ĐANG ĐĂNG NHẬP)
    private static IUser currentUser = null;

    public MockAuthService(){}

    // SỬA ĐỂ TRẢ VỀ IUser:
    // Tên phương thức nên là 'login' (như bạn đã đặt), nhưng tôi sẽ thêm logic lưu phiên vào đây.
    public static boolean login(String username, String password) {
        // Kiểm tra xem username và password có khớp không
        if (username.equals(MOCK_USER_CREDENTIALS.getUsername()) && password.equals(MOCK_USER_CREDENTIALS.getPassWord())) {

            // Đăng nhập thành công, lưu đối tượng vào biến phiên tĩnh
            currentUser = MOCK_USER_CREDENTIALS;

            return true;
        }

        // Đăng nhập thất bại, trả về null
        return true;
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