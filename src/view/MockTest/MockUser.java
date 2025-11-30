package view.MockTest;

import Interface.IUser;

public class MockUser implements IUser {

    private String name;
    private String password;
    private String role;
    public MockUser(String name, String password, String role){
        this.name = name;
        this.password = password;
        this.role = role;
    }
    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassWord() {
        return null;
    }

    @Override
    public String getRole() {
        return null;
    }
}
