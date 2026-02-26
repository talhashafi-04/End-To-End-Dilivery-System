package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Notification;
import util.DBConnection;

public class NotificationDAO {
    
    // Create notification
    public boolean createNotification(Notification notification) {
        String query = "INSERT INTO notifications (notification_id, customer_id, order_id, " +
                      "message, type, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, notification.getNotificationId());
            pstmt.setString(2, notification.getCustomerId());
            pstmt.setString(3, notification.getOrderId());
            pstmt.setString(4, notification.getMessage());
            pstmt.setString(5, notification.getType());
            pstmt.setBoolean(6, notification.isRead());
            
            int rows = pstmt.executeUpdate();
            System.out.println("✅ Notification created: " + notification.getNotificationId());
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating notification!");
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all notifications for a customer
    public List<Notification> getCustomerNotifications(String customerId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE customer_id = ? " +
                      "ORDER BY created_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getString("notification_id"));
                notification.setCustomerId(rs.getString("customer_id"));
                notification.setOrderId(rs.getString("order_id"));
                notification.setMessage(rs.getString("message"));
                notification.setType(rs.getString("type"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedDate(rs.getTimestamp("created_date"));
                
                notifications.add(notification);
            }
            
            System.out.println("✅ Loaded " + notifications.size() + " notifications");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading notifications!");
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    // Alias method for compatibility
    public List<Notification> getNotificationsByCustomer(String customerId) {
        return getCustomerNotifications(customerId);
    }
    
    // Get unread count
    public int getUnreadCount(String customerId) {
        String query = "SELECT COUNT(*) as count FROM notifications " +
                      "WHERE customer_id = ? AND is_read = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Mark notification as read
    public boolean markAsRead(String notificationId) {
        String query = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, notificationId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Mark all as read for a customer
    public boolean markAllAsRead(String customerId) {
        String query = "UPDATE notifications SET is_read = TRUE WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}