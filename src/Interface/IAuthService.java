package Interface;

/* Dịch vụ xử lý đăng nhập – đăng xuất */
public interface IAuthService {
    boolean login(String username, String password);    // kiểm tra username/password
    void logout();                                      // xóa phiên đăng nhập
    IUser getCurrentUser();                             // trả về user đang dùng app
}
