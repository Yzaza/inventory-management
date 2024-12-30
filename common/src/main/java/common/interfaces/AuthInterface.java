package common.interfaces;

import common.models.Employee;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * The AuthInterface defines the remote methods required for handling user authentication
 * in a distributed system. Implementations of this interface are responsible for verifying
 * user credentials and returning the relevant Employee object upon successful authentication.
 *
 * This interface extends the Remote interface, indicating compliance with RMI requirements
 * for supporting remote method invocation.
 */
public interface AuthInterface extends Remote {
    Employee authenticate(String username, String password) throws RemoteException;
    // Additional methods if required
}
