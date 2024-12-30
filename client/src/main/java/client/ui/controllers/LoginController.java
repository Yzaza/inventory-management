package client.ui.controllers;

import common.interfaces.AuthInterface;
import common.util.Logger;
import common.models.Employee;
import server.util.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The LoginController class handles the user login functionality of the application.
 * It interacts with the UI and backend services, including authentication logic and
 * navigation to the dashboard upon successful login.
 *
 * This controller is associated with the login view and utilizes JavaFX annotations to
 * connect UI components and event handlers.
 *
 * Responsibilities:
 * - Initialize the authentication service through RMI.
 * - Process user credentials input and validate them against the authentication service.
 * - Provide feedback to the user in cases of errors or unsuccessful login attempts.
 * - Redirect authenticated users to the dashboard view while passing user-specific information.
 *
 * Key Elements:
 * - usernameField: Input field for the username.
 * - passwordField: Input field for the password.
 * - errorLabel: Label to display error messages.
 * - authService: An instance of the AuthInterface used for remote authentication.
 *
 * Methods:
 * - initialize(): Sets up the authentication service by connecting to an RMI registry. Handles
 *   connection errors by displaying relevant messages.
 * - handleLogin(): Captures the user input, sends it to the authentication service, and handles
 *   the response. Provides feedback through the UI or navigates to the dashboard for successful login.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthInterface authService;

    @FXML
    public void initialize() {
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            int port = config.getRmiPort();
            Registry registry = LocateRegistry.getRegistry("localhost",port);
            authService = (AuthInterface) registry.lookup("AuthService");
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to connect to RMI registry: " + e.getMessage(), "system");
            this.errorLabel.setText("Error connecting to server");
            this.errorLabel.setVisible(true);
        }
    }


    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Employee employee = authService.authenticate(username, password);
            if (employee == null) {
                Logger.log("LOGIN_FAILED", "Invalid login attempt for username: " + username, "system");
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
                return;
            }

            if (employee.isAuthenticated()) {
                Logger.log("LOGIN_SUCCESS", "User logged in successfully: " + username, username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/DashboardView.fxml"));
                Parent root = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.setUserInfo(employee.isAdmin(), employee.getFullname(), username);

                Scene dashboardScene = new Scene(root, 600, 400);
                stage.setScene(dashboardScene);
                stage.setTitle("Dashboard");
                stage.setResizable(false);
                stage.centerOnScreen();
            } else {
                Logger.log("LOGIN_FAILED", "Authentication failed for username: " + username, "system");
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            Logger.log("ERROR", "Login error: " + e.getMessage(), "system");
            e.printStackTrace();
            errorLabel.setText("Error connecting to server");
            errorLabel.setVisible(true);
        }
    }
}