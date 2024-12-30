package common.models;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Represents an employee in the system.
 * The Employee class encapsulates information about an individual employee,
 * such as their ID, username, full name, hashed password, role, creation timestamp,
 * and flags indicating whether they have administrative rights or are authenticated.
 *
 * This class implements Serializable to enable its instances to be serialized,
 * allowing for persistence or transfer across systems.
 *
 * The class provides constructors for different use cases and allows the caller
 * to retrieve or modify the attributes through getters and setters.
 */
public class Employee implements Serializable {
    private int id;
    private String username;
    private String fullname;
    private String password; // Hashed password
    private String role;
    private LocalDateTime createdAt;
    private boolean admin = false;

    // Additional fields for authentication-related info
    private boolean authenticated = false;

    public Employee(int id, String username, String fullname, String password, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Employee(String username, String fullname, String password, String role) {
        this(0,username, fullname, password, role, null);
    }

    public Employee(int id,String username, String fullname, String role) {
        this( id,username, fullname,null, role, null);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                ", admin=" + admin +
                ", authenticated=" + authenticated +
                '}';
    }

    public void setFullname(String fullname) {
        this.fullname=fullname;
    }
}
