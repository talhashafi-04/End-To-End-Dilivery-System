package model;

import java.sql.Timestamp;

public class DeliveryAssignment {
    private String assignmentId;
    private String orderId;
    private String riderId;
    private String warehouseStaffId;
    private Timestamp assignmentDate;
    private Timestamp confirmedDate;
    private Timestamp completedDate;
    private String status;
    private int deliveryAttempts;
    
    public DeliveryAssignment() {}
    
    public DeliveryAssignment(String assignmentId, String orderId, String riderId, 
                             String warehouseStaffId, String status) {
        this.assignmentId = assignmentId;
        this.orderId = orderId;
        this.riderId = riderId;
        this.warehouseStaffId = warehouseStaffId;
        this.status = status;
        this.deliveryAttempts = 0;
    }
    
    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    
    public String getWarehouseStaffId() { return warehouseStaffId; }
    public void setWarehouseStaffId(String warehouseStaffId) { this.warehouseStaffId = warehouseStaffId; }
    
    public Timestamp getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(Timestamp assignmentDate) { this.assignmentDate = assignmentDate; }
    
    public Timestamp getConfirmedDate() { return confirmedDate; }
    public void setConfirmedDate(Timestamp confirmedDate) { this.confirmedDate = confirmedDate; }
    
    public Timestamp getCompletedDate() { return completedDate; }
    public void setCompletedDate(Timestamp completedDate) { this.completedDate = completedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getDeliveryAttempts() { return deliveryAttempts; }
    public void setDeliveryAttempts(int deliveryAttempts) { this.deliveryAttempts = deliveryAttempts; }
}