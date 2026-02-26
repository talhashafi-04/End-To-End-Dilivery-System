package model;

import java.sql.Timestamp;

public class Notification {
    private String notificationId;
    private String customerId;
    private String orderId;
    private String message;
    private String type;
    private boolean isRead;
    private Timestamp createdDate;
    
    public Notification() {}
    
    public Notification(String notificationId, String customerId, String orderId, 
                       String message, String type) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }
    
    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    // Helper method for display
    public String getTypeEmoji() {
        switch (type) {
            case "success": return "✅";
            case "warning": return "⚠️";
            case "info": 
            default: return "ℹ️";
        }
    }
}