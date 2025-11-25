package Interface;

public interface IAuthService {
    boolean login(String username, String password);
    void logout();
    IUser getCurrentUser();
}
