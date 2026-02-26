package application;

import dao.NotificationDAO;
import dao.OrderDAO;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Customer;
import model.Notification;
import model.Order;
import model.Parcel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerDashboardController {
    
    // Navigation buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnSubmitRequest;
    @FXML private Button btnMyOrders;
    @FXML private Button btnNotifications;
    @FXML private Button btnProfile;
    
    // Header labels
    @FXML private Label lblUsername;
    @FXML private Label lblCustomerId;
    @FXML private Label lblWelcome;
    @FXML private Label lblCurrentDateTime;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblPendingOrders;
    @FXML private Label lblInTransitOrders;
    @FXML private Label notificationBadge;
    
    // Dashboard stats
    @FXML private Label lblDashTotalOrders;
    @FXML private Label lblActiveOrders;
    @FXML private Label lblDeliveredOrders;
    @FXML private Label lblPendingDash;
    
    // Content views
    @FXML private StackPane contentStackPane;
    @FXML private ScrollPane dashboardView;
    @FXML private ScrollPane submitRequestView;
    @FXML private ScrollPane myOrdersView;
    @FXML private ScrollPane notificationsView;
    @FXML private ScrollPane profileView;
    
    // Dashboard containers
    @FXML private VBox recentOrdersContainer;
    
    private Customer currentCustomer;
    private OrderDAO orderDAO = new OrderDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    
    // Submit Request Form Fields (will be created dynamically)
    private TextField senderNameField;
    private TextField senderPhoneField;
    private TextArea senderAddressField;
    private TextField receiverNameField;
    private TextField receiverPhoneField;
    private TextArea receiverAddressField;
    private TextField weightField;
    private TextField dimensionsField;
    private TextArea descriptionField;
    private Label imageLabel;
    private ImageView imagePreview;
    private Label submitStatusLabel;
    private String selectedImagePath = null;
    
    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        
        // Update header
        lblUsername.setText(customer.getUsername());
        lblCustomerId.setText("ID: " + customer.getCustomerId());
        lblWelcome.setText("Welcome Back, " + customer.getUsername() + "!");
        
        // Start clock
        startClock();
        
        // Load initial data
        loadDashboardData();
        updateNotificationBadge();
        
        // Build all views
        buildSubmitRequestView();
        buildMyOrdersView();
        buildNotificationsView();
        buildProfileView();
        
        System.out.println("✅ Customer dashboard loaded for: " + customer.getUsername());
    }
    
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy - hh:mm:ss a");
            lblCurrentDateTime.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }
    
    private void loadDashboardData() {
        if (currentCustomer == null) return;
        
        List<Order> orders = orderDAO.getOrdersByCustomer(currentCustomer.getCustomerId());
        
        int total = orders.size();
        int pending = (int) orders.stream().filter(o -> "Pending".equals(o.getStatus())).count();
        int inTransit = (int) orders.stream().filter(o -> o.getStatus() != null && 
            (o.getStatus().contains("Transit") || o.getStatus().equals("Assigned") || 
             o.getStatus().equals("Picked Up"))).count();
        int delivered = (int) orders.stream().filter(o -> "Delivered".equals(o.getStatus())).count();
        
        // Update header stats
        lblTotalOrders.setText(String.valueOf(total));
        lblPendingOrders.setText(String.valueOf(pending));
        lblInTransitOrders.setText(String.valueOf(inTransit));
        
        // Update dashboard stats
        lblDashTotalOrders.setText(String.valueOf(total));
        lblActiveOrders.setText(String.valueOf(inTransit));
        lblDeliveredOrders.setText(String.valueOf(delivered));
        lblPendingDash.setText(String.valueOf(pending));
        
        // Load recent orders (top 5)
        loadRecentOrders(orders);
    }
    
    private void loadRecentOrders(List<Order> allOrders) {
        recentOrdersContainer.getChildren().clear();
        
        if (allOrders.isEmpty()) {
            Label empty = new Label("📭 No orders yet. Create your first delivery request!");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-padding: 30;");
            recentOrdersContainer.getChildren().add(empty);
            return;
        }
        
        // Show only 5 most recent
        int count = Math.min(5, allOrders.size());
        for (int i = 0; i < count; i++) {
            recentOrdersContainer.getChildren().add(createOrderRow(allOrders.get(i)));
        }
    }
    
    private HBox createOrderRow(Order order) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        
        // Order icon
        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 28px; -fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");
        
        // Order details
        VBox details = new VBox(5);
        HBox.setHgrow(details, Priority.ALWAYS);
        
        Label orderId = new Label("Order #" + order.getOrderId().substring(order.getOrderId().length() - 8));
        orderId.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Label receiver = new Label("To: " + order.getReceiverName() + " • " + order.getReceiverAddress());
        receiver.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        receiver.setMaxWidth(450);
        receiver.setWrapText(true);
        
        details.getChildren().addAll(orderId, receiver);
        
        // Status badge
        Label status = createStatusBadge(order.getStatus());
        
        // Date
        VBox dateBox = new VBox(3);
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        Label dateLabel = new Label(formatDate(order.getOrderDate()));
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
        dateBox.getChildren().add(dateLabel);
        
        row.getChildren().addAll(icon, details, status, dateBox);
        
        return row;
    }
    
    private Label createStatusBadge(String status) {
        Label badge = new Label(status == null ? "Unknown" : status);
        String style = "-fx-padding: 6 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold; ";
        
        String s = status == null ? "" : status.toLowerCase();
        if (s.equals("pending")) {
            badge.setStyle(style + "-fx-background-color: #FEF5E7; -fx-text-fill: #F39C12;");
        } else if (s.equals("delivered")) {
            badge.setStyle(style + "-fx-background-color: #D5F4E6; -fx-text-fill: #2ECC71;");
        } else if (s.contains("transit") || s.equals("assigned") || s.equals("picked up")) {
            badge.setStyle(style + "-fx-background-color: #EBF5FB; -fx-text-fill: #3498DB;");
        } else if (s.equals("rejected")) {
            badge.setStyle(style + "-fx-background-color: #FADBD8; -fx-text-fill: #E74C3C;");
        } else {
            badge.setStyle(style + "-fx-background-color: #ECF0F1; -fx-text-fill: #7f8c8d;");
        }
        
        return badge;
    }
    
    // Navigation Methods
    @FXML
    private void showDashboard() {
        switchView(dashboardView);
        highlightButton(btnDashboard);
        loadDashboardData();
    }
    
    @FXML
    private void showSubmitRequest() {
        switchView(submitRequestView);
        highlightButton(btnSubmitRequest);
    }
    
    @FXML
    private void showMyOrders() {
        switchView(myOrdersView);
        highlightButton(btnMyOrders);
        refreshMyOrdersView();
    }
    
    @FXML
    private void showNotifications() {
        switchView(notificationsView);
        highlightButton(btnNotifications);
        refreshNotificationsView();
        updateNotificationBadge();
    }
    
    @FXML
    private void showProfile() {
        switchView(profileView);
        highlightButton(btnProfile);
        refreshProfileView();
    }
    
    private void switchView(ScrollPane viewToShow) {
        dashboardView.setVisible(false);
        submitRequestView.setVisible(false);
        myOrdersView.setVisible(false);
        notificationsView.setVisible(false);
        profileView.setVisible(false);
        
        viewToShow.setVisible(true);
    }
    
    private void highlightButton(Button activeButton) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.8); -fx-alignment: CENTER_LEFT; -fx-padding: 15 20; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-pref-width: 220;";
        String activeStyle = "-fx-background-color: #2980B9; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 15 20; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8; -fx-pref-width: 220;";
        
        btnDashboard.setStyle(inactiveStyle);
        btnSubmitRequest.setStyle(inactiveStyle);
        btnMyOrders.setStyle(inactiveStyle);
        btnNotifications.setStyle(inactiveStyle);
        btnProfile.setStyle(inactiveStyle);
        
        activeButton.setStyle(activeStyle);
    }
    
    // Build Submit Request View
    private void buildSubmitRequestView() {
        VBox content = new VBox(25);
        content.setStyle("-fx-padding: 0;");
        
        // Form Card
        VBox formCard = new VBox(25);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 40;");
        formCard.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📝");
        icon.setStyle("-fx-font-size: 32px;");
        VBox headerText = new VBox(3);
        Label title = new Label("Submit Delivery Request");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-font-family: 'Century Gothic';");
        Label subtitle = new Label("Fill in the details to create a new delivery order");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        headerText.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(icon, headerText);
        
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #ecf0f1;");
        
        // Sender Section
        Label senderLabel = new Label("📤 Sender Information");
        senderLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        GridPane senderGrid = new GridPane();
        senderGrid.setHgap(20);
        senderGrid.setVgap(15);
        senderGrid.getColumnConstraints().addAll(
            createColumnConstraint(50),
            createColumnConstraint(50)
        );
        
        senderNameField = createTextField("Sender name");
        senderPhoneField = createTextField("03XX-XXXXXXX");
        senderAddressField = createTextArea("Complete pickup address");
        
        senderGrid.add(createFieldBox("Name *", senderNameField), 0, 0);
        senderGrid.add(createFieldBox("Phone *", senderPhoneField), 1, 0);
        VBox addressBox = createFieldBox("Address *", senderAddressField);
        GridPane.setColumnSpan(addressBox, 2);
        senderGrid.add(addressBox, 0, 1);
        
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #ecf0f1;");
        
        // Receiver Section
        Label receiverLabel = new Label("📥 Receiver Information");
        receiverLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        GridPane receiverGrid = new GridPane();
        receiverGrid.setHgap(20);
        receiverGrid.setVgap(15);
        receiverGrid.getColumnConstraints().addAll(
            createColumnConstraint(50),
            createColumnConstraint(50)
        );
        
        receiverNameField = createTextField("Receiver name");
        receiverPhoneField = createTextField("03XX-XXXXXXX");
        receiverAddressField = createTextArea("Complete delivery address");
        
        receiverGrid.add(createFieldBox("Name *", receiverNameField), 0, 0);
        receiverGrid.add(createFieldBox("Phone *", receiverPhoneField), 1, 0);
        VBox recAddrBox = createFieldBox("Address *", receiverAddressField);
        GridPane.setColumnSpan(recAddrBox, 2);
        receiverGrid.add(recAddrBox, 0, 1);
        
        Separator sep3 = new Separator();
        sep3.setStyle("-fx-background-color: #ecf0f1;");
        
        // Parcel Section
        Label parcelLabel = new Label("📦 Parcel Details");
        parcelLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
        
        GridPane parcelGrid = new GridPane();
        parcelGrid.setHgap(20);
        parcelGrid.setVgap(15);
        parcelGrid.getColumnConstraints().addAll(
            createColumnConstraint(33),
            createColumnConstraint(33),
            createColumnConstraint(34)
        );
        
        weightField = createTextField("e.g., 2.5");
        dimensionsField = createTextField("e.g., 30x20x10");
        descriptionField = createTextArea("Special instructions (optional)");
        
        parcelGrid.add(createFieldBox("Weight (kg) *", weightField), 0, 0);
        parcelGrid.add(createFieldBox("Dimensions (cm)", dimensionsField), 1, 0);
        
        // Image upload
        VBox imageBox = new VBox(8);
        Label imgLabel = new Label("Parcel Image *");
        imgLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        HBox imageUpload = new HBox(10);
        imageUpload.setAlignment(Pos.CENTER_LEFT);
        Button uploadBtn = new Button("📷 Upload");
        uploadBtn.setStyle("-fx-background-color: #16A085; -fx-text-fill: white; -fx-cursor: hand; -fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8;");
        uploadBtn.setOnAction(e -> handleImageUpload());
        
        imageLabel = new Label("No image selected");
        imageLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
        
        imageUpload.getChildren().addAll(uploadBtn, imageLabel);
        imageBox.getChildren().addAll(imgLabel, imageUpload);
        parcelGrid.add(imageBox, 2, 0);
        
        // Description
        VBox descBox = createFieldBox("Description", descriptionField);
        GridPane.setColumnSpan(descBox, 3);
        parcelGrid.add(descBox, 0, 1);
        
        // Image preview
        imagePreview = new ImageView();
        imagePreview.setFitWidth(400);
        imagePreview.setFitHeight(300);
        imagePreview.setPreserveRatio(true);
        imagePreview.setStyle("-fx-border-color: #dce0e3; -fx-border-width: 2; -fx-border-radius: 8;");
        VBox imagePreviewBox = new VBox(10);
        imagePreviewBox.setAlignment(Pos.CENTER);
        imagePreviewBox.getChildren().add(imagePreview);
        
        Separator sep4 = new Separator();
        sep4.setStyle("-fx-background-color: #ecf0f1;");
        
        // Action buttons
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);
        
        Button submitBtn = new Button("✓ Submit Request");
        submitBtn.setPrefSize(220, 50);
        submitBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        submitBtn.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(46, 204, 113, 0.5)));
        submitBtn.setOnAction(e -> handleSubmitRequest());
        
        Button clearBtn = new Button("Clear Form");
        clearBtn.setPrefSize(180, 50);
        clearBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> clearSubmitForm());
        
        actions.getChildren().addAll(submitBtn, clearBtn);
        
        // Status label
        submitStatusLabel = new Label();
        submitStatusLabel.setAlignment(Pos.CENTER);
        submitStatusLabel.setMaxWidth(Double.MAX_VALUE);
        submitStatusLabel.setWrapText(true);
        submitStatusLabel.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 6;");
        
        // Assemble form
        formCard.getChildren().addAll(
            header, sep1,
            senderLabel, senderGrid, sep2,
            receiverLabel, receiverGrid, sep3,
            parcelLabel, parcelGrid, imagePreviewBox, sep4,
            actions, submitStatusLabel
        );
        
        // Pre-fill sender info
        if (currentCustomer != null) {
            senderNameField.setText(currentCustomer.getUsername());
            senderPhoneField.setText(currentCustomer.getPhone());
            senderAddressField.setText(currentCustomer.getAddress());
        }
        
        content.getChildren().add(formCard);
        submitRequestView.setContent(content);
    }
    
    // Helper methods for form building
    private javafx.scene.layout.ColumnConstraints createColumnConstraint(double percent) {
        javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
        col.setPercentWidth(percent);
        return col;
    }
    
    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-pref-height: 40; -fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        return tf;
    }
    
    private TextArea createTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(2);
        ta.setWrapText(true);
        ta.setStyle("-fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: #dce0e3; -fx-border-radius: 8; -fx-border-width: 2;");
        return ta;
    }
    
    private VBox createFieldBox(String labelText, Control field) {
        VBox box = new VBox(8);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        box.getChildren().addAll(lbl, field);
        return box;
    }
    
    // Image upload handler
    private void handleImageUpload() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Parcel Image");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        Stage stage = (Stage) imageLabel.getScene().getWindow();
        File selected = fc.showOpenDialog(stage);
        
        if (selected != null) {
            try {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                String timestamp = String.valueOf(System.currentTimeMillis());
                String ext = selected.getName().substring(selected.getName().lastIndexOf("."));
                String newName = "parcel_" + timestamp + ext;
                
                File dest = new File("uploads/" + newName);
                Files.copy(selected.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                selectedImagePath = "uploads/" + newName;
                imageLabel.setText("✓ " + selected.getName());
                imageLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 12px;");
                
                Image img = new Image(dest.toURI().toString());
                imagePreview.setImage(img);
                
            } catch (IOException ex) {
                imageLabel.setText("✗ Upload failed");
                imageLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                ex.printStackTrace();
            }
        }
    }
    
    // Submit request handler
    private void handleSubmitRequest() {
        // Validation
        if (!validateSubmitForm()) return;
        
        try {
            String orderId = "ORD_" + System.currentTimeMillis();
            String parcelId = "PCL_" + System.currentTimeMillis();
            
            Order order = new Order(
                orderId,
                currentCustomer.getCustomerId(),
                senderNameField.getText().trim(),
                senderPhoneField.getText().trim(),
                senderAddressField.getText().trim(),
                receiverNameField.getText().trim(),
                receiverPhoneField.getText().trim(),
                receiverAddressField.getText().trim(),
                "Pending"
            );
            
            Parcel parcel = new Parcel(
                parcelId,
                orderId,
                Double.parseDouble(weightField.getText().trim()),
                dimensionsField.getText().trim(),
                descriptionField.getText().trim()
            );
            
            boolean success = orderDAO.createOrderWithNotification(
                order, parcel, selectedImagePath, currentCustomer.getCustomerId()
            );
            
            if (success) {
                submitStatusLabel.setText("✅ Request submitted successfully! Order ID: " + orderId.substring(orderId.length() - 8));
                submitStatusLabel.setStyle("-fx-text-fill: #2ECC71; -fx-background-color: #D5F4E6; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 6;");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Delivery Request Submitted");
                alert.setContentText("Your request has been submitted!\n\nOrder ID: " + orderId + "\nStatus: Pending\n\nCheck Notifications for updates.");
                alert.showAndWait();
                
                clearSubmitForm();
                loadDashboardData();
                updateNotificationBadge();
                
            } else {
                submitStatusLabel.setText("❌ Failed to submit request. Please try again.");
                submitStatusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FADBD8; -fx-font-weight: bold;");
            }
            
        } catch (Exception e) {
            submitStatusLabel.setText("❌ Error: " + e.getMessage());
            submitStatusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FADBD8; -fx-font-weight: bold;");
            e.printStackTrace();
        }
    }
    
    private boolean validateSubmitForm() {
        StringBuilder errors = new StringBuilder();
        
        if (senderNameField.getText().trim().isEmpty()) errors.append("• Sender name required\n");
        if (senderPhoneField.getText().trim().isEmpty()) errors.append("• Sender phone required\n");
        if (senderAddressField.getText().trim().isEmpty()) errors.append("• Sender address required\n");
        if (receiverNameField.getText().trim().isEmpty()) errors.append("• Receiver name required\n");
        if (receiverPhoneField.getText().trim().isEmpty()) errors.append("• Receiver phone required\n");
        if (receiverAddressField.getText().trim().isEmpty()) errors.append("• Receiver address required\n");
        if (weightField.getText().trim().isEmpty()) {
            errors.append("• Weight required\n");
        } else {
            try {
                double w = Double.parseDouble(weightField.getText().trim());
                if (w <= 0) errors.append("• Weight must be > 0\n");
            } catch (NumberFormatException e) {
                errors.append("• Invalid weight format\n");
            }
        }
        if (selectedImagePath == null) errors.append("• Parcel image required\n");
        
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fix these issues:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            
            submitStatusLabel.setText("❌ Please fill all required fields");
            submitStatusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FADBD8; -fx-font-weight: bold;");
            return false;
        }
        
        return true;
    }
    
    private void clearSubmitForm() {
        receiverNameField.clear();
        receiverPhoneField.clear();
        receiverAddressField.clear();
        weightField.clear();
        dimensionsField.clear();
        descriptionField.clear();
        selectedImagePath = null;
        imageLabel.setText("No image selected");
        imageLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
        imagePreview.setImage(null);
        submitStatusLabel.setText("");
    }
    
    // Build other views (simplified for space)
    private void buildMyOrdersView() {
        VBox content = new VBox(20);
        content.setStyle("-fx-padding: 0;");
        myOrdersView.setContent(content);
    }
    
    private void buildNotificationsView() {
        VBox content = new VBox(20);
        content.setStyle("-fx-padding: 0;");
        notificationsView.setContent(content);
    }
    
    private void buildProfileView() {
        VBox content = new VBox(20);
        content.setStyle("-fx-padding: 0;");
        profileView.setContent(content);
    }
    
    private void refreshMyOrdersView() {
    VBox content = new VBox(25);
    content.setStyle("-fx-padding: 0;");
    
    // Header Card
    VBox headerCard = new VBox(15);
    headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 30;");
    headerCard.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
    
    HBox header = new HBox(15);
    header.setAlignment(Pos.CENTER_LEFT);
    Label icon = new Label("📋");
    icon.setStyle("-fx-font-size: 32px;");
    VBox headerText = new VBox(3);
    Label title = new Label("My Orders");
    title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-font-family: 'Century Gothic';");
    Label subtitle = new Label("Track and manage all your delivery orders");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
    headerText.getChildren().addAll(title, subtitle);
    header.getChildren().addAll(icon, headerText);
    
    headerCard.getChildren().add(header);
    
    // Get all orders
    List<Order> orders = orderDAO.getOrdersByCustomer(currentCustomer.getCustomerId());
    
    if (orders.isEmpty()) {
        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 60;");
        emptyState.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
        
        Label emptyIcon = new Label("📭");
        emptyIcon.setStyle("-fx-font-size: 80px;");
        Label emptyText = new Label("No Orders Yet");
        emptyText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        Label emptySubtext = new Label("Start by creating your first delivery request");
        emptySubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6;");
        
        Button createBtn = new Button("📝 Create New Request");
        createBtn.setPrefSize(250, 50);
        createBtn.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        createBtn.setOnAction(e -> showSubmitRequest());
        
        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext, createBtn);
        content.getChildren().addAll(headerCard, emptyState);
    } else {
        // Orders Grid
        VBox ordersContainer = new VBox(15);
        ordersContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 30;");
        ordersContainer.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
        
        Label ordersTitle = new Label("All Orders (" + orders.size() + ")");
        ordersTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #ecf0f1;");
        
        ordersContainer.getChildren().addAll(ordersTitle, sep);
        
        for (Order order : orders) {
            ordersContainer.getChildren().add(createDetailedOrderCard(order));
        }
        
        content.getChildren().addAll(headerCard, ordersContainer);
    }
    
    myOrdersView.setContent(content);
}

