package application;

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
import model.Manager;
import model.Order;
import model.Parcel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ManagerDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private VBox ordersContainer;
    
    private Manager currentManager;
    private OrderDAO orderDAO = new OrderDAO();
    
    public void setManager(Manager manager) {
        this.currentManager = manager;
        welcomeLabel.setText("Welcome, " + manager.getUsername());
        loadPendingOrders();
    }
    
    @FXML
    private void loadPendingOrders() {
        ordersContainer.getChildren().clear();
        
        List<Order> pendingOrders = orderDAO.getPendingOrders();
        
        pendingCountLabel.setText(String.valueOf(pendingOrders.size()));
        
        if (pendingOrders.isEmpty()) {
            Label emptyLabel = new Label("✅ No pending requests at the moment");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50;");
            ordersContainer.getChildren().add(emptyLabel);
        } else {
            for (Order order : pendingOrders) {
                ordersContainer.getChildren().add(createOrderCard(order));
            }
        }
    }
    
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #dee2e6;" +
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
            "-fx-background-color: #ffc107;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(orderIdLabel, dateLabel, spacer, statusBadge);
        
        // Sender & Receiver Info
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30);
        infoGrid.setVgap(10);
        
        // Sender Section
        VBox senderBox = new VBox(5);
        Label senderTitle = new Label("📤 SENDER");
        senderTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        Label senderName = new Label(order.getSenderName());
        senderName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label senderPhone = new Label("📞 " + order.getSenderPhone());
        senderPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label senderAddress = new Label("📍 " + order.getSenderAddress());
        senderAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        senderAddress.setWrapText(true);
        senderAddress.setMaxWidth(300);
        senderBox.getChildren().addAll(senderTitle, senderName, senderPhone, senderAddress);
        
        // Receiver Section
        VBox receiverBox = new VBox(5);
        Label receiverTitle = new Label("📥 RECEIVER");
        receiverTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        Label receiverName = new Label(order.getReceiverName());
        receiverName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label receiverPhone = new Label("📞 " + order.getReceiverPhone());
        receiverPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label receiverAddress = new Label("📍 " + order.getReceiverAddress());
        receiverAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        receiverAddress.setWrapText(true);
        receiverAddress.setMaxWidth(300);
        receiverBox.getChildren().addAll(receiverTitle, receiverName, receiverPhone, receiverAddress);
        
        GridPane.setConstraints(senderBox, 0, 0);
        GridPane.setConstraints(receiverBox, 1, 0);
        infoGrid.getChildren().addAll(senderBox, receiverBox);
        
        // Parcel Info & Image
        HBox parcelSection = new HBox(20);
        parcelSection.setAlignment(Pos.CENTER_LEFT);
        
        // Parcel Details
        VBox parcelBox = new VBox(5);
        Label parcelTitle = new Label("📦 PARCEL DETAILS");
        parcelTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
        
        Parcel parcel = orderDAO.getParcelByOrderId(order.getOrderId());
        if (parcel != null) {
            Label weightLabel = new Label("⚖️ Weight: " + parcel.getWeight() + " kg");
            weightLabel.setStyle("-fx-font-size: 12px;");
            Label dimensionsLabel = new Label("📏 Dimensions: " + parcel.getDimensions());
            dimensionsLabel.setStyle("-fx-font-size: 12px;");
            Label descLabel = new Label("📝 " + (parcel.getDescription() != null ? parcel.getDescription() : "No description"));
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(300);
            
            parcelBox.getChildren().addAll(parcelTitle, weightLabel, dimensionsLabel, descLabel);
        } else {
            parcelBox.getChildren().add(parcelTitle);
        }
        
        // Parcel Image
        VBox imageBox = new VBox(5);
        imageBox.setAlignment(Pos.CENTER);
        Label imageTitle = new Label("📷 PARCEL IMAGE");
        imageTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 2;");
        
        String imagePath = orderDAO.getImagePathByOrderId(order.getOrderId());
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            } else {
                imageView.setImage(null);
                Label noImage = new Label("❌ Image not found");
                noImage.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
                imageBox.getChildren().add(noImage);
            }
        }
        
        imageBox.getChildren().addAll(imageTitle, imageView);
        
        parcelSection.getChildren().addAll(parcelBox, imageBox);
        
        // Action Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button approveBtn = new Button("✓ Approve");
        approveBtn.setStyle(
            "-fx-background-color: #28a745;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        approveBtn.setOnAction(e -> handleApprove(order));
        
        Button rejectBtn = new Button("✗ Reject");
        rejectBtn.setStyle(
            "-fx-background-color: #dc3545;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        rejectBtn.setOnAction(e -> handleReject(order));
        
        buttonBox.getChildren().addAll(approveBtn, rejectBtn);
        
        // Add all sections to card
        card.getChildren().addAll(
            header,
            new Separator(),
            infoGrid,
            new Separator(),
            parcelSection,
            new Separator(),
            buttonBox
        );
        
        return card;
    }
    
    private void handleApprove(Order order) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Approval");
        confirmAlert.setHeaderText("Approve Order: " + order.getOrderId());
        confirmAlert.setContentText("Are you sure you want to approve this delivery request?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = orderDAO.approveOrder(order.getOrderId(), currentManager.getManagerId());
            
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Order Approved!");
                successAlert.setContentText("Order " + order.getOrderId() + " has been approved.\n" +
                                          "Customer has been notified.");
                successAlert.showAndWait();
                
                loadPendingOrders(); // Refresh list
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Approval Failed");
                errorAlert.setContentText("Failed to approve the order. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }
    
    private void handleReject(Order order) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Order");
        dialog.setHeaderText("Reject Order: " + order.getOrderId());
        dialog.setContentText("Please provide a reason for rejection:");
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Reason Required");
                alert.setContentText("Please provide a reason for rejection.");
                alert.showAndWait();
                return;
            }
            
            boolean success = orderDAO.rejectOrder(order.getOrderId(), currentManager.getManagerId(), reason);
            
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Order Rejected");
                successAlert.setContentText("Order " + order.getOrderId() + " has been rejected.\n" +
                                          "Customer has been notified with the reason.");
                successAlert.showAndWait();
                
                loadPendingOrders(); // Refresh list
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Rejection Failed");
                errorAlert.setContentText("Failed to reject the order. Please try again.");
                errorAlert.showAndWait();
            }
        });
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