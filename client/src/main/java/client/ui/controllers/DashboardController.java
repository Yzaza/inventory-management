package client.ui.controllers;

import common.interfaces.InventoryInterface;
import common.util.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import common.models.Product;
import common.models.Employee;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import javafx.scene.control.Alert.AlertType;
import server.util.DatabaseConfig;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDateTime;

/**
 * The DashboardController class handles the interaction and logic for the main dashboard of the application.
 * It controls the display and functionality for managing products and employees within the application.
 * The class interacts with an RMI-based inventory service to fetch and manipulate data.
 * It supports navigation between different dashboard views, such as products and employees.
 *
 * Key functionalities provided by this class include:
 * - Initializing and configuring product and employee tables.
 * - Loading product and employee data from the inventory service.
 * - Allowing product operations such as adding, updating, and deleting products.
 * - Allowing employee operations such as adding and updating employees, with restricted access for non-admin users.
 * - Switching between user-friendly product and employee dashboard views.
 *
 * This class utilizes JavaFX annotations and components for UI management and user interaction.
 */
public class DashboardController {
    @FXML private VBox productContainer;
    @FXML private VBox employeeContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox filterComboBox;
    @FXML private TableColumn employeeFullnameColumn;
    @FXML private Label employeeNameLabel;
    @FXML private VBox vboxContainer;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> employeeUsernameColumn;
    @FXML private TableColumn<Employee, String> employeeRoleColumn;

    private ObservableList<Product> productData;
    private ObservableList<Employee> employeeData;
    private InventoryInterface inventoryService;
    private Boolean isAdmin;
    private String currentUsername;
    private double height = 400;
    private double width = 500;

    public DashboardController() {
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            int port = config.getRmiPort();
            Registry registry = LocateRegistry.getRegistry("localhost",port);
            inventoryService = (InventoryInterface) registry.lookup("InventoryService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        productContainer.setVisible(true);
        productContainer.setManaged(true);
        employeeContainer.setVisible(false);
        employeeContainer.setManaged(false);
        initializeProductTable();
        initializeEmployeeTable();
        filterComboBox.getSelectionModel().selectFirst();
        loadProducts();
    }

    private void initializeProductTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productTable.getColumns().forEach(column -> column.setPrefWidth(width/5));
    }

    private void initializeEmployeeTable() {
        employeeUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        employeeFullnameColumn.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        employeeRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        employeeTable.getColumns().forEach(column -> column.setPrefWidth(width/3));
    }

    @FXML
    public void handleProductDashboard() {
        productContainer.setVisible(true);
        productContainer.setManaged(true);
        employeeContainer.setVisible(false);
        employeeContainer.setManaged(false);
        loadProducts();
        Logger.log("NAVIGATION", "Switched to Product Dashboard", currentUsername);
    }

