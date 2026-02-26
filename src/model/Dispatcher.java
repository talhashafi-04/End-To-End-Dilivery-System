package model;

public class Dispatcher extends User {

	private String dispatcherId;
    private String assignedZone;
    private String availabilityStatus;
    
    public Dispatcher() {}
    
    public Dispatcher(String userId, String username, String password, String email, 
            String phone, String dispatcherId, String assignedZone)
    {
    	super(userId,username,password,email,phone,"dispatcher");
    	this.dispatcherId = dispatcherId;
		this.assignedZone = assignedZone;
		this.availabilityStatus = "Available";
    }
    public String getDispatcherId() { return dispatcherId; }
    public void setDispatcherId(String dispatcherId) { this.dispatcherId = dispatcherId; }
    
    public String getAssignedZone() { return assignedZone; }
    public void setAssignedZone(String assignedZone) { this.assignedZone = assignedZone; }
    
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
}