private VBox createDetailedOrderCard(Order order) {
    VBox card = new VBox(15);
    card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10;");
    
    // Header row
    HBox headerRow = new HBox();
    headerRow.setAlignment(Pos.CENTER_LEFT);
    
    VBox orderInfo = new VBox(5);
    HBox.setHgrow(orderInfo, Priority.ALWAYS);
    
    Label orderId = new Label("Order #" + order.getOrderId().substring(Math.max(0, order.getOrderId().length() - 12)));
    orderId.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
    
    Label orderDate = new Label("📅 " + formatDate(order.getOrderDate()));
    orderDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
    
    orderInfo.getChildren().addAll(orderId, orderDate);
    
    Label statusBadge = createStatusBadge(order.getStatus());
    
    headerRow.getChildren().addAll(orderInfo, statusBadge);
    
    // Details Grid
    GridPane detailsGrid = new GridPane();
    detailsGrid.setHgap(40);
    detailsGrid.setVgap(12);
    detailsGrid.setStyle("-fx-padding: 10 0;");
    
    // Sender info
    VBox senderBox = new VBox(5);
    Label senderLabel = new Label("📤 FROM");
    senderLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
    Label senderName = new Label(order.getSenderName());
    senderName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
    Label senderPhone = new Label(order.getSenderPhone());
    senderPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
    Label senderAddr = new Label(order.getSenderAddress());
    senderAddr.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");
    senderAddr.setMaxWidth(300);
    senderAddr.setWrapText(true);
    senderBox.getChildren().addAll(senderLabel, senderName, senderPhone, senderAddr);
    
    // Receiver info
    VBox receiverBox = new VBox(5);
    Label receiverLabel = new Label("📥 TO");
    receiverLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
    Label receiverName = new Label(order.getReceiverName());
    receiverName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
    Label receiverPhone = new Label(order.getReceiverPhone());
    receiverPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
    Label receiverAddr = new Label(order.getReceiverAddress());
    receiverAddr.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");
    receiverAddr.setMaxWidth(300);
    receiverAddr.setWrapText(true);
    receiverBox.getChildren().addAll(receiverLabel, receiverName, receiverPhone, receiverAddr);
    
    detailsGrid.add(senderBox, 0, 0);
    detailsGrid.add(receiverBox, 1, 0);
    
    // Action button
    Button viewDetailsBtn = new Button("View Details →");
    viewDetailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-cursor: hand; -fx-pref-height: 35; -fx-font-size: 12px; -fx-background-radius: 6;");
    viewDetailsBtn.setOnAction(e -> showOrderDetails(order));
    
    card.getChildren().addAll(headerRow, new Separator(), detailsGrid, viewDetailsBtn);
    
    return card;
}

