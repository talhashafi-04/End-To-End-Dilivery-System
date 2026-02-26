package model;

public class Parcel {
    private String parcelId;
    private String orderId;
    private double weight;
    private String dimensions;
    private String description;
    
    public Parcel() {}
    
    public Parcel(String parcelId, String orderId, double weight, 
                  String dimensions, String description) {
        this.parcelId = parcelId;
        this.orderId = orderId;
        this.weight = weight;
        this.dimensions = dimensions;
        this.description = description;
    }
    
    // Getters and Setters
    public String getParcelId() { return parcelId; }
    public void setParcelId(String parcelId) { this.parcelId = parcelId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}