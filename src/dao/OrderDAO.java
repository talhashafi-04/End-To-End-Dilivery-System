package dao;

import model.Order;
import model.Parcel;
import model.ShipmentPicture;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public boolean createOrder(Order order, Parcel parcel, String imagePath) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Insert Order
            String orderQuery = "INSERT INTO orders (order_id, customer_id, sender_name, " +
                              "sender_phone, sender_address, receiver_name, receiver_phone, " +
                              "receiver_address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getCustomerId());
            orderStmt.setString(3, order.getSenderName());
            orderStmt.setString(4, order.getSenderPhone());
            orderStmt.setString(5, order.getSenderAddress());
            orderStmt.setString(6, order.getReceiverName());
            orderStmt.setString(7, order.getReceiverPhone());
            orderStmt.setString(8, order.getReceiverAddress());
            orderStmt.setString(9, order.getStatus());
            orderStmt.executeUpdate();
            
            // 2. Insert Parcel
            String parcelQuery = "INSERT INTO parcels (parcel_id, order_id, weight, " +
                               "dimensions, description) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement parcelStmt = conn.prepareStatement(parcelQuery);
            parcelStmt.setString(1, parcel.getParcelId());
            parcelStmt.setString(2, order.getOrderId());
            parcelStmt.setDouble(3, parcel.getWeight());
            parcelStmt.setString(4, parcel.getDimensions());
            parcelStmt.setString(5, parcel.getDescription());
            parcelStmt.executeUpdate();
            
            // 3. Insert Picture if provided
            if (imagePath != null && !imagePath.isEmpty()) {
                String pictureQuery = "INSERT INTO shipment_pictures (picture_id, order_id, " +
                                    "image_path) VALUES (?, ?, ?)";
                
                PreparedStatement pictureStmt = conn.prepareStatement(pictureQuery);
                pictureStmt.setString(1, "PIC_" + System.currentTimeMillis());
                pictureStmt.setString(2, order.getOrderId());
                pictureStmt.setString(3, imagePath);
                pictureStmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            System.out.println("✅ Order created successfully: " + order.getOrderId());
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error creating order!");
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
    
 // Add this method to OrderDAO class
    public boolean createOrderWithNotification(Order order, Parcel parcel, String imagePath, String customerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Insert Order
            String orderQuery = "INSERT INTO orders (order_id, customer_id, sender_name, " +
                              "sender_phone, sender_address, receiver_name, receiver_phone, " +
                              "receiver_address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getCustomerId());
            orderStmt.setString(3, order.getSenderName());
            orderStmt.setString(4, order.getSenderPhone());
            orderStmt.setString(5, order.getSenderAddress());
            orderStmt.setString(6, order.getReceiverName());
            orderStmt.setString(7, order.getReceiverPhone());
            orderStmt.setString(8, order.getReceiverAddress());
            orderStmt.setString(9, order.getStatus());
            orderStmt.executeUpdate();
            
            // 2. Insert Parcel
            String parcelQuery = "INSERT INTO parcels (parcel_id, order_id, weight, " +
                               "dimensions, description) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement parcelStmt = conn.prepareStatement(parcelQuery);
            parcelStmt.setString(1, parcel.getParcelId());
            parcelStmt.setString(2, order.getOrderId());
            parcelStmt.setDouble(3, parcel.getWeight());
            parcelStmt.setString(4, parcel.getDimensions());
            parcelStmt.setString(5, parcel.getDescription());
            parcelStmt.executeUpdate();
            
            // 3. Insert Picture if provided
            if (imagePath != null && !imagePath.isEmpty()) {
                String pictureQuery = "INSERT INTO shipment_pictures (picture_id, order_id, " +
                                    "image_path) VALUES (?, ?, ?)";
                
                PreparedStatement pictureStmt = conn.prepareStatement(pictureQuery);
                pictureStmt.setString(1, "PIC_" + System.currentTimeMillis());
                pictureStmt.setString(2, order.getOrderId());
                pictureStmt.setString(3, imagePath);
                pictureStmt.executeUpdate();
            }
            
            // 4. Create Status History
            String historyQuery = "INSERT INTO status_history (history_id, order_id, " +
                                "old_status, new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, order.getOrderId());
            historyStmt.setString(3, null); // No old status for new order
            historyStmt.setString(4, "Pending");
            historyStmt.setString(5, customerId);
            historyStmt.setString(6, "Order created by customer");
            historyStmt.executeUpdate();
            
            // 5. Create Notification
            String notificationQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                     "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement notifStmt = conn.prepareStatement(notificationQuery);
            notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
            notifStmt.setString(2, customerId);
            notifStmt.setString(3, order.getOrderId());
            notifStmt.setString(4, "Your delivery request has been submitted successfully! " +
                                   "Order ID: " + order.getOrderId() + ". Status: Pending. " +
                                   "Manager will review your request soon.");
            notifStmt.setString(5, "success");
            notifStmt.executeUpdate();
            
            conn.commit(); // Commit transaction
            System.out.println("✅ Order, notification, and history created successfully!");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error creating order!");
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
    
    // Fetch orders for a specific customer including one shipment picture path (if any)
    public java.util.List<model.Order> getOrdersByCustomer(String customerId) {
        java.util.List<model.Order> orders = new java.util.ArrayList<>();
        String query = "SELECT o.*, sp.image_path FROM orders o " +
                       "LEFT JOIN shipment_pictures sp ON o.order_id = sp.order_id " +
                       "WHERE o.customer_id = ? ORDER BY o.order_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.Order order = new model.Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setSenderName(rs.getString("sender_name"));
                order.setSenderPhone(rs.getString("sender_phone"));
                order.setSenderAddress(rs.getString("sender_address"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));

                // store image path in the approvedBy field as a lightweight hack OR extend Order model
                // Prefer to add a transient property via a Map; here we'll use the approvedBy as holder
                String imagePath = rs.getString("image_path");
                if (imagePath != null) {
                    order.setApprovedBy(imagePath);
                }

                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE status = 'Pending' ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setSenderName(rs.getString("sender_name"));
                order.setSenderPhone(rs.getString("sender_phone"));
                order.setSenderAddress(rs.getString("sender_address"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                orders.add(order);
            }
            
            System.out.println("✅ Loaded " + orders.size() + " pending orders");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading pending orders!");
            e.printStackTrace();
        }
        
        return orders;
    }
    public Parcel getParcelByOrderId(String orderId) {
        String query = "SELECT * FROM parcels WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Parcel parcel = new Parcel();
                parcel.setParcelId(rs.getString("parcel_id"));
                parcel.setOrderId(rs.getString("order_id"));
                parcel.setWeight(rs.getDouble("weight"));
                parcel.setDimensions(rs.getString("dimensions"));
                parcel.setDescription(rs.getString("description"));
                return parcel;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Get image path for an order
    public String getImagePathByOrderId(String orderId) {
        String query = "SELECT image_path FROM shipment_pictures WHERE order_id = ? LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("image_path");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Approve order
    public boolean approveOrder(String orderId, String managerId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Update order status
            String updateQuery = "UPDATE orders SET status = 'Approved', approved_by = ? WHERE order_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, managerId);
            updateStmt.setString(2, orderId);
            updateStmt.executeUpdate();
            
            // 2. Get customer ID
            String getCustomerQuery = "SELECT customer_id FROM orders WHERE order_id = ?";
            PreparedStatement getCustomerStmt = conn.prepareStatement(getCustomerQuery);
            getCustomerStmt.setString(1, orderId);
            ResultSet rs = getCustomerStmt.executeQuery();
            
            String customerId = null;
            if (rs.next()) {
                customerId = rs.getString("customer_id");
            }
            
            // 3. Create status history
            String historyQuery = "INSERT INTO status_history (history_id, order_id, old_status, " +
                                "new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, orderId);
            historyStmt.setString(3, "Pending");
            historyStmt.setString(4, "Approved");
            historyStmt.setString(5, managerId);
            historyStmt.setString(6, "Order approved by manager");
            historyStmt.executeUpdate();
            
            // 4. Create notification for customer
            if (customerId != null) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, orderId);
                notifStmt.setString(4, "Great news! Your order " + orderId + " has been APPROVED! " +
                                     "A dispatcher will assign a rider for pickup soon.");
                notifStmt.setString(5, "success");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Order approved: " + orderId);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error approving order!");
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

    // Reject order
    public boolean rejectOrder(String orderId, String managerId, String reason) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Update order status
            String updateQuery = "UPDATE orders SET status = 'Rejected', approved_by = ? WHERE order_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, managerId);
            updateStmt.setString(2, orderId);
            updateStmt.executeUpdate();
            
            // 2. Get customer ID
            String getCustomerQuery = "SELECT customer_id FROM orders WHERE order_id = ?";
            PreparedStatement getCustomerStmt = conn.prepareStatement(getCustomerQuery);
            getCustomerStmt.setString(1, orderId);
            ResultSet rs = getCustomerStmt.executeQuery();
            
            String customerId = null;
            if (rs.next()) {
                customerId = rs.getString("customer_id");
            }
            
            // 3. Create status history
            String historyQuery = "INSERT INTO status_history (history_id, order_id, old_status, " +
                                "new_status, changed_by, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            historyStmt.setString(1, "HIST_" + System.currentTimeMillis());
            historyStmt.setString(2, orderId);
            historyStmt.setString(3, "Pending");
            historyStmt.setString(4, "Rejected");
            historyStmt.setString(5, managerId);
            historyStmt.setString(6, "Order rejected by manager. Reason: " + reason);
            historyStmt.executeUpdate();
            
            // 4. Create notification for customer
            if (customerId != null) {
                String notifQuery = "INSERT INTO notifications (notification_id, customer_id, " +
                                  "order_id, message, type) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifQuery);
                notifStmt.setString(1, "NOTIF_" + System.currentTimeMillis());
                notifStmt.setString(2, customerId);
                notifStmt.setString(3, orderId);
                notifStmt.setString(4, "Your order " + orderId + " has been REJECTED. " +
                                     "Reason: " + reason + ". You can drop off the parcel at our office instead.");
                notifStmt.setString(5, "warning");
                notifStmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✅ Order rejected: " + orderId);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error rejecting order!");
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
    
 // Get approved orders (not yet assigned to pickup rider)
    public List<Order> getApprovedOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE status = 'Approved' ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setSenderName(rs.getString("sender_name"));
                order.setSenderPhone(rs.getString("sender_phone"));
                order.setSenderAddress(rs.getString("sender_address"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setApprovedBy(rs.getString("approved_by"));
                
                orders.add(order);
            }
            
            System.out.println("✅ Loaded " + orders.size() + " approved orders");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading approved orders!");
            e.printStackTrace();
        }
        
        return orders;
    }
    public Order getOrderById(String orderId) {
        String query = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setSenderName(rs.getString("sender_name"));
                order.setSenderPhone(rs.getString("sender_phone"));
                order.setSenderAddress(rs.getString("sender_address"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setApprovedBy(rs.getString("approved_by"));
                return order;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading order by ID!");
            e.printStackTrace();
        }
        
        return null;
    }
    
 // Get collected orders (in warehouse, ready for delivery assignment)
    public List<Order> getCollectedOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE status = 'Picked Up' ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getString("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setSenderName(rs.getString("sender_name"));
                order.setSenderPhone(rs.getString("sender_phone"));
                order.setSenderAddress(rs.getString("sender_address"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                orders.add(order);
            }
            
            System.out.println("✅ Loaded " + orders.size() + " collected orders");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading collected orders!");
            e.printStackTrace();
        }
        
        return orders;
    }

    // Get order by ID
   
}