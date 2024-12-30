import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Client class is a JavaFX application serving as the front-end entry point
 * for the Inventory Management system. It initializes and displays the graphical
 * user interface for the application using an FXML file.
 *
 * This class inherits from javafx.application.Application and overrides the start method
 * to set up and display the primary stage of the application.
 *
 * Features:
 * - Loads an FXML file to define the application's visual layout.
 * - Displays the login view as the initial screen of the application.
 * - Ensures the availability of required resources (FXML file).
 *
 * Responsibilities:
 * - Initialize the JavaFX runtime and manage the application lifecycle.
 * - Validate the presence of the FXML resource and handle its loading.
 *
 * Error Handling:
 * - Throws an IllegalStateException if the FXML file is not found.
 *
 * Notes:
 * - This class is typically launched from the application's main method through
 *   the `launch` method provided by JavaFX's Application class.
 */
public class Client extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {


        // Use getClass().getResource to load the FXML file from resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/LoginView.fxml"));

        if (loader.getLocation() == null) {
            throw new IllegalStateException("FXML file not found!");
        }

        // Load the FXML and set up the scene
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventory Management - Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
