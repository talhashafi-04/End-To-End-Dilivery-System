package model;

public class Manager extends User {
    private String managerId;
    private String department;
    
    public Manager() {}
    
    public Manager(String userId, String username, String password, String email, 
                   String phone, String managerId, String department) {
        super(userId, username, password, email, phone, "manager");
        this.managerId = managerId;
        this.department = department;
    }
    
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}