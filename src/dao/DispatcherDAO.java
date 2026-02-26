package dao;

import model.PickupRider;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DispatcherDAO {
    
    // Get all available pickup riders
    public List<PickupRider> getAvailableRiders() {
        List<PickupRider> riders = new ArrayList<>();
        String query = "SELECT u.*, pr.rider_id, pr.vehicle_type, pr.license_number, " +
                      "pr.availability_status, pr.rating " +
                      "FROM users u " +
                      "JOIN pickup_riders pr ON u.user_id = pr.user_id " +
                      "WHERE pr.availability_status = 'Available' " +
                      "ORDER BY pr.rating DESC, u.username ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PickupRider rider = new PickupRider();
                rider.setUserId(rs.getString("user_id"));
                rider.setUsername(rs.getString("username"));
                rider.setEmail(rs.getString("email"));
                rider.setPhone(rs.getString("phone"));
                rider.setRiderId(rs.getString("rider_id"));
                rider.setVehicleType(rs.getString("vehicle_type"));
                rider.setLicenseNumber(rs.getString("license_number"));
                rider.setAvailabilityStatus(rs.getString("availability_status"));
                rider.setRating(rs.getFloat("rating"));
                
                riders.add(rider);
            }
            
            System.out.println("✅ Loaded " + riders.size() + " available riders");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading available riders!");
            e.printStackTrace();
        }
        
        return riders;
    }
    
    // Get all riders (including busy ones)
    public List<PickupRider> getAllRiders() {
        List<PickupRider> riders = new ArrayList<>();
        String query = "SELECT u.*, pr.rider_id, pr.vehicle_type, pr.license_number, " +
                      "pr.availability_status, pr.rating " +
                      "FROM users u " +
                      "JOIN pickup_riders pr ON u.user_id = pr.user_id " +
                      "ORDER BY pr.rating DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PickupRider rider = new PickupRider();
                rider.setUserId(rs.getString("user_id"));
                rider.setUsername(rs.getString("username"));
                rider.setEmail(rs.getString("email"));
                rider.setPhone(rs.getString("phone"));
                rider.setRiderId(rs.getString("rider_id"));
                rider.setVehicleType(rs.getString("vehicle_type"));
                rider.setLicenseNumber(rs.getString("license_number"));
                rider.setAvailabilityStatus(rs.getString("availability_status"));
                rider.setRating(rs.getFloat("rating"));
                
                riders.add(rider);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return riders;
    }
    
    // Get rider by ID
    public PickupRider getRiderById(String riderId) {
        String query = "SELECT u.*, pr.rider_id, pr.vehicle_type, pr.license_number, " +
                      "pr.availability_status, pr.rating " +
                      "FROM users u " +
                      "JOIN pickup_riders pr ON u.user_id = pr.user_id " +
                      "WHERE pr.rider_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                PickupRider rider = new PickupRider();
                rider.setUserId(rs.getString("user_id"));
                rider.setUsername(rs.getString("username"));
                rider.setEmail(rs.getString("email"));
                rider.setPhone(rs.getString("phone"));
                rider.setRiderId(rs.getString("rider_id"));
                rider.setVehicleType(rs.getString("vehicle_type"));
                rider.setLicenseNumber(rs.getString("license_number"));
                rider.setAvailabilityStatus(rs.getString("availability_status"));
                rider.setRating(rs.getFloat("rating"));
                
                return rider;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get rider statistics
    public int getTotalRiders() {
        String query = "SELECT COUNT(*) as count FROM pickup_riders";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Get available rider count
    public int getAvailableRiderCount() {
        String query = "SELECT COUNT(*) as count FROM pickup_riders " +
                      "WHERE availability_status = 'Available'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}