    @FXML
    public void handleEmployeeDashboard() {
        if (!isAdmin) {
            showAlert(AlertType.ERROR, "Access Denied", "You need admin privileges to access the Employee Dashboard");
            return;
        }
        employeeContainer.setVisible(true);
        employeeContainer.setManaged(true);
        productContainer.setVisible(false);
        productContainer.setManaged(false);
        loadEmployees();
        Logger.log("NAVIGATION", "Switched to Employee Dashboard", currentUsername);
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = inventoryService.getAllEmployees();
            employeeData = FXCollections.observableArrayList(employees);
            employeeTable.setItems(employeeData);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load employees");
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = inventoryService.getAllProducts();
            productData = FXCollections.observableArrayList(products);
            productTable.setItems(productData);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load products");
        }
    }

    @FXML
    public void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/ProductFormView.fxml"));
            Parent root = loader.load();
            ProductFormController formController = loader.getController();
            formController.setInventoryService(inventoryService);
            formController.setCurrentUsername(currentUsername);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Product");
            stage.showAndWait();

            loadProducts();
            Logger.log("PRODUCT", "Opened Add Product form", currentUsername);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not open product form");
        }
    }

    @FXML
    public void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(AlertType.ERROR, "No Selection", "Please select a product to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/ProductFormView.fxml"));
            Parent root = loader.load();

            ProductFormController formController = loader.getController();
            formController.setInventoryService(inventoryService);
            formController.setProductToUpdate(selectedProduct);
            formController.setCurrentUsername(currentUsername);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Product");
            stage.showAndWait();

            loadProducts();
            Logger.log("PRODUCT", "Updated product: " + selectedProduct.getName(), currentUsername);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not open update form");
        }
    }

    @FXML
    public void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(AlertType.ERROR, "No Selection", "Please select a product to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to delete this product?",
                ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait();

        if (confirmationAlert.getResult() == ButtonType.YES) {
            try {
                inventoryService.deleteProduct(selectedProduct.getId(), currentUsername);
                loadProducts();
                Logger.log("PRODUCT", "Deleted product: " + selectedProduct.getName(), currentUsername);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Deletion Error", "Could not delete the product.");
            }
        }
    }

    @FXML
    public void handleAddEmployee() {
        if (!isAdmin) {
            showAlert(AlertType.ERROR, "Access Denied", "You need admin privileges to add employees");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/EmployeeFormView.fxml"));
            Parent root = loader.load();

            EmployeeFormController formController = loader.getController();
            formController.setInventoryService(inventoryService);
            formController.setCurrentUsername(currentUsername);
            formController.addEmployee();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Employee");
            stage.showAndWait();

            loadEmployees();
            Logger.log("EMPLOYEE", "Opened Add Employee form", currentUsername);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not open employee form");
        }
    }

    @FXML
    public void handleUpdateEmployee() {
        if (!isAdmin) {
            showAlert(AlertType.ERROR, "Access Denied", "You need admin privileges to update employees");
            return;
        }

        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(AlertType.ERROR, "No Selection", "Please select an employee to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/EmployeeFormView.fxml"));
            Parent root = loader.load();

            EmployeeFormController formController = loader.getController();
            formController.setInventoryService(inventoryService);
            formController.setEmployeeToUpdate(selectedEmployee);
            formController.setCurrentUsername(currentUsername);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Employee");
            stage.showAndWait();

            loadEmployees();
            Logger.log("EMPLOYEE", "Updated employee: " + selectedEmployee.getUsername(), currentUsername);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not open update form");
        }
    }

    @FXML
    public void handleDeleteEmployee() {
        if (!isAdmin) {
            showAlert(AlertType.ERROR, "Access Denied", "You need admin privileges to delete employees");
            return;
        }

        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(AlertType.ERROR, "No Selection", "Please select an employee to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to delete this employee?",
                ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait();

        if (confirmationAlert.getResult() == ButtonType.YES) {
            try {
                inventoryService.deleteEmployee(selectedEmployee.getId(), currentUsername);
                loadEmployees();
                Logger.log("EMPLOYEE", "Deleted employee: " + selectedEmployee.getUsername(), currentUsername);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Deletion Error", "Could not delete the employee.");
            }
        }
    }

    public void setUserInfo(Boolean isAdmin, String employeeName, String username) {
        this.isAdmin = isAdmin;
        this.employeeNameLabel.setText(employeeName);
        this.currentUsername = username;
        Logger.log("LOGIN", "User accessed dashboard", username);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleSearch(ActionEvent actionEvent) throws RemoteException {
        String filter = filterComboBox.getSelectionModel().getSelectedItem().toString();
        List<Product> filteredProducts = null;

        try {
            switch (filter) {
                case "Name":
                    filteredProducts = inventoryService.getProductsByName(searchField.getText());
                    break;
                case "Category":
                    filteredProducts = inventoryService.getProductsByCategory(searchField.getText());
                    break;
                case "Quantity":
                    try {
                        int quantity = Integer.parseInt(searchField.getText());
                        filteredProducts = inventoryService.getProductsByQuantity(quantity);
                    } catch (NumberFormatException e) {
                        showAlert(AlertType.ERROR, "Invalid Input", "Please enter a valid number for quantity");
                        return;
                    }
                    break;
            }

            if (filteredProducts != null) {
                productTable.setItems(FXCollections.observableArrayList(filteredProducts));
                Logger.log("SEARCH", "Searched products with filter: " + filter + ", value: " + searchField.getText(), currentUsername);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Search Error", "An error occurred while searching");
        }
    }

    @FXML
    public void logout(ActionEvent actionEvent) throws IOException {
        Logger.log("LOGOUT", "User logged out", currentUsername);
        Stage stage = (Stage) productTable.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/views/LoginView.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.centerOnScreen();
    }
}