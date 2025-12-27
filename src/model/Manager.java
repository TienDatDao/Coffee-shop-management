package model;

public class Manager extends User {
    private int hourlyRate;
    private int hoursWorked;
    private String currentShift;

    public Manager(int id, String username, String password, String fullName, String phoneNumber, 
                   int hourlyRate, String currentShift) {
        super(id, username, password, fullName, phoneNumber);
        this.hourlyRate = hourlyRate;
        this.currentShift = currentShift;
        this.hoursWorked = 0;
    }

    public boolean deleteMenuItem(MenuItem item) {
        System.out.println("Manager deleted item: " + item.getName());
        return true; 
    }

    public boolean addMenuItem(MenuItem item) {
        System.out.println("Manager added new item: " + item.getName());
        return true;
    }

    public void removeItem(String itemId) {
        System.out.println("Manager removed item with ID: " + itemId);
    }

    public void addItem(MenuItem item) {
        System.out.println("Manager add item to list: " + item.getName());
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getCurrentShift() {
        return currentShift;
    }

    public void setCurrentShift(String currentShift) {
        this.currentShift = currentShift;
    }
}