private void showOrderDetails(Order order) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Order Details");
    alert.setHeaderText("Order #" + order.getOrderId());
    
    StringBuilder details = new StringBuilder();
    details.append("Status: ").append(order.getStatus()).append("\n");
    details.append("Order Date: ").append(formatDate(order.getOrderDate())).append("\n\n");
    
    details.append("FROM:\n");
    details.append("  Name: ").append(order.getSenderName()).append("\n");
    details.append("  Phone: ").append(order.getSenderPhone()).append("\n");
    details.append("  Address: ").append(order.getSenderAddress()).append("\n\n");
    
    details.append("TO:\n");
    details.append("  Name: ").append(order.getReceiverName()).append("\n");
    details.append("  Phone: ").append(order.getReceiverPhone()).append("\n");
    details.append("  Address: ").append(order.getReceiverAddress()).append("\n");
    
    alert.setContentText(details.toString());
    alert.showAndWait();
}

private void refreshNotificationsView() {
    VBox content = new VBox(25);
    content.setStyle("-fx-padding: 0;");
    
    // Header Card
    VBox headerCard = new VBox(15);
    headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 30;");
    headerCard.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
    
    HBox header = new HBox(15);
    header.setAlignment(Pos.CENTER_LEFT);
    Label icon = new Label("🔔");
    icon.setStyle("-fx-font-size: 32px;");
    VBox headerText = new VBox(3);
    Label title = new Label("Notifications");
    title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-font-family: 'Century Gothic';");
    Label subtitle = new Label("Stay updated with your delivery status");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
    headerText.getChildren().addAll(title, subtitle);
    header.getChildren().addAll(icon, headerText);
    
    // Mark all as read button
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Button markAllReadBtn = new Button("✓ Mark All Read");
    markAllReadBtn.setStyle("-fx-background-color: #16A085; -fx-text-fill: white; -fx-cursor: hand; -fx-pref-height: 35; -fx-font-size: 12px; -fx-background-radius: 6;");
    markAllReadBtn.setOnAction(e -> markAllNotificationsAsRead());
    header.getChildren().addAll(spacer, markAllReadBtn);
    
    headerCard.getChildren().add(header);
    
    // Get notifications
    List<Notification> notifications = notificationDAO.getNotificationsByCustomer(currentCustomer.getCustomerId());
    
    if (notifications.isEmpty()) {
        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 60;");
        emptyState.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
        
        Label emptyIcon = new Label("🔕");
        emptyIcon.setStyle("-fx-font-size: 80px;");
        Label emptyText = new Label("No Notifications");
        emptyText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        Label emptySubtext = new Label("You're all caught up!");
        emptySubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6;");
        
        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
        content.getChildren().addAll(headerCard, emptyState);
    } else {
        // Notifications Container
        VBox notificationsContainer = new VBox(12);
        notificationsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 30;");
        notificationsContainer.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
        
        for (Notification notif : notifications) {
            notificationsContainer.getChildren().add(createNotificationCard(notif));
        }
        
        content.getChildren().addAll(headerCard, notificationsContainer);
    }
    
    notificationsView.setContent(content);
}

