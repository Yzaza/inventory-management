package server.services;

import common.interfaces.AuthInterface;
import common.util.Logger;
import server.dao.EmployeeDAO;
import common.models.Employee;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



/**
 * AuthService is a secured, RMI-enabled implementation of the AuthInterface.
 * It is responsible for handling authentication operations in the system.
 * This class interacts with the EmployeeDAO for user data retrieval and validation.
 * Additionally, it logs authentication successes, failures, and error events.
 *
 * AuthService relies on the following classes and external functionalities:
 * - EmployeeDAO for database access and user authentication logic.
 * - Employee for representing user data and state.
 * - Logger for logging authentication-related operations.
 * - BCrypt for password hashing and validation.
 *
 * Extends: UnicastRemoteObject to enable RMI functionality.
 * Implements: AuthInterface for remote authentication capability.
 */
public class AuthService extends UnicastRemoteObject implements AuthInterface {
    private final EmployeeDAO employeeDAO;

    public AuthService() throws RemoteException {
        super();
        employeeDAO = new EmployeeDAO();
    }



    /**
     * Authenticates an employee based on the provided username and password.
     * This method verifies the credentials using the EmployeeDAO and logs the outcome
     * of the authentication attempt, whether successful, failed, or for a non-existent user.
     *
     * @param username the username of the employee attempting to authenticate
     * @param password the password provided by the employee for authentication
     * @return an Employee object if authentication is successful, or null if the credentials are invalid or the user does not exist
     * @throws RemoteException if an error occurs during the authentication process
     */
    @Override
    public Employee authenticate(String username, String password) throws RemoteException {
        try {
            Employee employee = employeeDAO.authenticate(username, password);
            if (employee != null) {
                if (employee.isAuthenticated()) {
                    Logger.log("AUTH", "Successful authentication for user: " + username, "system");
                } else {
                    Logger.log("AUTH", "Failed authentication attempt for user: " + username, "system");
                }
            } else {
                Logger.log("AUTH", "Authentication attempt for non-existent user: " + username, "system");
            }
            return employee;
        } catch (Exception e) {
            Logger.log("ERROR", "Authentication error for user " + username + ": " + e.getMessage(), "system");
            throw new RemoteException("Error authenticating user", e);
        }
    }
}