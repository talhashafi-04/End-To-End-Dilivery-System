package model;

public class PickupRider extends User {
    private String riderId;
    private String vehicleType;
    private String licenseNumber;
    private String availabilityStatus;
    private float rating;

    public PickupRider() {
        super();
    }

    public PickupRider(String userId, String username, String Password,String email, String phone, 
                      String riderId, String vehicleType, String licenseNumber, 
                      String availabilityStatus, float rating) {
        super(userId, username,Password, email, phone, "pickup_rider");
        this.riderId = riderId;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.availabilityStatus = availabilityStatus;
        this.rating = rating;
    }

    // Getters and Setters
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { 
        this.availabilityStatus = availabilityStatus; 
    }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    /**
     * Override toString() to display rider information in ChoiceDialog
     */
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