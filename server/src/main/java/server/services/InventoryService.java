package server.services;

import common.interfaces.InventoryInterface;
import common.util.Logger;
import server.dao.EmployeeDAO;
import server.dao.ProductDAO;
import common.models.Employee;
import common.models.Product;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * The InventoryService class provides a remote implementation of the InventoryInterface.
 * It manages inventory operations such as product and employee management.
 * This class communicates with the data layer (ProductDAO and EmployeeDAO) to fetch, add, update,
 * and delete records related to products and employees.
 * It also logs actions taken on the system for audit and tracking purposes.
 */
public class InventoryService extends UnicastRemoteObject implements InventoryInterface {
    private final ProductDAO productDAO;
    private final EmployeeDAO employeeDAO;

    /**
     * Constructs a new instance of the InventoryService class.
     * The constructor initializes the data access objects (DAOs) used to manage employees and products.
     *
     * @throws RemoteException if a remote communication error occurs during object export.
     */
    public InventoryService() throws RemoteException {
        super();
        employeeDAO = new EmployeeDAO();
        productDAO = new ProductDAO();
    }

    /**
     * Retrieves a list of all products from the inventory.
     * This method interacts with the productDAO to fetch the data.
     *
     * @return a list of all available Product objects in the inventory
     * @throws RemoteException if an error occurs while fetching the products
     */
    @Override
    public List<Product> getAllProducts() throws RemoteException {
        try {
            return productDAO.getAllProducts();
        } catch (Exception e) {
            throw new RemoteException("Error fetching products", e);
        }
    }

    /**
     * Retrieves a list of products belonging to a specific category.
     * This method interacts with the productDAO to fetch the products that match the given category.
     *
     * @param category the name of the category to filter the products by
     * @return a list of Product objects that belong to the specified category
     * @throws RemoteException if an error occurs while fetching the products
     */
    @Override
    public List<Product> getProductsByCategory(String category) throws RemoteException {
        try {
            return productDAO.getProductsByCategory(category);
        } catch (Exception e) {
            throw new RemoteException("Error fetching products by category", e);
        }
    }

    /**
     * Retrieves a list of products from the inventory that match the specified name.
     * This method uses the productDAO to query products based on the given name.
     *
     * @param productName the name, or partial name, of the products to search for
     * @return a list of Product objects that match the specified name
     * @throws RemoteException if an error occurs while fetching the products
     */
    @Override
    public List<Product> getProductsByName(String productName) throws RemoteException {
        try {
            return productDAO.getProductsByName(productName);
        } catch (Exception e) {
            throw new RemoteException("Error fetching products by name", e);
        }
    }

    @Override
    public List<Product> getProductsByQuantity(int quantity) throws RemoteException {
        try {
            return productDAO.getProductsByQuantity(quantity);
        } catch (Exception e) {
            throw new RemoteException("Error fetching products by quantity", e);
        }
    }

    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        try {
            return employeeDAO.getAllEmployees();
        } catch (Exception e) {
            throw new RemoteException("Error fetching employees", e);
        }
    }

    @Override
    public void addProduct(Product product, String username) throws RemoteException {
        try {
            productDAO.addProduct(product);
            Logger.log("ADD_PRODUCT", "Added product: " + product.getName(), username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to add product: " + product.getName(), username);
            throw new RemoteException("Error adding product", e);
        }
    }

    @Override
    public void updateProduct(Product product, String username) throws RemoteException {
        try {
            productDAO.updateProduct(product);
            Logger.log("UPDATE_PRODUCT", "Updated product: " + product.getName(), username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to update product: " + product.getName(), username);
            throw new RemoteException("Error updating product", e);
        }
    }

    @Override
    public void deleteProduct(int productId, String username) throws RemoteException {
        try {
            productDAO.deleteProduct(productId);
            Logger.log("DELETE_PRODUCT", "Deleted product with ID: " + productId, username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to delete product with ID: " + productId, username);
            throw new RemoteException("Error deleting product", e);
        }
    }

    @Override
    public void addEmployee(Employee employee, String username) throws RemoteException {
        try {
            employeeDAO.addEmployee(employee);
            Logger.log("ADD_EMPLOYEE", "Added employee: " + employee.getUsername(), username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to add employee: " + employee.getUsername(), username);
            throw new RemoteException("Error adding employee", e);
        }
    }

    @Override
    public void updateEmployee(Employee employee, boolean updatePassword, String username) throws RemoteException {
        try {
            employeeDAO.updateEmployee(employee, updatePassword);
            Logger.log("UPDATE_EMPLOYEE", "Updated employee: " + employee.getUsername(), username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to update employee: " + employee.getUsername(), username);
            throw new RemoteException("Error updating employee", e);
        }
    }

    @Override
    public void deleteEmployee(int id, String username) throws RemoteException {
        try {
            employeeDAO.deleteEmployee(id);
            Logger.log("DELETE_EMPLOYEE", "Deleted employee with ID: " + id, username);
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to delete employee with ID: " + id, username);
            throw new RemoteException("Error deleting employee", e);
        }
    }
}