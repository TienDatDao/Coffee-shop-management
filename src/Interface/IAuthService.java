package Interface;

/* Dịch vụ xử lý đăng nhập – đăng xuất */
public interface IAuthService {
    static boolean login(String username, String password){
        return true;
    };    // kiểm tra username/password
    void logout();                                      // xóa phiên đăng nhập

    static IUser getCurrentUser()                             // trả về user đang dùng app
    {
        return null;
    }
}
