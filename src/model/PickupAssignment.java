package model;

import java.sql.Timestamp;

public class PickupAssignment {
    private String assignmentId;
    private String orderId;
    private String dispatcherId;
    private String riderId;
    private Timestamp assignmentDate;
    private Timestamp confirmedDate;
    private Timestamp completedDate;
    private String status;
    
    public PickupAssignment() {}
    
    public PickupAssignment(String assignmentId, String orderId, String dispatcherId, 
                           String riderId, String status) {
        this.assignmentId = assignmentId;
        this.orderId = orderId;
        this.dispatcherId = dispatcherId;
        this.riderId = riderId;
        this.status = status;
    }
    
    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getDispatcherId() { return dispatcherId; }
    public void setDispatcherId(String dispatcherId) { this.dispatcherId = dispatcherId; }
    
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    
    public Timestamp getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(Timestamp assignmentDate) { this.assignmentDate = assignmentDate; }
    
    public Timestamp getConfirmedDate() { return confirmedDate; }
    public void setConfirmedDate(Timestamp confirmedDate) { this.confirmedDate = confirmedDate; }
    
    public Timestamp getCompletedDate() { return completedDate; }
    public void setCompletedDate(Timestamp completedDate) { this.completedDate = completedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}