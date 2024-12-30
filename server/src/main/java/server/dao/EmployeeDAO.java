package server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import common.models.Employee;
import org.mindrot.jbcrypt.BCrypt;

/**
 * EmployeeDAO is a Data Access Object (DAO) class responsible for performing
 * CRUD (Create, Read, Update, Delete) operations and other database-related
 * tasks related to the Employee entity in the system.
 *
 * This class extends BaseDAO to inherit common database operation logic,
 * such as executing queries and updates in the database.
 *
 * The primary responsibilities of this class include:
 * - Retrieving all employees from the database.
 * - Fetching specific employees by their username.
 * - Adding new employees to the database, including encrypting their passwords.
 * - Updating employee information, with or without updating the password.
 * - Deleting employee records from the database.
 * - Authenticating an employee using their login credentials.
 *
 * This class internally uses methods from BaseDAO to streamline database
 * operations and ensure clean separation of concerns. PreparedStatement
 * parameters are set securely to prevent SQL injection. Passwords are
 * encrypted using the BCrypt algorithm for enhanced security.
 */
public class EmployeeDAO extends BaseDAO {

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT * FROM employees";
        return executeQuery(sql,
                null,
                rs -> {
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()) {
                        employees.add(mapResultSetToEmployee(rs));
                    }
                    return employees;
                }
        );
    }

    public Employee getEmployeeByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM employees WHERE username = ?";
        return executeQuery(sql,
                stmt -> stmt.setString(1, username),
                rs -> rs.next() ? mapResultSetToEmployee(rs) : null
        );
    }

    public void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (username, fullname, password, role) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
        executeUpdate(sql, stmt -> {
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getFullname());
            stmt.setString(3, hashedPassword);
            stmt.setString(4, employee.getRole());
        });
    }

    public void updateEmployee(Employee employee, boolean updatePassword) throws SQLException {
        if (updatePassword) {
            String sql = "UPDATE employees SET username = ?, fullname = ?, password = ?, role = ? WHERE id = ?";
            String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
            executeUpdate(sql, stmt -> {
                stmt.setString(1, employee.getUsername());
                stmt.setString(2, employee.getFullname());
                stmt.setString(3, hashedPassword);
                stmt.setString(4, employee.getRole());
                stmt.setInt(5, employee.getId());
            });
        } else {
            String sql = "UPDATE employees SET username = ?, fullname = ?, role = ? WHERE id = ?";
            executeUpdate(sql, stmt -> {
                stmt.setString(1, employee.getUsername());
                stmt.setString(2, employee.getFullname());
                stmt.setString(3, employee.getRole());
                stmt.setInt(4, employee.getId());
            });
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        executeUpdate(sql, stmt -> stmt.setInt(1, id));
    }

    public Employee authenticate(String username, String password) throws SQLException {
        Employee employee = getEmployeeByUsername(username);

        if (employee == null) {
            return null;
        }

        boolean isAuthenticated = BCrypt.checkpw(password, employee.getPassword());
        employee.setAuthenticated(isAuthenticated);

        if (isAuthenticated && "admin".equalsIgnoreCase(employee.getRole())) {
            employee.setAdmin(true);
        }

        return employee;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("fullname"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}