private HBox createNotificationCard(Notification notif) {
    HBox card = new HBox(15);
    card.setAlignment(Pos.CENTER_LEFT);
    card.setStyle("-fx-padding: 20; -fx-background-radius: 8; " +
                  (notif.isRead() ? "-fx-background-color: #f8f9fa;" : "-fx-background-color: #e8f4f8;"));
    
    // Icon based on type
    String iconEmoji = "ℹ️";
    String iconBg = "#3498DB";
    if ("success".equals(notif.getType())) {
        iconEmoji = "✅";
        iconBg = "#2ECC71";
    } else if ("warning".equals(notif.getType())) {
        iconEmoji = "⚠️";
        iconBg = "#F39C12";
    }
    
    Label icon = new Label(iconEmoji);
    icon.setStyle("-fx-font-size: 24px; -fx-background-color: " + iconBg + "20; -fx-background-radius: 8; -fx-padding: 10;");
    
    // Message content
    VBox contentBox = new VBox(5);
    HBox.setHgrow(contentBox, Priority.ALWAYS);
    
    Label message = new Label(notif.getMessage());
    message.setWrapText(true);
    message.setMaxWidth(700);
    message.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C3E50;" + 
                     (notif.isRead() ? "" : " -fx-font-weight: bold;"));
    
    Label date = new Label(formatDate(notif.getCreatedDate()));
    date.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
    
    contentBox.getChildren().addAll(message, date);
    
    // Read indicator
    VBox statusBox = new VBox(5);
    statusBox.setAlignment(Pos.CENTER);
    if (!notif.isRead()) {
        Label unreadBadge = new Label("NEW");
        unreadBadge.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
        statusBox.getChildren().add(unreadBadge);
        
        Button markReadBtn = new Button("Mark Read");
        markReadBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498DB; -fx-cursor: hand; -fx-font-size: 11px; -fx-underline: true;");
        markReadBtn.setOnAction(e -> {
            notificationDAO.markAsRead(notif.getNotificationId());
            refreshNotificationsView();
            updateNotificationBadge();
        });
        statusBox.getChildren().add(markReadBtn);
    }
    
    card.getChildren().addAll(icon, contentBox, statusBox);
    
    return card;
}

