package client.ui.controllers;

import common.interfaces.InventoryInterface;
import common.util.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import common.models.Product;
import java.math.BigDecimal;


/**
 * The ProductFormController class is responsible for handling the operations and interactions
 * with the product form user interface. It facilitates creating or updating products in the
 * inventory system and manages user inputs to ensure correct data handling.
 *
 * This controller relies on interaction with an InventoryInterface implementation for performing
 * product-related operations such as adding or updating products in the inventory.
 *
 * Key functionalities include:
 * - Populating the form fields with existing product data for updates.
 * - Handling user actions for saving or cancelling form operations.
 * - Validating and processing user input to ensure correct product data before saving.
 */
public class ProductFormController {
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;

    private InventoryInterface inventoryService;
    private Product productToUpdate;
    private String currentUsername;

    public void setInventoryService(InventoryInterface inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void setProductToUpdate(Product product) {
        this.productToUpdate = product;
        nameField.setText(product.getName());
        categoryField.setText(product.getCategory());
        quantityField.setText(String.valueOf(product.getQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
    }


    @FXML
    public void handleSave() {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            BigDecimal price = new BigDecimal(priceField.getText());

            if (productToUpdate == null) {
                Product newProduct = new Product(name, category, quantity, price);
                inventoryService.addProduct(newProduct, currentUsername);
                Logger.log("PRODUCT", "Created new product: " + name, currentUsername);
            } else {
                productToUpdate.setName(name);
                productToUpdate.setCategory(category);
                productToUpdate.setQuantity(quantity);
                productToUpdate.setPrice(price);
                inventoryService.updateProduct(productToUpdate, currentUsername);
                Logger.log("PRODUCT", "Updated product: " + name, currentUsername);
            }

            closeForm();
        } catch (NumberFormatException e) {
            Logger.log("ERROR", "Invalid number format in product form", currentUsername);
            // Show error alert
        } catch (Exception e) {
            Logger.log("ERROR", "Error saving product: " + e.getMessage(), currentUsername);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        Logger.log("CANCEL", "Cancelled product form", currentUsername);
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}