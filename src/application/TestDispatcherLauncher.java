package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Dispatcher;

public class TestDispatcherLauncher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DispatcherDashboard.fxml"));
        Parent root = loader.load();

        // set up a test dispatcher (adjust fields to match your model)
        Dispatcher testDispatcher = new Dispatcher();
        testDispatcher.setDispatcherId("DISP001");
        testDispatcher.setUsername("DispatcherTest");
        testDispatcher.setAssignedZone("Rawalpindi/Islamabad");

        // inject dispatcher into controller
        DispatcherDashboardController controller = loader.getController();
        controller.setDispatcher(testDispatcher);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Dispatcher Dashboard - Test Launcher");
        stage.show();
    }
}