private void markAllNotificationsAsRead() {
    List<Notification> notifications = notificationDAO.getNotificationsByCustomer(currentCustomer.getCustomerId());
    for (Notification notif : notifications) {
        if (!notif.isRead()) {
            notificationDAO.markAsRead(notif.getNotificationId());
        }
    }
    refreshNotificationsView();
    updateNotificationBadge();
}

private void refreshProfileView() {
    VBox content = new VBox(25);
    content.setStyle("-fx-padding: 0;");
    
    // Profile Header Card
    VBox profileCard = new VBox(25);
    profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 40;");
    profileCard.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0, 0, 0, 0.1)));
    
    // Header
    HBox header = new HBox(15);
    header.setAlignment(Pos.CENTER_LEFT);
    Label icon = new Label("👤");
    icon.setStyle("-fx-font-size: 32px;");
    VBox headerText = new VBox(3);
    Label title = new Label("My Profile");
    title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-font-family: 'Century Gothic';");
    Label subtitle = new Label("Manage your account information");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
    headerText.getChildren().addAll(title, subtitle);
    header.getChildren().addAll(icon, headerText);
    
    Separator sep1 = new Separator();
    sep1.setStyle("-fx-background-color: #ecf0f1;");
    
    // Profile Avatar
    VBox avatarBox = new VBox(15);
    avatarBox.setAlignment(Pos.CENTER);
    Label avatar = new Label("👤");
    avatar.setStyle("-fx-font-size: 80px; -fx-background-color: #2980B9; -fx-background-radius: 50%; -fx-padding: 30;");
    Label customerName = new Label(currentCustomer.getUsername());
    customerName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
    Label customerId = new Label("Customer ID: " + currentCustomer.getCustomerId());
    customerId.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
    avatarBox.getChildren().addAll(avatar, customerName, customerId);
    
    Separator sep2 = new Separator();
    sep2.setStyle("-fx-background-color: #ecf0f1;");
    
    // Profile Details Grid
    GridPane detailsGrid = new GridPane();
    detailsGrid.setHgap(40);
    detailsGrid.setVgap(20);
    detailsGrid.setStyle("-fx-padding: 20 0;");
    
    int row = 0;
    
    // Username
    detailsGrid.add(createProfileDetailRow("👤 Username", currentCustomer.getUsername()), 0, row++);
    
    // Email
    if (currentCustomer.getEmail() != null && !currentCustomer.getEmail().isEmpty()) {
        detailsGrid.add(createProfileDetailRow("📧 Email", currentCustomer.getEmail()), 0, row++);
    }
    
    // Phone
    if (currentCustomer.getPhone() != null && !currentCustomer.getPhone().isEmpty()) {
        detailsGrid.add(createProfileDetailRow("📱 Phone", currentCustomer.getPhone()), 0, row++);
    }
    
    // Address
    if (currentCustomer.getAddress() != null && !currentCustomer.getAddress().isEmpty()) {
        detailsGrid.add(createProfileDetailRow("📍 Address", currentCustomer.getAddress()), 0, row++);
    }
    
    // Account Stats
    List<Order> orders = orderDAO.getOrdersByCustomer(currentCustomer.getCustomerId());
    int totalOrders = orders.size();
    int deliveredOrders = (int) orders.stream().filter(o -> "Delivered".equals(o.getStatus())).count();
    
    Separator sep3 = new Separator();
    sep3.setStyle("-fx-background-color: #ecf0f1;");
    
    Label statsTitle = new Label("📊 Account Statistics");
    statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980B9;");
    
    HBox statsBox = new HBox(30);
    statsBox.setAlignment(Pos.CENTER);
    statsBox.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa; -fx-background-radius: 10;");
    
    VBox totalBox = createStatBox("Total Orders", String.valueOf(totalOrders), "#3498DB");
    VBox deliveredBox = createStatBox("Delivered", String.valueOf(deliveredOrders), "#2ECC71");
    VBox rateBox = createStatBox("Success Rate", 
                                  totalOrders > 0 ? String.format("%.1f%%", (deliveredOrders * 100.0 / totalOrders)) : "0%", 
                                  "#9B59B6");
    
    statsBox.getChildren().addAll(totalBox, deliveredBox, rateBox);
    
    // Action Buttons
    HBox actions = new HBox(15);
    actions.setAlignment(Pos.CENTER);
    actions.setStyle("-fx-padding: 20 0 0 0;");
    
    Button editBtn = new Button("✏️ Edit Profile");
    editBtn.setPrefSize(200, 45);
    editBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
    editBtn.setOnAction(e -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Profile");
        alert.setHeaderText("Feature Coming Soon");
        alert.setContentText("Profile editing will be available in the next update!");
        alert.showAndWait();
    });
    
    Button changePassBtn = new Button("🔒 Change Password");
    changePassBtn.setPrefSize(200, 45);
    changePassBtn.setStyle("-fx-background-color: #16A085; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
    changePassBtn.setOnAction(e -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change Password");
        alert.setHeaderText("Feature Coming Soon");
        alert.setContentText("Password change will be available in the next update!");
        alert.showAndWait();
    });
    
    actions.getChildren().addAll(editBtn, changePassBtn);
    
    // Assemble profile card
    profileCard.getChildren().addAll(
        header, sep1,
        avatarBox, sep2,
        detailsGrid, sep3,
        statsTitle, statsBox,
        actions
    );
    
    content.getChildren().add(profileCard);
    profileView.setContent(content);
}

