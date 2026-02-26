package application;

import dao.UserDAO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

public class SignupController {

    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private CheckBox termsCheckbox;
    @FXML private Label statusLabel;
    @FXML private VBox addressContainer;
    @FXML private VBox roleSpecificContainer;
    @FXML private VBox additionalFieldContainer;

    private UserDAO userDAO = new UserDAO();
    
    // Dynamic fields based on role
    private TextField vehicleTypeField;
    private TextField licenseNumberField;
    private TextField departmentField;
    private TextField assignedZoneField;
    private TextField warehouseIdField;
    private ComboBox<String> shiftComboBox;

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
        roleComboBox.setPromptText("Select account type");
        
        // Initialize with Customer fields visible
        handleRoleChange();
        
        // Add validation listeners
        passwordField.textProperty().addListener((obs, old, newVal) -> validatePasswordMatch());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> validatePasswordMatch());
    }
    
    @FXML
    private void handleRoleChange() {
        String role = roleComboBox.getValue();
        
        // Clear previous role-specific fields
        roleSpecificContainer.getChildren().clear();
        additionalFieldContainer.getChildren().clear();
        
        // Show/hide address based on role
        addressContainer.setVisible(true);
        addressContainer.setManaged(true);
        
        if (role == null) return;
        
        switch (role) {
            case "Customer":
                setupCustomerFields();
                break;
            case "Manager":
                setupManagerFields();
                break;
            case "Dispatcher":
                setupDispatcherFields();
                break;
            case "Pickup Rider":
                setupPickupRiderFields();
                break;
            case "Delivery Rider":
                setupDeliveryRiderFields();
                break;
            case "Warehouse Staff":
                setupWarehouseFields();
                break;
        }
    }
    
    private void setupCustomerFields() {
        // Customers just need address - already visible
        addressContainer.setVisible(true);
        addressContainer.setManaged(true);
    }
    
    private void setupManagerFields() {
        addressContainer.setVisible(false);
        addressContainer.setManaged(false);
        
        Label titleLabel = new Label("👔 Manager Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        Label deptLabel = new Label("Department *");
        deptLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        departmentField = new TextField();
        departmentField.setPromptText("e.g., Operations, Logistics, Finance");
        departmentField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        VBox deptBox = new VBox(8, deptLabel, departmentField);
        roleSpecificContainer.getChildren().addAll(titleLabel, deptBox);
    }
    
    private void setupDispatcherFields() {
        addressContainer.setVisible(false);
        addressContainer.setManaged(false);
        
        Label titleLabel = new Label("📡 Dispatcher Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        Label zoneLabel = new Label("Assigned Zone *");
        zoneLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        assignedZoneField = new TextField();
        assignedZoneField.setPromptText("e.g., Rawalpindi/Islamabad, Lahore Central");
        assignedZoneField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        VBox zoneBox = new VBox(8, zoneLabel, assignedZoneField);
        roleSpecificContainer.getChildren().addAll(titleLabel, zoneBox);
    }
    
    private void setupPickupRiderFields() {
        Label titleLabel = new Label("🏍️ Pickup Rider Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        // Vehicle Type
        Label vehicleLabel = new Label("Vehicle Type *");
        vehicleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        vehicleTypeField = new TextField();
        vehicleTypeField.setPromptText("e.g., Motorcycle, Bike");
        vehicleTypeField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        VBox vehicleBox = new VBox(8, vehicleLabel, vehicleTypeField);
        
        // License Number in additional field
        Label licenseLabel = new Label("License Number *");
        licenseLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        licenseNumberField = new TextField();
        licenseNumberField.setPromptText("e.g., ISB-2024-12345");
        licenseNumberField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        additionalFieldContainer.getChildren().addAll(licenseLabel, licenseNumberField);
        
        roleSpecificContainer.getChildren().addAll(titleLabel, vehicleBox);
    }
    
    private void setupDeliveryRiderFields() {
        Label titleLabel = new Label("🚚 Delivery Rider Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        // Vehicle Type
        Label vehicleLabel = new Label("Vehicle Type *");
        vehicleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        vehicleTypeField = new TextField();
        vehicleTypeField.setPromptText("e.g., Motorcycle, Van");
        vehicleTypeField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        VBox vehicleBox = new VBox(8, vehicleLabel, vehicleTypeField);
        
        // License Number in additional field
        Label licenseLabel = new Label("License Number *");
        licenseLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        licenseNumberField = new TextField();
        licenseNumberField.setPromptText("e.g., ISB-2024-67890");
        licenseNumberField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        additionalFieldContainer.getChildren().addAll(licenseLabel, licenseNumberField);
        
        roleSpecificContainer.getChildren().addAll(titleLabel, vehicleBox);
    }
    
    private void setupWarehouseFields() {
        addressContainer.setVisible(false);
        addressContainer.setManaged(false);
        
        Label titleLabel = new Label("📦 Warehouse Staff Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        // Warehouse ID
        Label warehouseLabel = new Label("Warehouse ID *");
        warehouseLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        warehouseIdField = new TextField();
        warehouseIdField.setPromptText("e.g., WH001, WH002");
        warehouseIdField.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        VBox warehouseBox = new VBox(8, warehouseLabel, warehouseIdField);
        
        // Shift in additional field
        Label shiftLabel = new Label("Work Shift *");
        shiftLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        shiftComboBox = new ComboBox<>();
        shiftComboBox.getItems().addAll("Morning", "Evening", "Night");
        shiftComboBox.setValue("Morning");
        shiftComboBox.setPrefWidth(315);
        shiftComboBox.setPrefHeight(40);
        shiftComboBox.setStyle("-fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        
        additionalFieldContainer.getChildren().addAll(shiftLabel, shiftComboBox);
        
        roleSpecificContainer.getChildren().addAll(titleLabel, warehouseBox);
    }
    
    @FXML
    private void handleSignup() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String role = roleComboBox.getValue();
            boolean success = false;
            
            switch (role) {
                case "Customer":
                    success = registerCustomer();
                    break;
                case "Manager":
                    success = registerManager();
                    break;
                case "Dispatcher":
                    success = registerDispatcher();
                    break;
                case "Pickup Rider":
                    success = registerPickupRider();
                    break;
                case "Delivery Rider":
                    success = registerDeliveryRider();
                    break;
                case "Warehouse Staff":
                    success = registerWarehouseStaff();
                    break;
            }
            
            if (success) {
                showSuccess("✅ Account created successfully! Redirecting to login...");
                
                // Wait 2 seconds then redirect
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> backToLogin());
                pause.play();
            } else {
                showError("❌ Registration failed. Username or email may already exist.");
            }
            
        } catch (Exception e) {
            showError("❌ Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean registerCustomer() {
        Customer customer = new Customer();
        customer.setUsername(usernameField.getText().trim());
        customer.setPassword(passwordField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressField.getText().trim());
        
        return userDAO.registerCustomer(customer);
    }
    
    private boolean registerManager() {
        // For now, use a generic registration method
        // You may need to add registerManager() to UserDAO
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = departmentField.getText().trim();
        
        // Call DAO method (you'll need to create this)
        return userDAO.registerManager(username, password, email, phone, department);
    }
    
    private boolean registerDispatcher() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String zone = assignedZoneField.getText().trim();
        
        return userDAO.registerDispatcher(username, password, email, phone, zone);
    }
    
    private boolean registerPickupRider() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String vehicleType = vehicleTypeField.getText().trim();
        String licenseNumber = licenseNumberField.getText().trim();
        
        return userDAO.registerPickupRider(username, password, email, phone, address, vehicleType, licenseNumber);
    }
    
    private boolean registerDeliveryRider() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String vehicleType = vehicleTypeField.getText().trim();
        String licenseNumber = licenseNumberField.getText().trim();
        
        return userDAO.registerDeliveryRider(username, password, email, phone, address, vehicleType, licenseNumber);
    }
    
    private boolean registerWarehouseStaff() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String warehouseId = warehouseIdField.getText().trim();
        String shift = shiftComboBox.getValue();
        
        return userDAO.registerWarehouseStaff(username, password, email, phone, warehouseId, shift);
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        // Basic validation
        if (usernameField.getText().trim().isEmpty()) {
            errors.append("• Username is required\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors.append("• Email is required\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("• Invalid email format\n");
        }
        
        if (passwordField.getText().trim().isEmpty()) {
            errors.append("• Password is required\n");
        } else if (passwordField.getText().trim().length() < 6) {
            errors.append("• Password must be at least 6 characters\n");
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errors.append("• Passwords do not match\n");
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("• Phone number is required\n");
        }
        
        // Role-specific validation
        String role = roleComboBox.getValue();
        if (role.equals("Customer") && addressField.getText().trim().isEmpty()) {
            errors.append("• Address is required for customers\n");
        }
        
        if (role.equals("Manager") && departmentField.getText().trim().isEmpty()) {
            errors.append("• Department is required for managers\n");
        }
        
        if (role.equals("Dispatcher") && assignedZoneField.getText().trim().isEmpty()) {
            errors.append("• Assigned zone is required for dispatchers\n");
        }
        
        if ((role.equals("Pickup Rider") || role.equals("Delivery Rider"))) {
            if (vehicleTypeField.getText().trim().isEmpty()) {
                errors.append("• Vehicle type is required for riders\n");
            }
            if (licenseNumberField.getText().trim().isEmpty()) {
                errors.append("• License number is required for riders\n");
            }
            if (addressField.getText().trim().isEmpty()) {
                errors.append("• Address is required for riders\n");
            }
        }
        
        if (role.equals("Warehouse Staff") && warehouseIdField.getText().trim().isEmpty()) {
            errors.append("• Warehouse ID is required\n");
        }
        
        if (!termsCheckbox.isSelected()) {
            errors.append("• You must agree to the terms and conditions\n");
        }
        
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fix the following issues:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            
            showError("❌ Please fill all required fields correctly");
            return false;
        }
        
        return true;
    }
    
    private void validatePasswordMatch() {
        if (confirmPasswordField.getText().isEmpty()) return;
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2; -fx-border-radius: 8;");
        } else {
            confirmPasswordField.setStyle("-fx-border-color: #2ECC71; -fx-border-width: 2; -fx-border-radius: 8;");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    @FXML
    private void handleClear() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        phoneField.clear();
        addressField.clear();
        termsCheckbox.setSelected(false);
        roleComboBox.setValue("Customer");
        clearStatus();
        handleRoleChange();
    }
    
    @FXML
    public void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 850));
            stage.setTitle("SwiftShip - Login");
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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