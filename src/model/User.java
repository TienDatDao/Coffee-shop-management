package model;

import Interface.IUser;

public class User implements IUser {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;

    public User(int id, String username, String password, String fullName, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // --- CÁC METHOD NGHIỆP VỤ (BUSINESS LOGIC) ---

    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    public void logout() {
        System.out.println("User " + username + " has logged out.");
    }

    public boolean isManager() {
        return this instanceof Manager;
    }

    public boolean isStaff() {
        return this instanceof Staff;
    }

    public void checkIn() {
        System.out.println(fullName + " checked in.");
    }

    public void checkOut() {
        System.out.println(fullName + " checked out.");
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password changed successfully.");
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getPassWord() {
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return "Manager";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}