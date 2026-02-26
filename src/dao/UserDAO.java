package dao;
import java.sql.*;
import model.Customer;
import model.DeliveryRider;
import model.Dispatcher;
import model.Manager;
import model.PickupRider;
import model.WarehouseStaff;
import util.DBConnection;


public class UserDAO {

	public Customer login(String username, String password) {
		 String query = "SELECT u.*, c.customer_id, c.address " +
                 "FROM users u " +
                 "JOIN customers c ON u.user_id = c.user_id " +
                 "WHERE u.username = ? AND u.password = ? AND u.role = 'customer'";
		 
		 try(Connection con = DBConnection.getConnection();
				 PreparedStatement pst = con.prepareStatement(query)) {
		
			 
			 	pst.setString(1, username);
			 	pst.setString(2, password);
			 	ResultSet rs = pst.executeQuery();
			 	
			 	if(rs.next())
			 	{
			 		Customer customer = new Customer();
			 		customer.setUserId(rs.getString("user_id"));
		            customer.setUsername(rs.getString("username"));
		            customer.setEmail(rs.getString("email"));
		            customer.setPhone(rs.getString("phone"));
		            customer.setCustomerId(rs.getString("customer_id"));
		            customer.setAddress(rs.getString("address"));
		                
		            System.out.println("✅ Login successful: " + username);
		            return customer;
			 	}
			 	
		 }
		 catch (SQLException e) {
	            System.err.println("❌ Login failed!");
	            e.printStackTrace();
	        }
		 return null;
	}
	// Registers a new customer: creates a user row and customer row inside a transaction
    public boolean registerCustomer(Customer customer) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertCustomer = "INSERT INTO customers (customer_id, user_id, address) VALUES (?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstCust = conn.prepareStatement(insertCustomer)) {

                String userId = "USR_" + System.currentTimeMillis();
                String customerId = "CUST_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, customer.getUsername());
                pstUser.setString(3, customer.getPassword());
                pstUser.setString(4, customer.getEmail());
                pstUser.setString(5, customer.getPhone());
                pstUser.setString(6, "customer");
                pstUser.executeUpdate();

