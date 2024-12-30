package common.interfaces;

import common.models.Employee;
import common.models.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The InventoryInterface defines the remote methods for managing inventory, including
 * operations for handling products and employees within the system. Implementers of
 * this interface must provide mechanisms for remote invocation in a distributed environment.
 *
 * This interface extends the Remote interface, indicating that its methods
 * will be accessible from remote clients.
 */
public interface InventoryInterface extends Remote {
    List<Product> getAllProducts() throws RemoteException;
    List<Product> getProductsByCategory(String category) throws RemoteException;
    List<Product> getProductsByName(String productName) throws RemoteException;
    List<Product> getProductsByQuantity(int quantity) throws RemoteException;
    List<Employee> getAllEmployees() throws RemoteException;
    void addProduct(Product product, String username) throws RemoteException;
    void updateProduct(Product product, String username) throws RemoteException;
    void deleteProduct(int productId, String username) throws RemoteException;
    void addEmployee(Employee employee, String username) throws RemoteException;
    void updateEmployee(Employee employee, boolean updatePassword, String username) throws RemoteException;
    void deleteEmployee(int id, String username) throws RemoteException;
}