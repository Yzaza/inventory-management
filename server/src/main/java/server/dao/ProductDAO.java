package server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import common.models.Product;
import java.math.BigDecimal;

/**
 * ProductDAO is a data access object responsible for managing CRUD operations
 * on the "products" table within the database. It offers several methods to
 * interact with product records, including fetching, adding, updating, and
 * deleting product data.
 *
 * This class extends BaseDAO, inheriting methods for performing SQL operations
 * such as querying and updating the database. It also includes utility methods
 * to map database results into Product objects.
 *
 * Methods:
 * - getAllProducts: Retrieves all product records from the database.
 * - getProductsByCategory: Retrieves products filtered by a given category.
 * - getProductsByName: Retrieves products filtered by a given name.
 * - getProductsByQuantity: Retrieves products filtered by a specified quantity.
 * - addProduct: Inserts a new product record into the database.
 * - updateProduct: Updates the record of an existing product.
 * - deleteProduct: Removes a product record from the database based on its ID.
 *
 * The class relies on functional interfaces `PreparedStatementSetter` and
 * `ResultSetMapper` (from BaseDAO) for parameter setting and result mapping,
 * respectively.
 */
public class ProductDAO extends BaseDAO {

    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT * FROM products";
        return executeQuery(sql,
                null,
                rs -> {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapResultSetToProduct(rs));
                    }
                    return products;
                }
        );
    }

    public List<Product> getProductsByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM products WHERE category LIKE ?";
        return executeQuery(sql,
                stmt -> stmt.setString(1, "%" + category + "%"),
                rs -> {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapResultSetToProduct(rs));
                    }
                    return products;
                }
        );
    }

    public List<Product> getProductsByName(String name) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        return executeQuery(sql,
                stmt -> stmt.setString(1, "%" + name + "%"),
                rs -> {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapResultSetToProduct(rs));
                    }
                    return products;
                }
        );
    }

    public List<Product> getProductsByQuantity(int quantity) throws SQLException {
        String sql = "SELECT * FROM products WHERE quantity = ?";
        return executeQuery(sql,
                stmt -> stmt.setInt(1, quantity),
                rs -> {
                    List<Product> products = new ArrayList<>();
                    while (rs.next()) {
                        products.add(mapResultSetToProduct(rs));
                    }
                    return products;
                }
        );
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category, quantity, price) VALUES (?, ?, ?, ?)";
        executeUpdate(sql, stmt -> {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getQuantity());
            stmt.setBigDecimal(4, product.getPrice());
        });
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?";
        executeUpdate(sql, stmt -> {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getQuantity());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setInt(5, product.getId());
        });
    }

    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        executeUpdate(sql, stmt -> stmt.setInt(1, id));
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}