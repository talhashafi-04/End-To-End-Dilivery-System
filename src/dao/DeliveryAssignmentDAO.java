package dao;

import model.DeliveryAssignment;
import model.DeliveryRider;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryAssignmentDAO {
    
    // Get available delivery riders
    public List<DeliveryRider> getAvailableDeliveryRiders() {
        List<DeliveryRider> riders = new ArrayList<>();
        String query = "SELECT u.*, dr.rider_id, dr.vehicle_type, dr.license_number, " +
                      "dr.availability_status, dr.rating " +
                      "FROM users u " +
                      "JOIN delivery_riders dr ON u.user_id = dr.user_id " +
                      "WHERE dr.availability_status = 'Available' " +
                      "ORDER BY dr.rating DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DeliveryRider rider = new DeliveryRider();
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
            
            System.out.println("✅ Loaded " + riders.size() + " available delivery riders");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading delivery riders!");
            e.printStackTrace();
        }
        
        return riders;
    }
    
//    public boolean startDelivery(String assignmentId) throws SQLException {
//    	 String orderQuery = "UPDATE orders SET status = 'In Transit' WHERE order_id = ?";
//    	 
//    	 String assignmentQuery="Update delivery_assignments SET status='In Progress', confirmed_date=CURRENT_TIMESTAMP WHERE assignment_id=?";
//		 
//		 try (Connection conn = DBConnection.getConnection();
//			  PreparedStatement pstmt = conn.prepareStatement(assignmentQuery)) {
//
//			 pstmt.setString(1, assignmentId);
//
//			 int rowsAffected = pstmt.executeUpdate();
//			 return rowsAffected > 0;
//		 }
//        
//        
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(orderQuery)) {
//
//            pstmt.setString(1, assignmentId);
//
//            int rowsAffected = pstmt.executeUpdate();
//            return rowsAffected > 0;
//        }
//    }
    
    public boolean startDelivery(String assignmentId) throws SQLException {
        String assignmentQuery = 
            "UPDATE delivery_assignments " +
            "SET status = 'In Progress', confirmed_date = CURRENT_TIMESTAMP " +
            "WHERE assignment_id = ?";

        String orderQuery = 
            "UPDATE orders " +
            "SET status = 'In Transit' " +
            "WHERE order_id = (SELECT order_id FROM delivery_assignments WHERE assignment_id = ?)";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);  // Start transaction

            // Update delivery_assignments table
            try (PreparedStatement stmt1 = conn.prepareStatement(assignmentQuery)) {
                stmt1.setString(1, assignmentId);
                int rows1 = stmt1.executeUpdate();

                if (rows1 == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Update orders table
            try (PreparedStatement stmt2 = conn.prepareStatement(orderQuery)) {
                stmt2.setString(1, assignmentId);
                int rows2 = stmt2.executeUpdate();

                if (rows2 == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();  // Both successful
            return true;
        }
    }


    
    // Create delivery assignment
    public boolean createDeliveryAssignment(DeliveryAssignment assignment, String customerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insert delivery assignment
            String assignmentQuery = "INSERT INTO delivery_assignments (assignment_id, order_id, " +
                                   "rider_id, warehouse_staff_id, status) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement assignmentStmt = conn.prepareStatement(assignmentQuery);
            assignmentStmt.setString(1, assignment.getAssignmentId());
            assignmentStmt.setString(2, assignment.getOrderId());
            assignmentStmt.setString(3, assignment.getRiderId());
            assignmentStmt.setString(4, assignment.getWarehouseStaffId());
            assignmentStmt.setString(5, assignment.getStatus());
            assignmentStmt.executeUpdate();
            
            // 2. Update order status
            String orderQuery = "UPDATE orders SET status = 'In Transit' WHERE order_id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, assignment.getOrderId());
            orderStmt.executeUpdate();
            
            // 3. Update rider availability
            String riderQuery = "UPDATE delivery_riders SET availability_status = 'Busy' WHERE rider_id = ?";
            PreparedStatement riderStmt = conn.prepareStatement(riderQuery);
            riderStmt.setString(1, assignment.getRiderId());
            riderStmt.executeUpdate();
            
            // 4. Create status history
            String historyQuery = "INSERT INTO status_history (history_id, order_id, old_status, " +
                                "new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, assignment.getOrderId());
            historyStmt.setString(3, "Collected");
            historyStmt.setString(4, "Out for Delivery");
            historyStmt.setString(5, assignment.getWarehouseStaffId());
            historyStmt.setString(6, "Delivery rider assigned from warehouse");
            historyStmt.executeUpdate();
            
            // 5. Notify customer
            if (customerId != null) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, assignment.getOrderId());
                notifStmt.setString(4, "Your parcel is out for delivery! Order: " + assignment.getOrderId() + 
                                     ". The delivery rider will arrive soon at: " + 
                                     getDeliveryAddress(conn, assignment.getOrderId()));
                notifStmt.setString(5, "info");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Delivery assignment created: " + assignment.getAssignmentId());
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error creating delivery assignment!");
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get pending delivery assignments for a rider
    public List<DeliveryAssignment> getPendingDeliveryAssignments(String riderId) {
        List<DeliveryAssignment> assignments = new ArrayList<>();
        String query = "SELECT * FROM delivery_assignments WHERE rider_id = ? " +
                      "AND status IN ('Pending', 'Confirmed', 'In Progress') " +
                      "ORDER BY assignment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DeliveryAssignment assignment = new DeliveryAssignment();
                assignment.setAssignmentId(rs.getString("assignment_id"));
                assignment.setOrderId(rs.getString("order_id"));
                assignment.setRiderId(rs.getString("rider_id"));
                assignment.setWarehouseStaffId(rs.getString("warehouse_staff_id"));
                assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                assignment.setConfirmedDate(rs.getTimestamp("confirmed_date"));
                assignment.setCompletedDate(rs.getTimestamp("completed_date"));
                assignment.setStatus(rs.getString("status"));
                assignment.setDeliveryAttempts(rs.getInt("delivery_attempts"));
                
                assignments.add(assignment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return assignments;
    }
    
    // Complete delivery
    public boolean completeDelivery(String assignmentId, String customerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Get order ID
            String getOrderQuery = "SELECT order_id FROM delivery_assignments WHERE assignment_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getOrderQuery);
            getStmt.setString(1, assignmentId);
            ResultSet rs = getStmt.executeQuery();
            
            String orderId = null;
            if (rs.next()) {
                orderId = rs.getString("order_id");
            }
            
            // 2. Update assignment
            String updateAssignmentQuery = "UPDATE delivery_assignments SET status = 'Completed', " +
                                         "completed_date = CURRENT_TIMESTAMP WHERE assignment_id = ?";
            PreparedStatement assignmentStmt = conn.prepareStatement(updateAssignmentQuery);
            assignmentStmt.setString(1, assignmentId);
            assignmentStmt.executeUpdate();
            
            // 3. Update order status
//            String updateOrderQuery = "UPDATE orders SET status = 'Delivered', " +
//                                    "actual_delivery = CURRENT_TIMESTAMP WHERE order_id = ?";\
            
            String updateOrderQuery = "UPDATE orders SET status = 'Delivered'";
            PreparedStatement orderStmt = conn.prepareStatement(updateOrderQuery);
           // orderStmt.setString(1, orderId);
            orderStmt.executeUpdate();
            
            // 4. Update parcel
//            String updateParcelQuery = "UPDATE parcels SET status = 'Delivered', " +
//                                      "delivered_date = CURRENT_TIMESTAMP WHERE order_id = ?";
            
//            String updateParcelQuery = "UPDATE parcels SET status = 'Delivered'";
//            PreparedStatement parcelStmt = conn.prepareStatement(updateParcelQuery);
//            parcelStmt.setString(1, orderId);
//            parcelStmt.executeUpdate();
//            
            // 5. Create status history
            String historyQuery = "INSERT INTO status_history (history_id, order_id, old_status, " +
                                "new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, orderId);
            historyStmt.setString(3, "Out for Delivery");
            historyStmt.setString(4, "Delivered");
            historyStmt.setString(5, assignmentId);
            historyStmt.setString(6, "Parcel delivered successfully");
            historyStmt.executeUpdate();
            
            // 6. Notify customer
            if (customerId != null) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, orderId);
                notifStmt.setString(4, "✅ Your parcel has been delivered! Order: " + orderId + 
                                     ". Thank you for using our delivery service!");
                notifStmt.setString(5, "success");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Delivery completed: " + assignmentId);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper method to get delivery address
    private String getDeliveryAddress(Connection conn, String orderId) {
        try {
            String query = "SELECT receiver_address FROM orders WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("receiver_address");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "destination";
    }
    
    // Check if order has delivery assignment
    public boolean hasDeliveryAssignment(String orderId) {
        String query = "SELECT COUNT(*) as count FROM delivery_assignments WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}