                pstCust.setString(1, customerId);
                pstCust.setString(2, userId);
                pstCust.setString(3, customer.getAddress());
                pstCust.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new customer: " + customer.getUsername());
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public Manager loginManager(String username, String password) {
        String query = "SELECT u.*, m.manager_id, m.department " +
                      "FROM users u " +
                      "JOIN managers m ON u.user_id = m.user_id " +
                      "WHERE u.username = ? AND u.password = ? AND u.role = 'manager'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Manager manager = new Manager();
                manager.setUserId(rs.getString("user_id"));
                manager.setUsername(rs.getString("username"));
                manager.setEmail(rs.getString("email"));
                manager.setPhone(rs.getString("phone"));
                manager.setManagerId(rs.getString("manager_id"));
                manager.setDepartment(rs.getString("department"));
                
                System.out.println("✅ Manager login successful: " + username);
                return manager;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Manager login failed!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Dispatcher loginDispatcher(String username,String password)
    {
    	 String query = "SELECT u.*, d.dispatcher_id, d.assigned_zone, d.availability_status " +
                 "FROM users u " +
                 "JOIN dispatchers d ON u.user_id = d.user_id " +
                 "WHERE u.username = ? AND u.password = ? AND u.role = 'dispatcher'";
    	 
    	 try (Connection con=DBConnection.getConnection();
    			 PreparedStatement pst=con.prepareStatement(query))
    	 {
    		 pst.setString(1, username);
			 pst.setString(2, password);
			 ResultSet rs = pst.executeQuery();
			 
			 if(rs.next())
			 {
				 Dispatcher dispatcher = new Dispatcher();
				 dispatcher.setUserId(rs.getString("user_id"));
				 dispatcher.setUsername(rs.getString("username"));
				 dispatcher.setEmail(rs.getString("email"));
				 dispatcher.setPhone(rs.getString("phone"));
				 dispatcher.setDispatcherId(rs.getString("dispatcher_id"));
				 dispatcher.setAssignedZone(rs.getString("assigned_zone"));
				 dispatcher.setAvailabilityStatus(rs.getString("availability_status"));
				 
				 System.out.println("✅ Dispatcher login successful: " + username);
				 return dispatcher;
			 }
		 }
		 catch (SQLException e) {
			 System.err.println("❌ Dispatcher login failed!");
			 e.printStackTrace();
		 
    	 }
    	return null;
    }
    
    public PickupRider loginPickupRider(String username, String password) {
    	String query = "SELECT u.*, pr.rider_id, pr.vehicle_type, pr.license_number, " +
                "pr.availability_status, pr.rating " +
                "FROM users u " +
                "JOIN pickup_riders pr ON u.user_id = pr.user_id " +
                "WHERE u.username = ? AND u.password = ? AND u.role = 'pickup_rider'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
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
                
                System.out.println("✅ Pickup Rider login successful: " + username);
                return rider;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Pickup Rider login failed!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public DeliveryRider loginDeliveryRider(String username, String password) {
    	String query = "SELECT u.*, dr.rider_id, dr.vehicle_type, dr.license_number, " +
                "dr.availability_status, dr.rating " +
                "FROM users u " +
                "JOIN delivery_riders dr ON u.user_id = dr.user_id " +
                "WHERE u.username = ? AND u.password = ? AND u.role = 'delivery_rider'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
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
                
                System.out.println("✅ Delivery Rider login successful: " + username);
                return rider;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Pickup Rider login failed!");
            e.printStackTrace();
        }
        
        return null;
    }
 // Warehouse Staff Login
    public WarehouseStaff loginWarehouseStaff(String username, String password) {
        String query = "SELECT u.*, ws.staff_id, ws.warehouse_id, ws.shift " +
                      "FROM users u " +
                      "JOIN warehouse_staff ws ON u.user_id = ws.user_id " +
                      "WHERE u.username = ? AND u.password = ? AND u.role = 'warehouse_staff'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                WarehouseStaff staff = new WarehouseStaff();
                staff.setUserId(rs.getString("user_id"));
                staff.setUsername(rs.getString("username"));
                staff.setEmail(rs.getString("email"));
                staff.setPhone(rs.getString("phone"));
                staff.setStaffId(rs.getString("staff_id"));
                staff.setWarehouseId(rs.getString("warehouse_id"));
                staff.setShift(rs.getString("shift"));
                
                System.out.println("✅ Warehouse Staff login successful: " + username);
                return staff;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Warehouse Staff login failed!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Register Manager
    public boolean registerManager(String username, String password, String email, String phone, String department) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertManager = "INSERT INTO managers (manager_id, user_id, department) VALUES (?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstManager = conn.prepareStatement(insertManager)) {

                String userId = "USR_" + System.currentTimeMillis();
                String managerId = "MGR_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, username);
                pstUser.setString(3, password);
                pstUser.setString(4, email);
                pstUser.setString(5, phone);
                pstUser.setString(6, "manager");
                pstUser.executeUpdate();

                pstManager.setString(1, managerId);
                pstManager.setString(2, userId);
                pstManager.setString(3, department);
                pstManager.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new manager: " + username);
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // Register Dispatcher
    public boolean registerDispatcher(String username, String password, String email, String phone, String zone) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertDispatcher = "INSERT INTO dispatchers (dispatcher_id, user_id, assigned_zone, availability_status) VALUES (?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstDispatcher = conn.prepareStatement(insertDispatcher)) {

                String userId = "USR_" + System.currentTimeMillis();
                String dispatcherId = "DISP_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, username);
                pstUser.setString(3, password);
                pstUser.setString(4, email);
                pstUser.setString(5, phone);
                pstUser.setString(6, "dispatcher");
                pstUser.executeUpdate();

                pstDispatcher.setString(1, dispatcherId);
                pstDispatcher.setString(2, userId);
                pstDispatcher.setString(3, zone);
                pstDispatcher.setString(4, "Available");
                pstDispatcher.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new dispatcher: " + username);
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // Register Pickup Rider
    public boolean registerPickupRider(String username, String password, String email, String phone, String address, String vehicleType, String licenseNumber) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertPickupRider = "INSERT INTO pickup_riders (rider_id, user_id, vehicle_type, license_number, availability_status, rating) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstRider = conn.prepareStatement(insertPickupRider)) {

                String userId = "USR_" + System.currentTimeMillis();
                String riderId = "PR_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, username);
                pstUser.setString(3, password);
                pstUser.setString(4, email);
                pstUser.setString(5, phone);
                pstUser.setString(6, "pickup_rider");
                pstUser.executeUpdate();

                pstRider.setString(1, riderId);
                pstRider.setString(2, userId);
                pstRider.setString(3, vehicleType);
                pstRider.setString(4, licenseNumber);
                pstRider.setString(5, "Available");
                pstRider.setFloat(6, 5.0f); // Default rating
                pstRider.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new pickup rider: " + username);
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // Register Delivery Rider
    public boolean registerDeliveryRider(String username, String password, String email, String phone, String address, String vehicleType, String licenseNumber) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertDeliveryRider = "INSERT INTO delivery_riders (rider_id, user_id, vehicle_type, license_number, availability_status, rating) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstRider = conn.prepareStatement(insertDeliveryRider)) {

                String userId = "USR_" + System.currentTimeMillis();
                String riderId = "DR_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, username);
                pstUser.setString(3, password);
                pstUser.setString(4, email);
                pstUser.setString(5, phone);
                pstUser.setString(6, "delivery_rider");
                pstUser.executeUpdate();

                pstRider.setString(1, riderId);
                pstRider.setString(2, userId);
                pstRider.setString(3, vehicleType);
                pstRider.setString(4, licenseNumber);
                pstRider.setString(5, "Available");
                pstRider.setFloat(6, 5.0f); // Default rating
                pstRider.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new delivery rider: " + username);
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // Register Warehouse Staff
    public boolean registerWarehouseStaff(String username, String password, String email, String phone, String warehouseId, String shift) {
        String insertUser = "INSERT INTO users (user_id, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?);";
        String insertWarehouseStaff = "INSERT INTO warehouse_staff (staff_id, user_id, warehouse_id, shift) VALUES (?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstUser = conn.prepareStatement(insertUser);
                 PreparedStatement pstStaff = conn.prepareStatement(insertWarehouseStaff)) {

                String userId = "USR_" + System.currentTimeMillis();
                String staffId = "WS_" + System.currentTimeMillis();

                pstUser.setString(1, userId);
                pstUser.setString(2, username);
                pstUser.setString(3, password);
                pstUser.setString(4, email);
                pstUser.setString(5, phone);
                pstUser.setString(6, "warehouse_staff");
                pstUser.executeUpdate();

                pstStaff.setString(1, staffId);
                pstStaff.setString(2, userId);
                pstStaff.setString(3, warehouseId);
                pstStaff.setString(4, shift);
                pstStaff.executeUpdate();

                conn.commit();
                System.out.println("✅ Registered new warehouse staff: " + username);
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
}