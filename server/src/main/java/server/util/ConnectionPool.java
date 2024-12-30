package server.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



/**
 * The ConnectionPool class provides centralized management for database connection pooling
 * using the HikariCP library. This class is responsible for initializing, configuring,
 * and handling the lifecycle of the connection pool, ensuring efficient and optimized
 * database connectivity.
 */
public class ConnectionPool {
    private static HikariDataSource dataSource;
    private static final DatabaseConfig config = DatabaseConfig.getInstance();

    static {
        initializeDataSource();
    }

    private static void initializeDataSource() {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setDriverClassName(config.getDriver());

            // Connection pool settings
            hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
            hikariConfig.setMinimumIdle(config.getMinimumIdle());
            hikariConfig.setIdleTimeout(config.getIdleTimeout());
            hikariConfig.setConnectionTimeout(config.getConnectionTimeout());

            // Optional: connection testing
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariConfig.setAutoCommit(true);

            dataSource = new HikariDataSource(hikariConfig);
            System.out.println("Connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Connection pool closed");
        }
    }
}