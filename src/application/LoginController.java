package application;

import dao.UserDAO;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

public class LoginController {
    
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label statusLabel;
    
    private UserDAO userDAO = new UserDAO();
    
    @FXML
    public void initialize() {
        // Populate role dropdown
        roleComboBox.getItems().addAll(
            "Customer", 
            "Manager", 
            "Dispatcher", 
            "Pickup Rider", 
            "Delivery Rider", 
            "Warehouse Staff"
        );
        roleComboBox.setValue("Customer");
        
        // Add prompt text styling
        roleComboBox.setPromptText("Select your role");
        
        // Add enter key handlers
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
        
        // Clear status on field change
        usernameField.textProperty().addListener((obs, old, newVal) -> clearStatus());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearStatus());
        roleComboBox.valueProperty().addListener((obs, old, newVal) -> clearStatus());
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();
        
        // Validation
        if (role == null || role.isEmpty()) {
            showError("Please select a role");
            roleComboBox.requestFocus();
            return;
        }
        
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Show loading state
        showInfo("🔄 Authenticating...");
        
        try {
            switch (role) {
                case "Customer":
                    loginAsCustomer(username, password);
                    break;
                case "Manager":
                    loginAsManager(username, password);
                    break;
                case "Dispatcher":
                    loginAsDispatcher(username, password);
                    break;
                case "Pickup Rider":
                    loginAsPickupRider(username, password);
                    break;
                case "Delivery Rider":
                    loginAsDeliveryRider(username, password);
                    break;
                case "Warehouse Staff":
                    loginAsWarehouseStaff(username, password);
                    break;
                default:
                    showError("Invalid role selected");
            }
            
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loginAsCustomer(String username, String password) {
        try {
            Customer customer = userDAO.login(username, password);
            
            if (customer != null) {
                showSuccess("✅ Login successful! Loading dashboard...");
                
                // Add small delay for better UX
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("customer_dashboard.fxml"));
                        Parent root = loader.load();
                        
                        CustomerDashboardController controller = loader.getController();
                        controller.setCustomer(customer);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Customer Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Customer logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid username or password");
            }
            
        } catch (Exception e) {
            showError("Error during customer login");
            e.printStackTrace();
        }
    }
    
    private void loginAsManager(String username, String password) {
        try {
            Manager manager = userDAO.loginManager(username, password);
            
            if (manager != null) {
                showSuccess("✅ Manager login successful!");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerDashboard.fxml"));
                        Parent root = loader.load();
                        
                        ManagerDashboardController controller = loader.getController();
                        controller.setManager(manager);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Manager Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Manager logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load manager dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid manager credentials");
            }
            
        } catch (Exception e) {
            showError("Error during manager login");
            e.printStackTrace();
        }
    }
    
    private void loginAsDispatcher(String username, String password) {
        try {
            Dispatcher dispatcher = userDAO.loginDispatcher(username, password);
            
            if (dispatcher != null) {
                showSuccess("✅ Dispatcher login successful!");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("DispatcherDashboard.fxml"));
                        Parent root = loader.load();
                        
                        DispatcherDashboardController controller = loader.getController();
                        controller.setDispatcher(dispatcher);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Dispatcher Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Dispatcher logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load dispatcher dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid dispatcher credentials");
            }
            
        } catch (Exception e) {
            showError("Error during dispatcher login");
            e.printStackTrace();
        }
    }
    
    private void loginAsPickupRider(String username, String password) {
        try {
            PickupRider rider = userDAO.loginPickupRider(username, password);
            
            if (rider != null) {
                showSuccess("✅ Pickup Rider login successful!");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("PickupRiderDashboard.fxml"));
                        Parent root = loader.load();
                        
                        PickupRiderDashboardController controller = loader.getController();
                        controller.setPickupRider(rider);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Pickup Rider Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Pickup Rider logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load pickup rider dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid pickup rider credentials");
            }
            
        } catch (Exception e) {
            showError("Error during pickup rider login");
            e.printStackTrace();
        }
    }
    
    private void loginAsDeliveryRider(String username, String password) {
        try {
            DeliveryRider rider = userDAO.loginDeliveryRider(username, password);
            
            if (rider != null) {
                showSuccess("✅ Delivery Rider login successful!");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeliveryRiderDashboard.fxml"));
                        Parent root = loader.load();
                        
                        DeliveryRiderDashboardController controller = loader.getController();
                        controller.setDeliveryRider(rider);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Delivery Rider Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Delivery Rider logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load delivery rider dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid delivery rider credentials");
            }
            
        } catch (Exception e) {
            showError("Error during delivery rider login");
            e.printStackTrace();
        }
    }
    
    private void loginAsWarehouseStaff(String username, String password) {
        try {
            WarehouseStaff staff = userDAO.loginWarehouseStaff(username, password);
            
            if (staff != null) {
                showSuccess("✅ Warehouse Staff login successful!");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("WarehouseDashboard.fxml"));
                        Parent root = loader.load();
                        
                        WarehouseDashboardController controller = loader.getController();
                        controller.setWarehouseStaff(staff);
                        
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root, 1400, 900));
                        stage.setTitle("SwiftShip - Warehouse Dashboard");
                        stage.centerOnScreen();
                        
                        System.out.println("✅ Warehouse Staff logged in: " + username);
                    } catch (Exception ex) {
                        showError("Failed to load warehouse dashboard");
                        ex.printStackTrace();
                    }
                });
                pause.play();
                
            } else {
                showError("❌ Invalid warehouse staff credentials");
            }
            
        } catch (Exception e) {
            showError("Error during warehouse staff login");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("forgot_password.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 500));
            stage.setTitle("SwiftShip - Reset Password");
            stage.centerOnScreen();
            
        } catch (Exception e) {
            showError("Unable to open password reset");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void showSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("SwiftShip - Create Account");
            stage.centerOnScreen();
            
        } catch (Exception e) {
            showError("Unable to open signup page");
            e.printStackTrace();
        }
    }
    
    // Status message helpers
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FADBD8; -fx-font-weight: bold;");
        fadeIn();
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #2ECC71; -fx-background-color: #D5F4E6; -fx-font-weight: bold;");
        fadeIn();
    }
    
    private void showInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #3498DB; -fx-background-color: #D6EAF8; -fx-font-weight: bold;");
        fadeIn();
    }
    
    private void clearStatus() {
        statusLabel.setText("");
        statusLabel.setStyle("");
    }
    
    private void fadeIn() {
        FadeTransition ft = new FadeTransition(Duration.millis(300), statusLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}