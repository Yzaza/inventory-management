package common.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;



/**
 * Represents a product in the inventory system.
 * This class encapsulates properties of a product such as its ID, name, category, quantity,
 * price, and the timestamp of when it was created.
 * It also provides methods to retrieve and modify these attributes.
 *
 * This class implements Serializable to allow instances of the Product class
 * to be converted into a byte stream, enabling their persistence or transfer across systems.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L; // Add a serial version UID

    private int id;
    private String name;
    private String category;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;

    public Product(int id, String name, String category, int quantity, BigDecimal price, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
    }

    public Product(String name, String category, int quantity, BigDecimal price) {
        this(0, name, category, quantity, price, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
