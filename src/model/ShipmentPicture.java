package model;

import java.sql.Timestamp;

public class ShipmentPicture {
    private String pictureId;
    private String orderId;
    private String imagePath;
    private Timestamp uploadDate;
    
    public ShipmentPicture() {}
    
    public ShipmentPicture(String pictureId, String orderId, String imagePath) {
        this.pictureId = pictureId;
        this.orderId = orderId;
        this.imagePath = imagePath;
    }
    
    // Getters and Setters
    public String getPictureId() { return pictureId; }
    public void setPictureId(String pictureId) { this.pictureId = pictureId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }
}