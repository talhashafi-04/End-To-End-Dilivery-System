package application;

import dao.AssignmentDAO;
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

public class PickupRiderDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusBadge;
    @FXML private Label pendingCountLabel;
    @FXML private Label completedTodayLabel;
    @FXML private Label ratingLabel;
    @FXML private VBox assignmentsContainer;
    
    private PickupRider currentRider;
    private AssignmentDAO assignmentDAO = new AssignmentDAO();
    private OrderDAO orderDAO = new OrderDAO();
    
    public void setPickupRider(PickupRider rider) {
        this.currentRider = rider;
        welcomeLabel.setText(rider.getUsername() + " | " + rider.getVehicleType());
        ratingLabel.setText(String.format("%.1f", rider.getRating()));
        
        // Set status badge
        if ("Available".equals(rider.getAvailabilityStatus())) {
            statusBadge.setText("🟢 Available");
        } else {
            statusBadge.setText("🔴 Busy");
        }
        
        loadAssignments();
    }
    
    @FXML
    private void loadAssignments() {
        assignmentsContainer.getChildren().clear();
        
        List<PickupAssignment> assignments = assignmentDAO.getPendingAssignmentsByRider(
            currentRider.getRiderId()
        );
        
        pendingCountLabel.setText(String.valueOf(assignments.size()));
        
        if (assignments.isEmpty()) {
            Label emptyLabel = new Label("✅ No pending pickups at the moment\n\nYou're all caught up!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50; -fx-text-alignment: center;");
            emptyLabel.setAlignment(Pos.CENTER);
            assignmentsContainer.getChildren().add(emptyLabel);
        } else {
            for (PickupAssignment assignment : assignments) {
                Order order = orderDAO.getOrderById(assignment.getOrderId());
                if (order != null) {
                    assignmentsContainer.getChildren().add(createAssignmentCard(assignment, order));
                }
            }
        }
    }
    
    private VBox createAssignmentCard(PickupAssignment assignment, Order order) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: " + 
            ("Confirmed".equals(assignment.getStatus()) ? "#e3f2fd" : "#fff9e6") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #2196F3;" +
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
            ("Confirmed".equals(assignment.getStatus()) ? "#28a745" : "#ffc107") + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(assignmentIdLabel, timeLabel, spacer, statusBadge);
        
        // Pickup Location
        VBox pickupBox = new VBox(8);
        Label pickupTitle = new Label("📍 PICKUP LOCATION");
        pickupTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        Label customerName = new Label("Customer: " + order.getSenderName());
        customerName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label customerPhone = new Label("📞 " + order.getSenderPhone());
        customerPhone.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Label pickupAddress = new Label("📍 " + order.getSenderAddress());
        pickupAddress.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        pickupAddress.setWrapText(true);
        
        pickupBox.getChildren().addAll(pickupTitle, customerName, customerPhone, pickupAddress);
        
        // Delivery destination (for reference)
        VBox destBox = new VBox(5);
        Label destTitle = new Label("📦 DELIVERY DESTINATION");
        destTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #666;");
        Label destAddress = new Label("→ " + order.getReceiverAddress());
        destAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        destAddress.setWrapText(true);
        destBox.getChildren().addAll(destTitle, destAddress);
        
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
            Button confirmBtn = new Button("✓ Accept Pickup");
            confirmBtn.setStyle(
                "-fx-background-color: #28a745;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            confirmBtn.setOnAction(e -> handleConfirm(assignment));
            buttonBox.getChildren().add(confirmBtn);
        }
        
        if ("Confirmed".equals(assignment.getStatus())) {
            Button collectBtn = new Button("✓ Mark as Collected");
            collectBtn.setStyle(
                "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            collectBtn.setOnAction(e -> handleCollect(assignment, order));
            buttonBox.getChildren().add(collectBtn);
        }
        
        // Add all to card
        card.getChildren().addAll(
            header,
            new Separator(),
            pickupBox,
            new Separator(),
            destBox,
            new Separator(),
            parcelSection,
            new Separator(),
            buttonBox
        );
        
        return card;
    }
    
    private void handleConfirm(PickupAssignment assignment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Accept Pickup");
        confirmAlert.setHeaderText("Accept this pickup assignment?");
        confirmAlert.setContentText("Order: " + assignment.getOrderId() + "\n\n" +
                                   "By accepting, you confirm that you will pick up this parcel.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = assignmentDAO.confirmAssignment(assignment.getAssignmentId());
            
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Pickup Accepted!");
                successAlert.setContentText("Assignment confirmed. Please proceed to pickup location.");
                successAlert.showAndWait();
                
                loadAssignments();
            } else {
                showError("Failed to confirm assignment");
            }
        }
    }
    
    private void handleCollect(PickupAssignment assignment, Order order) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Mark as Collected");
        confirmAlert.setHeaderText("Confirm Parcel Collection");
        confirmAlert.setContentText(
            "Order: " + order.getOrderId() + "\n" +
            "Customer: " + order.getSenderName() + "\n\n" +
            "Have you collected the parcel from the customer?\n\n" +
            "⚠️ Only mark as collected after physically receiving the parcel."
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = assignmentDAO.completePickup(
                assignment.getAssignmentId(),
                order.getCustomerId()
            );
            
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Parcel Collected!");
                successAlert.setContentText(
                    "Pickup completed successfully!\n\n" +
                    "Order: " + order.getOrderId() + "\n" +
                    "Status: Collected\n\n" +
                    "Please deliver the parcel to the warehouse."
                );
                successAlert.showAndWait();
                
                loadAssignments();
            } else {
                showError("Failed to complete pickup");
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