private VBox createProfileDetailRow(String label, String value) {
    VBox row = new VBox(8);
    
    Label lblTitle = new Label(label);
    lblTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
    
    Label lblValue = new Label(value);
    lblValue.setStyle("-fx-font-size: 15px; -fx-text-fill: #2C3E50;");
    lblValue.setWrapText(true);
    lblValue.setMaxWidth(600);
    
    row.getChildren().addAll(lblTitle, lblValue);
    return row;
}

private VBox createStatBox(String label, String value, String color) {
    VBox box = new VBox(8);
    box.setAlignment(Pos.CENTER);
    box.setStyle("-fx-padding: 15 30; -fx-background-color: white; -fx-background-radius: 8;");
    
    Label lblValue = new Label(value);
    lblValue.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
    
    Label lblTitle = new Label(label);
    lblTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
    
    box.getChildren().addAll(lblValue, lblTitle);
    return box;
}

private void updateNotificationBadge() {
    if (currentCustomer == null) return;
    
    List<Notification> notifications = notificationDAO.getNotificationsByCustomer(currentCustomer.getCustomerId());
    long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
    
    if (unreadCount > 0) {
        notificationBadge.setText(String.valueOf(unreadCount));
        notificationBadge.setVisible(true);
    } else {
        notificationBadge.setVisible(false);
    }
}

private String formatDate(java.sql.Timestamp timestamp) {
    if (timestamp == null) return "N/A";
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a");
    return sdf.format(new java.util.Date(timestamp.getTime()));
}

@FXML
private void handleLogout() {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Logout");
    confirm.setHeaderText("Are you sure you want to logout?");
    confirm.setContentText("You will be redirected to the login screen.");
    
    confirm.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) btnDashboard.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("SwiftShip - Login");
                stage.show();
                
                System.out.println("✅ User logged out successfully");
                
            } catch (IOException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Logout Failed");
                error.setContentText("Could not return to login screen: " + e.getMessage());
                error.showAndWait();
            }
        }
    });
}

}