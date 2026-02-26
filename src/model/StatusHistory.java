package model;

import java.sql.Timestamp;

public class StatusHistory {
    private String historyId;
    private String orderId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private Timestamp changeDate;
    private String notes;
    
    public StatusHistory() {}
    
    public StatusHistory(String historyId, String orderId, String oldStatus, 
                        String newStatus, String changedBy, String notes) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    
    public Timestamp getChangeDate() { return changeDate; }
    public void setChangeDate(Timestamp changeDate) { this.changeDate = changeDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}