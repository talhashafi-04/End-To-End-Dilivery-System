package model;

public class Customer extends User {
    private String customerId;
    private String address;
    
    public Customer() {}
    
    public Customer(String userId, String username, String password, String email, 
                    String phone, String customerId, String address) {
        super(userId, username, password, email, phone, "customer");
        this.customerId = customerId;
        this.address = address;
    }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}