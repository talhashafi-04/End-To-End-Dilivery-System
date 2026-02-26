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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeliveryRiderDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusBadge;
    @FXML private Label pendingCountLabel;
    @FXML private Label completedTodayLabel;
    @FXML private Label ratingLabel;
    @FXML private VBox assignmentsContainer;
    
    private DeliveryRider currentRider;
    private DeliveryAssignmentDAO deliveryAssignmentDAO = new DeliveryAssignmentDAO();
    private OrderDAO orderDAO = new OrderDAO();
    
    public void setDeliveryRider(DeliveryRider rider) {
        this.currentRider = rider;
        welcomeLabel.setText(rider.getUsername() + " | " + rider.getVehicleType());
        ratingLabel.setText(String.format("%.1f", rider.getRating()));
        
        // Set status badge
        if ("Available".equals(rider.getAvailabilityStatus())) {
            statusBadge.setText("🟢 Available");
        } else {
            statusBadge.setText("🔴 Busy");
        }
        
        loadDeliveryAssignments();
    }
    
    @FXML
    private void loadDeliveryAssignments() {
        assignmentsContainer.getChildren().clear();
        
        List<DeliveryAssignment> assignments = deliveryAssignmentDAO.getPendingDeliveryAssignments(
            currentRider.getRiderId()
        );
        
        pendingCountLabel.setText(String.valueOf(assignments.size()));
        
        if (assignments.isEmpty()) {
            Label emptyLabel = new Label("✅ No pending deliveries at the moment\n\nYou're all caught up!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50; -fx-text-alignment: center;");
            emptyLabel.setAlignment(Pos.CENTER);
            assignmentsContainer.getChildren().add(emptyLabel);
        } else {
            for (DeliveryAssignment assignment : assignments) {
                Order order = orderDAO.getOrderById(assignment.getOrderId());
                if (order != null) {
                    assignmentsContainer.getChildren().add(createDeliveryCard(assignment, order));
                }
            }
        }
    }
    
    private VBox createDeliveryCard(DeliveryAssignment assignment, Order order) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: " + 
            ("In Transit".equals(assignment.getStatus()) ? "#fff3e0" : "#e8f5e9") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #ff5722;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label assignmentIdLabel = new Label("📦 " + assignment.getOrderId());
        assignmentIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label timeLabel = new Label("🕐 " + assignment.getAssignmentDate().toString().substring(0, 16));
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        Label statusBadge = new Label(assignment.getStatus());
        statusBadge.setStyle(
            "-fx-background-color: " + 
            ("In Transit".equals(assignment.getStatus()) ? "#ff9800" : "#28a745") + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(assignmentIdLabel, timeLabel, spacer, statusBadge);
        
        // Delivery Destination (Main Focus)
        VBox deliveryBox = new VBox(8);
        Label deliveryTitle = new Label("📍 DELIVERY DESTINATION");
        deliveryTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ff5722;");
        
        Label receiverName = new Label("Recipient: " + order.getReceiverName());
        receiverName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label receiverPhone = new Label("📞 " + order.getReceiverPhone());
        receiverPhone.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Label deliveryAddress = new Label("📍 " + order.getReceiverAddress());
        deliveryAddress.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        deliveryAddress.setWrapText(true);
        
        deliveryBox.getChildren().addAll(deliveryTitle, receiverName, receiverPhone, deliveryAddress);
        
        // Pickup Location (for reference)
        VBox pickupBox = new VBox(5);
        Label pickupTitle = new Label("📤 PICKED UP FROM");
        pickupTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #666;");
        Label pickupAddress = new Label("← " + order.getSenderAddress());
        pickupAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        pickupAddress.setWrapText(true);
        pickupBox.getChildren().addAll(pickupTitle, pickupAddress);
        
        // Parcel Info
        HBox parcelSection = new HBox(20);
        parcelSection.setAlignment(Pos.CENTER_LEFT);
        
        VBox parcelBox = new VBox(5);
        Label parcelTitle = new Label("📦 PARCEL INFO");
        parcelTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
        
        Parcel parcel = orderDAO.getParcelByOrderId(order.getOrderId());
        if (parcel != null) {
            Label weightLabel = new Label("⚖️ " + parcel.getWeight() + " kg");
            Label dimensionsLabel = new Label("📏 " + 
                (parcel.getDimensions() != null ? parcel.getDimensions() : "Standard size"));
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
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        }
        imageBox.getChildren().add(imageView);
        
        parcelSection.getChildren().addAll(parcelBox, imageBox);
        
        // Action Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        if ("Pending".equals(assignment.getStatus())) {
            Button startBtn = new Button("✓ Start Delivery");
            startBtn.setStyle(
                "-fx-background-color: #ff9800;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            startBtn.setOnAction(e -> {
				try {
					handleStartDelivery(assignment);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
            buttonBox.getChildren().add(startBtn);
        }
        
        if ("In Progress".equals(assignment.getStatus())) {
            Button deliverBtn = new Button("✓ Mark as Delivered");
            deliverBtn.setStyle(
                "-fx-background-color: #28a745;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            deliverBtn.setOnAction(e -> handleMarkDelivered(assignment, order));
            buttonBox.getChildren().add(deliverBtn);
        }
        
        // Add all to card
        card.getChildren().addAll(
            header,
            new Separator(),
            deliveryBox,
            new Separator(),
            pickupBox,
            new Separator(),
            parcelSection,
            new Separator(),
            buttonBox
        );
        
        return card;
    }
    
    private void handleStartDelivery(DeliveryAssignment assignment) throws SQLException {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Start Delivery");
        confirmAlert.setHeaderText("Start delivery for this order?");
        confirmAlert.setContentText("Order: " + assignment.getOrderId() + "\n\n" +
                                   "By starting, you confirm that you have the parcel and are heading to deliver it.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
          // boolean success = deliveryAssignmentDAO.startDelivery(assignment.getAssignmentId());
        	boolean success = deliveryAssignmentDAO.startDelivery(assignment.getAssignmentId());
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Delivery Started!");
                successAlert.setContentText("Status updated to In Transit. Please proceed to delivery location.");
                successAlert.showAndWait();
                
                loadDeliveryAssignments();
            } else {
                showError("Failed to start delivery");
            }
        }
    }
    
    private void handleMarkDelivered(DeliveryAssignment assignment, Order order) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Mark as Delivered");
        confirmAlert.setHeaderText("Confirm Parcel Delivery");
        confirmAlert.setContentText(
            "Order: " + order.getOrderId() + "\n" +
            "Recipient: " + order.getReceiverName() + "\n\n" +
            "Have you delivered the parcel to the recipient?\n\n" +
            "⚠️ Only mark as delivered after the recipient receives the parcel."
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = deliveryAssignmentDAO.completeDelivery(
                assignment.getAssignmentId(),
                order.getCustomerId()
            );
            
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Parcel Delivered!");
                successAlert.setContentText(
                    "Delivery completed successfully!\n\n" +
                    "Order: " + order.getOrderId() + "\n" +
                    "Status: Delivered\n\n" +
                    "Great job! The customer has been notified."
                );
                successAlert.showAndWait();
                
                loadDeliveryAssignments();
            } else {
                showError("Failed to complete delivery");
            }
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
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