package application;

import dao.AssignmentDAO;
import dao.DispatcherDAO;
import dao.OrderDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DispatcherDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label availableRidersLabel;
    @FXML private Label assignedTodayLabel;
    @FXML private VBox ordersContainer;
    
    private Dispatcher currentDispatcher;
    private OrderDAO orderDAO = new OrderDAO();
    private DispatcherDAO dispatcherDAO = new DispatcherDAO();
    private AssignmentDAO assignmentDAO = new AssignmentDAO();
    
    public void setDispatcher(Dispatcher dispatcher) {
        this.currentDispatcher = dispatcher;
        welcomeLabel.setText("Welcome, " + dispatcher.getUsername() + " (" + dispatcher.getAssignedZone() + ")");
        loadApprovedOrders();
        updateStats();
    }
    
    @FXML
    private void loadApprovedOrders() {
        ordersContainer.getChildren().clear();
        
        List<Order> approvedOrders = orderDAO.getApprovedOrders();
        
        approvedCountLabel.setText(String.valueOf(approvedOrders.size()));
        
        if (approvedOrders.isEmpty()) {
            Label emptyLabel = new Label("✅ No approved orders waiting for assignment");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50;");
            ordersContainer.getChildren().add(emptyLabel);
        } else {
            for (Order order : approvedOrders) {
                // Check if already assigned
                if (!assignmentDAO.hasPickupAssignment(order.getOrderId())) {
                    ordersContainer.getChildren().add(createOrderCard(order));
                }
            }
        }
        
        updateStats();
    }
    
    
    //Change UI by Changing this card
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: #fff9e6;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #ff9800;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label orderIdLabel = new Label("📦 " + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label dateLabel = new Label("📅 " + order.getOrderDate().toString().substring(0, 19));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        Label statusBadge = new Label(order.getStatus());
        statusBadge.setStyle(
            "-fx-background-color: #28a745;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(orderIdLabel, dateLabel, spacer, statusBadge);
        
        // Pickup Info
        VBox pickupBox = new VBox(5);
        Label pickupTitle = new Label("📍 PICKUP LOCATION");
        pickupTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #ff9800;");
        Label customerName = new Label("Customer: " + order.getSenderName());
        customerName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label customerPhone = new Label("📞 " + order.getSenderPhone());
        customerPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label pickupAddress = new Label("📍 " + order.getSenderAddress());
        pickupAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        pickupAddress.setWrapText(true);
        pickupAddress.setMaxWidth(500);
        pickupBox.getChildren().addAll(pickupTitle, customerName, customerPhone, pickupAddress);
        
        // Delivery Info
        VBox deliveryBox = new VBox(5);
        Label deliveryTitle = new Label("📍 DELIVERY LOCATION");
        deliveryTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label receiverName = new Label("Receiver: " + order.getReceiverName());
        receiverName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label receiverPhone = new Label("📞 " + order.getReceiverPhone());
        receiverPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label deliveryAddress = new Label("📍 " + order.getReceiverAddress());
        deliveryAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        deliveryAddress.setWrapText(true);
        deliveryAddress.setMaxWidth(500);
        deliveryBox.getChildren().addAll(deliveryTitle, receiverName, receiverPhone, deliveryAddress);
        
        // Parcel Info with Image
        HBox parcelSection = new HBox(20);
        parcelSection.setAlignment(Pos.CENTER_LEFT);
        
        VBox parcelBox = new VBox(5);
        Label parcelTitle = new Label("📦 PARCEL DETAILS");
        parcelTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
        
        Parcel parcel = orderDAO.getParcelByOrderId(order.getOrderId());
        if (parcel != null) {
            Label weightLabel = new Label("⚖️ Weight: " + parcel.getWeight() + " kg");
            weightLabel.setStyle("-fx-font-size: 12px;");
            Label dimensionsLabel = new Label("📏 Dimensions: " + 
                (parcel.getDimensions() != null ? parcel.getDimensions() : "Not specified"));
            dimensionsLabel.setStyle("-fx-font-size: 12px;");
            
            parcelBox.getChildren().addAll(parcelTitle, weightLabel, dimensionsLabel);
        }
        
        // Image preview
        VBox imageBox = new VBox(5);
        imageBox.setAlignment(Pos.CENTER);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 2;");
        
        String imagePath = orderDAO.getImagePathByOrderId(order.getOrderId());
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            }
        }
        
        imageBox.getChildren().add(imageView);
        parcelSection.getChildren().addAll(parcelBox, imageBox);
        
        // Assign Rider Button
        Button assignBtn = new Button("🏍️ Assign Pickup Rider");
        assignBtn.setStyle(
            "-fx-background-color: #ff9800;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setOnAction(e -> handleAssignRider(order));
        
        VBox.setMargin(assignBtn, new Insets(10, 0, 0, 0));
        
        // Add all to card
        card.getChildren().addAll(
            header,
            new Separator(),
            pickupBox,
            new Separator(),
            deliveryBox,
            new Separator(),
            parcelSection,
            new Separator(),
            assignBtn
        );
        
        return card;
    }
    
    private void handleAssignRider(Order order) {
        // Get available riders
        List<PickupRider> availableRiders = dispatcherDAO.getAvailableRiders();
        
        if (availableRiders.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Riders Available");
            alert.setHeaderText("Cannot Assign Rider");
            alert.setContentText("No pickup riders are currently available. Please try again later.");
            alert.showAndWait();
            return;
        }
        
        // Create choice dialog with rider selection
        ChoiceDialog<PickupRider> dialog = new ChoiceDialog<>(availableRiders.get(0), availableRiders);
        dialog.setTitle("Assign Pickup Rider");
        dialog.setHeaderText("Select Rider for Order: " + order.getOrderId());
        dialog.setContentText("Choose pickup rider:");
        
        // Customize how riders are displayed
        dialog.getDialogPane().setContentText(
            "Available Riders:\n\n" +
            "Pick the best rider based on rating and vehicle type."
        );
        
        Optional<PickupRider> result = dialog.showAndWait();
        
        result.ifPresent(rider -> {
            // Confirm assignment
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Assignment");
            confirmAlert.setHeaderText("Assign Rider to Order");
            confirmAlert.setContentText(
                "Order: " + order.getOrderId() + "\n" +
                "Pickup Location: " + order.getSenderAddress() + "\n\n" +
                "Rider: " + rider.getUsername() + "\n" +
                "Vehicle: " + rider.getVehicleType() + "\n" +
                "Rating: ⭐ " + rider.getRating() + "\n" +
                "Phone: " + rider.getPhone() + "\n\n" +
                "Confirm this assignment?"
            );
            
            Optional<ButtonType> confirmation = confirmAlert.showAndWait();
            
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                // Create assignment
                String assignmentId = "ASG_" + System.currentTimeMillis();
                
                PickupAssignment assignment = new PickupAssignment(
                    assignmentId,
                    order.getOrderId(),
                    currentDispatcher.getDispatcherId(),
                    rider.getRiderId(),
                    "Pending"  // use DB-valid value from pickup_assignments.status enum
                );
                
                boolean success = assignmentDAO.createPickupAssignment(
                    assignment, 
                    order.getCustomerId()
                );
                
                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Rider Assigned!");
                    successAlert.setContentText(
                        "Pickup rider " + rider.getUsername() + " has been assigned to order " + 
                        order.getOrderId() + ".\n\n" +
                        "The rider and customer have been notified."
                    );
                    successAlert.showAndWait();
                    
                    // Reload orders
                    loadApprovedOrders();
                    
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Assignment Failed");
                    errorAlert.setHeaderText("Could Not Assign Rider");
                    errorAlert.setContentText(
                        "Failed to assign rider to order. Please try again or contact support."
                    );
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    private void updateStats() {
        // Update approved orders count
        List<Order> approvedOrders = orderDAO.getApprovedOrders();
        int unassignedCount = 0;
        for (Order order : approvedOrders) {
            if (!assignmentDAO.hasPickupAssignment(order.getOrderId())) {
                unassignedCount++;
            }
        }
        approvedCountLabel.setText(String.valueOf(unassignedCount));
        
        // Update available riders count
        List<PickupRider> availableRiders = dispatcherDAO.getAvailableRiders();
        availableRidersLabel.setText(String.valueOf(availableRiders.size()));
        
        // Update assigned today count
//        int assignedToday = assignmentDAO.getAssignmentCountToday(currentDispatcher.getDispatcherId());
//        assignedTodayLabel.setText(String.valueOf(assignedToday));
    }
    
    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout");
        confirmAlert.setHeaderText("Confirm Logout");
        confirmAlert.setContentText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Load login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Courier Service - Login");
                stage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Logout Failed");
                errorAlert.setContentText("Could not return to login screen. Error: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}