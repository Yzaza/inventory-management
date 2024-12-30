package server.dao;

import server.util.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BaseDAO is an abstract class that provides utility methods for executing
 * common database operations such as queries and updates. Its purpose is to
 * simplify database interaction and enforce reusable patterns for data access.
 *
 * Main Features:
 * - Simplified management of database connections using a connection pool.
 * - Encapsulation of common operations (query and update) with predefined structures.
 * - Support for functional interfaces to parameterize database operations.
 *
 * Methods:
 * - `executeOperation`: Executes a custom database operation with a managed connection.
 * - `executeQuery`: Executes a SQL query and maps the result set to a desired data structure.
 * - `executeUpdate`: Executes SQL update operations such as INSERT, UPDATE, or DELETE.
 *
 * Functional Interfaces:
 * - `DatabaseOperation<T>`: Represents a database operation to be executed.
 * - `PreparedStatementSetter`: Allows setting parameters on a prepared statement.
 * - `ResultSetMapper<T>`: Maps the processed result set into a specific object or data type.
 *
 * Usage:
 * - Extend this class and use the provided methods to perform database operations.
 * - Define specific DAO classes for business objects, implementing mapping operations as needed.
 *
 * Error Handling:
 * - Catches and logs `SQLException` during execution to help with debugging database-related issues.
 * - Rethrows exceptions to notify the caller in case of failure.
 */
public abstract class BaseDAO {

    protected <T> T executeOperation(DatabaseOperation<T> operation) throws SQLException {
        try (Connection connection = ConnectionPool.getDataSource().getConnection()) {
            return operation.execute(connection);
        } catch (SQLException e) {
            System.err.println("Database operation failed: " + e.getMessage());
            throw e;
        }
    }

    protected <T> T executeQuery(String sql, PreparedStatementSetter paramSetter, ResultSetMapper<T> resultMapper)
            throws SQLException {
        return executeOperation(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (paramSetter != null) {
                    paramSetter.setParameters(stmt);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultMapper.mapResult(rs);
                }
            }
        });
    }

    protected int executeUpdate(String sql, PreparedStatementSetter paramSetter) throws SQLException {
        return executeOperation(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (paramSetter != null) {
                    paramSetter.setParameters(stmt);
                }
                return stmt.executeUpdate();
            }
        });
    }

    @FunctionalInterface
    protected interface DatabaseOperation<T> {
        T execute(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T mapResult(ResultSet rs) throws SQLException;
    }
}