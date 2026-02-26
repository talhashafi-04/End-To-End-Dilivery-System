package dao;

import model.PickupAssignment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {
    
    // Helper method to get valid ENUM values from database
//    private List<String> getEnumOptions(Connection conn, String tableName, String columnName) {
//        List<String> options = new ArrayList<>();
//        String query = "SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
//                      "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
//        
//        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, tableName);
//            pstmt.setString(2, columnName);
//            ResultSet rs = pstmt.executeQuery();
//            
//            if (rs.next()) {
//                String columnType = rs.getString("COLUMN_TYPE");
//                // Parse enum values: enum('Pending','Assigned','Confirmed','Completed')
//                if (columnType.startsWith("enum(")) {
//                    String enumValues = columnType.substring(5, columnType.length() - 1);
//                    String[] values = enumValues.split(",");
//                    for (String value : values) {
//                        // Remove quotes from each value
//                        options.add(value.replaceAll("'", "").trim());
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("⚠️ Could not read enum options: " + e.getMessage());
//        }
//        
//        return options;
//    }
    
    // Create pickup assignment
    public boolean createPickupAssignment(PickupAssignment assignment, String customerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insert pickup assignment with correct status and required fields
            String assignmentQuery = "INSERT INTO pickup_assignments (assignment_id, order_id, " +
                                   "dispatcher_id, rider_id, status, assignment_date) VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement assignmentStmt = conn.prepareStatement(assignmentQuery);
            assignmentStmt.setString(1, assignment.getAssignmentId());
            assignmentStmt.setString(2, assignment.getOrderId());
            assignmentStmt.setString(3, assignment.getDispatcherId());
            assignmentStmt.setString(4, assignment.getRiderId());
            
            // Ensure status matches the ENUM values
            String assignmentStatus = "Approved"; // or get from assignment if it matches ENUM
            if (assignment.getStatus() != null) {
                assignmentStatus = assignment.getStatus();
            }
            assignmentStmt.setString(5, assignmentStatus);
            assignmentStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            assignmentStmt.executeUpdate();
            
            // 2. Update order status - use valid ENUM value from orders table
            String orderQuery = "UPDATE orders SET status = 'Assigned' WHERE order_id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, assignment.getOrderId());
            int updatedRows = orderStmt.executeUpdate();
            
            if (updatedRows == 0) {
                throw new SQLException("Order not found: " + assignment.getOrderId());
            }
            
            // 3. Update rider availability
            String riderQuery = "UPDATE pickup_riders SET availability_status = 'Busy' WHERE rider_id = ?";
            PreparedStatement riderStmt = conn.prepareStatement(riderQuery);
            riderStmt.setString(1, assignment.getRiderId());
            riderStmt.executeUpdate();
            
            // 4. Create status history - use valid status values
            String historyQuery = "INSERT INTO status_history (history_id, order_id, old_status, " +
                                "new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, assignment.getOrderId());
            historyStmt.setString(3, "Approved"); // Make sure this exists in orders status ENUM
            historyStmt.setString(4, "Assigned"); // Make sure this exists in orders status ENUM
            historyStmt.setString(5, assignment.getDispatcherId());
            historyStmt.setString(6, "Pickup rider assigned by dispatcher");
            historyStmt.executeUpdate();
            
            // 5. Create notification for customer
            if (customerId != null && !customerId.trim().isEmpty()) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, assignment.getOrderId());
                notifStmt.setString(4, "A pickup rider has been assigned to collect your parcel! " +
                                     "Order: " + assignment.getOrderId() + ". The rider will arrive soon.");
                notifStmt.setString(5, "info");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Pickup assignment created: " + assignment.getAssignmentId());
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error creating pickup assignment: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get assignments by rider ID
    public List<PickupAssignment> getAssignmentsByRider(String riderId) {
        List<PickupAssignment> assignments = new ArrayList<>();
        String query = "SELECT * FROM pickup_assignments WHERE rider_id = ? " +
                      "ORDER BY assignment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PickupAssignment assignment = new PickupAssignment();
                assignment.setAssignmentId(rs.getString("assignment_id"));
                assignment.setOrderId(rs.getString("order_id"));
                assignment.setDispatcherId(rs.getString("dispatcher_id"));
                assignment.setRiderId(rs.getString("rider_id"));
                assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                assignment.setConfirmedDate(rs.getTimestamp("confirmed_date"));
                assignment.setCompletedDate(rs.getTimestamp("completed_date"));
                assignment.setStatus(rs.getString("status"));
                
                assignments.add(assignment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return assignments;
    }
    
    // Check if order already has assignment
    public boolean hasPickupAssignment(String orderId) {
        String query = "SELECT COUNT(*) as count FROM pickup_assignments WHERE order_id = ?";
        
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
    
    // Get count of assignments made today by a dispatcher
    public int getAssignmentCountToday(String dispatcherId) {
        String query = "SELECT COUNT(*) as count FROM pickup_assignments " +
                      "WHERE dispatcher_id = ? AND DATE(assignment_date) = CURDATE()";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, dispatcherId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Get pending assignments for a rider
    public List<PickupAssignment> getPendingAssignmentsByRider(String riderId) {
        List<PickupAssignment> assignments = new ArrayList<>();
        String query = "SELECT * FROM pickup_assignments " +
                      "WHERE rider_id = ?" +
                      "ORDER BY assignment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PickupAssignment assignment = new PickupAssignment();
                assignment.setAssignmentId(rs.getString("assignment_id"));
                assignment.setOrderId(rs.getString("order_id"));
                assignment.setDispatcherId(rs.getString("dispatcher_id"));
                assignment.setRiderId(rs.getString("rider_id"));
                assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                assignment.setConfirmedDate(rs.getTimestamp("confirmed_date"));
                assignment.setCompletedDate(rs.getTimestamp("completed_date"));
                assignment.setStatus(rs.getString("status"));
                
                assignments.add(assignment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return assignments;
    }
    
    // Update assignment status
    public boolean updateAssignmentStatus(String assignmentId, String newStatus) {
        String query = "UPDATE pickup_assignments SET status = ? WHERE assignment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setString(2, assignmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Confirm assignment (when rider accepts)
    public boolean confirmAssignment(String assignmentId) {
        String query = "UPDATE pickup_assignments SET status = 'Confirmed', " +
                      "confirmed_date = CURRENT_TIMESTAMP WHERE assignment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, assignmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Complete assignment (when rider completes pickup)
//    public boolean completeAssignment(String assignmentId) {
//        String query = "UPDATE pickup_assignments SET status = 'Completed', " +
//                      "completed_date = CURRENT_TIMESTAMP WHERE assignment_id = ?";
//        
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//            
//            pstmt.setString(1, assignmentId);
//            int rowsAffected = pstmt.executeUpdate();
//            return rowsAffected > 0;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    
    public boolean completePickup(String assignmentId, String customerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Get assignment details
            String getAssignmentQuery = "SELECT order_id FROM pickup_assignments WHERE assignment_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getAssignmentQuery);
            getStmt.setString(1, assignmentId);
            ResultSet rs = getStmt.executeQuery();
            
            String orderId = null;
            if (rs.next()) {
                orderId = rs.getString("order_id");
            }
            
            if (orderId == null) {
                throw new SQLException("Assignment not found");
            }
            
            // 2. Update assignment status
            String updateAssignmentQuery = "UPDATE pickup_assignments SET status = 'Completed', " +
                                          "completed_date = CURRENT_TIMESTAMP WHERE assignment_id = ?";
            PreparedStatement assignmentStmt = conn.prepareStatement(updateAssignmentQuery);
            assignmentStmt.setString(1, assignmentId);
            assignmentStmt.executeUpdate();
            
            // 3. Update order status
            String updateOrderQuery = "UPDATE orders SET status = 'Picked Up' WHERE order_id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(updateOrderQuery);
            orderStmt.setString(1, orderId);
            orderStmt.executeUpdate();
            
//            // 4. Update parcel status
//            String updateParcelQuery = "UPDATE parcels SET status = 'Collected', " +
//                                      "collected_date = CURRENT_TIMESTAMP WHERE order_id = ?";
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
            historyStmt.setString(3, "Pickup Assigned");
            historyStmt.setString(4, "Collected");
            historyStmt.setString(5, assignmentId); // Use assignment ID as reference
            historyStmt.setString(6, "Parcel collected by pickup rider");
            historyStmt.executeUpdate();
            
            // 6. Notify customer
            if (customerId != null) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, orderId);
                notifStmt.setString(4, "Your parcel has been collected! Order: " + orderId + 
                                     ". It's on its way to the warehouse for processing.");
                notifStmt.setString(5, "success");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Pickup completed: " + assignmentId);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error completing pickup!");
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
    public PickupAssignment getAssignmentWithDetails(String assignmentId) {
        String query = "SELECT pa.*, o.sender_name, o.sender_phone, o.sender_address, " +
                      "o.customer_id FROM pickup_assignments pa " +
                      "JOIN orders o ON pa.order_id = o.order_id " +
                      "WHERE pa.assignment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, assignmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                PickupAssignment assignment = new PickupAssignment();
                assignment.setAssignmentId(rs.getString("assignment_id"));
                assignment.setOrderId(rs.getString("order_id"));
                assignment.setDispatcherId(rs.getString("dispatcher_id"));
                assignment.setRiderId(rs.getString("rider_id"));
                assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                assignment.setStatus(rs.getString("status"));
                
                return assignment;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}