package model;

public class DeliveryRider extends User {
    private String riderId;
    private String vehicleType;
    private String licenseNumber;
    private String availabilityStatus;
    private float rating;
    
    public DeliveryRider() {}
    
    public DeliveryRider(String userId, String username, String password, String email, 
                        String phone, String riderId, String vehicleType, String licenseNumber) {
        super(userId, username, password, email, phone, "delivery_rider");
        this.riderId = riderId;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.availabilityStatus = "Available";
        this.rating = 5.0f;
    }
    
    // Getters and Setters
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    
    @Override
    public String toString() {
        return String.format("%s | %s | ⭐ %.1f | 📞 %s", 
            getUsername(), 
            vehicleType != null ? vehicleType : "N/A", 
            rating, 
            getPhone() != null ? getPhone() : "N/A"
        );
    }
}