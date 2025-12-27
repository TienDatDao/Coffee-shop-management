package model;

public class Staff extends User {
    private int hourlyRate;
    private int hoursWorked;
    private String currentShift;

    public Staff(){}

    public Staff(int id, String username, String password, String fullName, String phoneNumber,
                 int hourlyRate, String currentShift) {
        super(id, username, password, fullName, phoneNumber);
        this.hourlyRate = hourlyRate;
        this.currentShift = currentShift;
        this.hoursWorked = 0;
    }

    public void createOrder() {
        System.out.println("Staff " + getUsername() + " is creating a new order.");
    }

    public void requestPayment() {
        int salary = hourlyRate * hoursWorked;
        System.out.println("Requesting payment: " + salary);
    }

    public void addHoursWorked(int hours) {
        this.hoursWorked += hours;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {
        if (hourlyRate >= 0) {
            this.hourlyRate = hourlyRate;
        } else {
            System.out.println("Lương theo giờ không hợp lệ!");
        }
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

    public String getRole() {
        return "Staff";
    }

    public void setCurrentShift(String currentShift) {
        this.currentShift = currentShift;
    }
}