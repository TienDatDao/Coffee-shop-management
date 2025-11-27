package view.MockTest;

import Interface.IAuthService;
import Interface.IUser;

public class MockAuthService implements IAuthService {
    private IUser iUser;
    public MockAuthService(){}
    public static boolean login(String username, String password) {
        return true;
    }

    @Override
    public void logout() {

    }

    @Override
    public IUser getCurrentUser() {
        return new MockUser("admin", "123", "manager");
    }
}
