package application;

import dao.DeliveryAssignmentDAO;
import dao.OrderDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class WarehouseDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label inWarehouseLabel;
    @FXML private Label availableRidersLabel;
    @FXML private Label dispatchedTodayLabel;
    @FXML private VBox ordersContainer;
    
    private WarehouseStaff currentStaff;
    private OrderDAO orderDAO = new OrderDAO();
    private DeliveryAssignmentDAO deliveryAssignmentDAO = new DeliveryAssignmentDAO();
    
    public void setWarehouseStaff(WarehouseStaff staff) {
        this.currentStaff = staff;
        welcomeLabel.setText("Welcome, " + staff.getUsername() + " | Warehouse: " + staff.getWarehouseId());
        loadCollectedOrders();
        updateStats();
    }
    
    @FXML
    private void loadCollectedOrders() {
        ordersContainer.getChildren().clear();
        
        List<Order> collectedOrders = orderDAO.getCollectedOrders();
        
        inWarehouseLabel.setText(String.valueOf(collectedOrders.size()));
        
        if (collectedOrders.isEmpty()) {
            Label emptyLabel = new Label("✅ No parcels in warehouse\n\nAll parcels have been dispatched!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50; -fx-text-alignment: center;");
            emptyLabel.setAlignment(Pos.CENTER);
            ordersContainer.getChildren().add(emptyLabel);
        } else {
            for (Order order : collectedOrders) {
                // Check if already assigned for delivery
                if (!deliveryAssignmentDAO.hasDeliveryAssignment(order.getOrderId())) {
                    ordersContainer.getChildren().add(createOrderCard(order));
                }
            }
        }
        
        updateStats();
    }
    
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: #f3e5f5;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #9c27b0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label orderIdLabel = new Label("📦 " + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label statusBadge = new Label(order.getStatus());
        statusBadge.setStyle(
            "-fx-background-color: #9c27b0;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(orderIdLabel, spacer, statusBadge);
        
        // Delivery Details
        VBox deliveryBox = new VBox(8);
        Label deliveryTitle = new Label("🚚 DELIVERY DESTINATION");
        deliveryTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #9c27b0;");
        
        Label receiverName = new Label("Receiver: " + order.getReceiverName());
        receiverName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        
        Label receiverPhone = new Label("📞 " + order.getReceiverPhone());
        receiverPhone.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Label deliveryAddress = new Label("📍 " + order.getReceiverAddress());
        deliveryAddress.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        deliveryAddress.setWrapText(true);
        
        deliveryBox.getChildren().addAll(deliveryTitle, receiverName, receiverPhone, deliveryAddress);
        
        // Parcel Info with Image
        HBox parcelSection = new HBox(20);
        parcelSection.setAlignment(Pos.CENTER_LEFT);
        
        VBox parcelBox = new VBox(5);
        Label parcelTitle = new Label("📦 PARCEL INFO");
        parcelTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
        
        Parcel parcel = orderDAO.getParcelByOrderId(order.getOrderId());
        if (parcel != null) {
            Label weightLabel = new Label("⚖️ " + parcel.getWeight() + " kg");
            Label dimensionsLabel = new Label("📏 " + 
                (parcel.getDimensions() != null ? parcel.getDimensions() : "Standard"));
            parcelBox.getChildren().addAll(parcelTitle, weightLabel, dimensionsLabel);
        }
        
        // Image
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        
        String imagePath = orderDAO.getImagePathByOrderId(order.getOrderId());
        if (imagePath != null && new File(imagePath).exists()) {
            imageView.setImage(new Image(new File(imagePath).toURI().toString()));
        }
        imageBox.getChildren().add(imageView);
        
        parcelSection.getChildren().addAll(parcelBox, imageBox);
        
        // Assign Button
        Button assignBtn = new Button("🚚 Assign Delivery Rider");
        assignBtn.setStyle(
            "-fx-background-color: #9c27b0;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setOnAction(e -> handleAssignDeliveryRider(order));
        
        // Add all to card
        card.getChildren().addAll(
            header,
            new Separator(),
            deliveryBox,
            new Separator(),
            parcelSection,
            new Separator(),
            assignBtn
        );
        
        return card;
    }
    
    private void handleAssignDeliveryRider(Order order) {
        // Get available delivery riders
        List<DeliveryRider> availableRiders = deliveryAssignmentDAO.getAvailableDeliveryRiders();
        
        if (availableRiders.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Riders Available");
            alert.setHeaderText("Cannot Assign Delivery Rider");
            alert.setContentText("No delivery riders are currently available. Please try again later.");
            alert.showAndWait();
            return;
        }
        
        // Create choice dialog
        ChoiceDialog<DeliveryRider> dialog = new ChoiceDialog<>(availableRiders.get(0), availableRiders);
        dialog.setTitle("Assign Delivery Rider");
        dialog.setHeaderText("Select Delivery Rider for Order: " + order.getOrderId());
        dialog.setContentText("Choose delivery rider:");
        
        Optional<DeliveryRider> result = dialog.showAndWait();
        
        result.ifPresent(rider -> {
            // Confirm assignment
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delivery Assignment");
            confirmAlert.setHeaderText("Assign Delivery Rider");
            confirmAlert.setContentText(
                "Order: " + order.getOrderId() + "\n" +
                "Delivery To: " + order.getReceiverAddress() + "\n\n" +
                "Rider: " + rider.getUsername() + "\n" +
                "Vehicle: " + rider.getVehicleType() + "\n" +
                "Rating: ⭐ " + rider.getRating() + "\n\n" +
                "Confirm this assignment?"
            );
            
            Optional<ButtonType> confirmation = confirmAlert.showAndWait();
            
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                // Create delivery assignment
                String assignmentId = "DELY_" + System.currentTimeMillis();
                
                DeliveryAssignment assignment = new DeliveryAssignment(
                    assignmentId,
                    order.getOrderId(),
                    rider.getRiderId(),
                    currentStaff.getStaffId(),
                    "Pending"
                );
                
                boolean success = deliveryAssignmentDAO.createDeliveryAssignment(
                    assignment,
                    order.getCustomerId()
                );
                
                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Delivery Rider Assigned!");
                    successAlert.setContentText(
                        "Delivery rider " + rider.getUsername() + " has been assigned.\n\n" +
                        "Order: " + order.getOrderId() + "\n" +
                        "Status: Out for Delivery\n\n" +
                        "The rider and customer have been notified."
                    );
                    successAlert.showAndWait();
                    
                    loadCollectedOrders();
                    
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Assignment Failed");
                    errorAlert.setContentText("Failed to assign delivery rider. Please try again.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    private void updateStats() {
        List<DeliveryRider> riders = deliveryAssignmentDAO.getAvailableDeliveryRiders();
        availableRidersLabel.setText(String.valueOf(riders.size()));
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Login - Delivery System");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}