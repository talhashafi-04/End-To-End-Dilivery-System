package model;

public class WarehouseStaff extends User {
    private String staffId;
    private String warehouseId;
    private String shift;
    
    public WarehouseStaff() {}
    
    public WarehouseStaff(String userId, String username, String password, String email, 
                         String phone, String staffId, String warehouseId, String shift) {
        super(userId, username, password, email, phone, "warehouse_staff");
        this.staffId = staffId;
        this.warehouseId = warehouseId;
        this.shift = shift;
    }
    
    // Getters and Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}