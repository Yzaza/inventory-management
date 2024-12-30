package client.ui.controllers;

import common.interfaces.InventoryInterface;
import common.util.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import common.models.Employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * The EmployeeFormController class is responsible for managing the form interface
 * used to add, update, or cancel operations for employees within the application.
 *
 * This controller handles the interaction logic for the GUI components and communicates
 * with the remote InventoryInterface to perform operations on employee data. It uses
 * logging for tracking user actions and errors.
 */
public class EmployeeFormController {
    @FXML private TextField employeeFullnameField;
    @FXML private TextField employeeIdField;
    @FXML private TextField employeeUsernameField;
    @FXML private PasswordField employeePasswordField;
    @FXML private ComboBox employeeRoleField;
    @FXML private TextField employeeCreatedAtField;

    private InventoryInterface inventoryService;
    private Employee employeeToUpdate;
    private String currentUsername;

    public void initialize() {
        employeeRoleField.getSelectionModel().selectFirst();
    }

    public void setInventoryService(InventoryInterface inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void addEmployee() {
        employeeIdField.setDisable(true);
        LocalDateTime now = LocalDateTime.now();
        employeeCreatedAtField.setText(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        employeeCreatedAtField.setDisable(true);
    }

    public void setEmployeeToUpdate(Employee employee) {
        this.employeeToUpdate = employee;

        employeeIdField.setText(String.valueOf(employee.getId()));
        employeeUsernameField.setText(employee.getUsername());
        employeeFullnameField.setText(employee.getFullname());
        employeePasswordField.setText(employee.getPassword());
        employeeRoleField.getSelectionModel().select(employee.getRole());
        employeeCreatedAtField.setText(employee.getCreatedAt().toString());

        employeeIdField.setDisable(true);
        employeeCreatedAtField.setDisable(true);
    }


    @FXML
    public void handleSave(ActionEvent actionEvent) {
        try {
            String username = employeeUsernameField.getText();
            String fullname = employeeFullnameField.getText();
            String password = employeePasswordField.getText();
            String role = employeeRoleField.getSelectionModel().getSelectedItem().toString();

            if (employeeToUpdate == null) {
                Employee newEmployee = new Employee(username, fullname, password, role);
                inventoryService.addEmployee(newEmployee, currentUsername);
                Logger.log("EMPLOYEE", "Created new employee: " + username, currentUsername);
            } else {
                boolean updatePassword = !employeeToUpdate.getPassword().equals(password);
                Employee updatedEmployee = updatePassword ?
                        new Employee(employeeToUpdate.getId(), username, fullname, password, role, null) :
                        new Employee(employeeToUpdate.getId(), username, fullname, role);

                inventoryService.updateEmployee(updatedEmployee, updatePassword, currentUsername);
                Logger.log("EMPLOYEE", "Updated employee: " + username + (updatePassword ? " (password changed)" : ""), currentUsername);
            }

            closeForm();
        } catch (Exception e) {
            Logger.log("ERROR", "Error saving employee: " + e.getMessage(), currentUsername);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        Logger.log("CANCEL", "Cancelled employee form", currentUsername);
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) employeeUsernameField.getScene().getWindow();
        stage.close();
    }
}