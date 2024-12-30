package server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * The DatabaseConfig class is responsible for managing the application's database configuration.
 * It uses a singleton design pattern to ensure only a single instance of the configuration exists.
 * The configuration is loaded from one of several predefined file locations, the classpath, or
 * falls back to a default configuration if no file is found.
 */
public class DatabaseConfig {

    /**
     * An array of predefined paths where the application's database configuration
     * properties files may be located. These paths are scanned in sequence to load
     * the relevant database settings into the application.
     *
     * This array includes default locations for property files, allowing flexible
     * configuration placement depending on the deployment environment.
     *
     * The included paths are:
     * - "config/database.properties" : Typically used for configurations within a
     *   project-specific directory.
     * - "database.properties" : A root-level property file in the application's
     *   working directory.
     * - "conf/database.properties" : A commonly used directory structure for
     *   configurations in deployment setups.
     * - "/etc/inventory-app/database.properties" : A standard location for
     *   system-wide configurations on Unix-like operating systems.
     *
     * These locations are processed in the order listed, with the application
     * using the first available file that is found. If none of the files exist
     * or are accessible, the application may fallback to default configuration
     * values or fail to initialize properly, depending on the implementation.
     */
    private static final String[] CONFIG_LOCATIONS = {
            "config/database.properties",
            "database.properties",
            "conf/database.properties",
            "/etc/inventory-app/database.properties"
    };

    private static Properties properties = new Properties();
    private static DatabaseConfig instance;
    private String configurationSource = "default";

    private DatabaseConfig() {
        loadProperties();
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Loads configuration properties for the application. The method attempts to load the properties
     * from external configuration files specified in predefined locations. If none of the external
     * configuration files are found or successfully loaded, it falls back to a bundled properties file
     * resource located in the classpath. If the classpath resource is unavailable, default configuration
     * properties are applied.
     *
     * This method also sets the source of the loaded configuration to a descriptive string, indicating
     * whether the properties were loaded from an external file, the classpath, or defaults.
     *
     * Error handling is included to manage issues during file access or property loading. Messages
     * regarding the success or failure of loading attempts are logged to standard output or error streams.
     *
     * Behavior:
     * - Search for configuration files in predefined external locations in the order specified.
     * - Attempt to load the properties from the first matching external file.
     * - If no external file is found or successfully loaded, attempt to load properties from a
     *   bundled classpath resource named "database.properties".
     * - If the classpath resource is also unavailable, default property values are applied.
     * - Update the configuration source to reflect the origin of the loaded or default configuration.
     */
    private void loadProperties() {
        boolean loaded = false;

        // Try external locations first
        for (String location : CONFIG_LOCATIONS) {
            Path path = Paths.get(location);
            if (Files.exists(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    properties.load(fis);
                    configurationSource = path.toAbsolutePath().toString();
                    System.out.println("Loaded configuration from: " + configurationSource);
                    loaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("Failed to load config from " + path + ": " + e.getMessage());
                }
            }
        }

        // Try classpath if no external config found
        if (!loaded) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
                if (is != null) {
                    properties.load(is);
                    configurationSource = "classpath:database.properties";
                    System.out.println("Loaded configuration from classpath");
                    loaded = true;
                }
            } catch (IOException e) {
                System.err.println("Failed to load config from classpath: " + e.getMessage());
            }
        }

        // Fall back to defaults if no config found
        if (!loaded) {
            setDefaults();
            configurationSource = "default configuration";
            System.out.println("Using default configuration");
        }
    }

    /**
     * Sets default configuration properties for the database connection and related settings.
     * This method initializes the `properties` field with predefined default values that are
     * applied when no external configuration file or classpath resource is loaded.
     *
     * The default configuration includes:
     * - Database connection details (URL, username, password, and driver class).
     * - Connection pool settings (maximum pool size, minimum idle connections, idle timeout,
     *   and connection timeout).
     * - Database initialization flags (whether to create the database or load test data).
     * - RMI port configuration.
     *
     * These defaults ensure the application can operate with a basic setup out of the box,
     * especially in development or testing environments, without requiring external configuration.
     */
    private void setDefaults() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/inventory_db");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.pool.maximumPoolSize", "10");
        properties.setProperty("db.pool.minimumIdle", "5");
        properties.setProperty("db.pool.idleTimeout", "300000");
        properties.setProperty("db.pool.connectionTimeout", "20000");
        properties.setProperty("db.init.createDatabase", "false");
        properties.setProperty("db.init.loadTestData", "false");
        properties.setProperty("rmi.port", "1099");
    }

    // Configuration source
    public String getConfigurationSource() {
        return configurationSource;
    }

    // Database connection properties
    public String getUrl() { return properties.getProperty("db.url"); }
    public String getUsername() { return properties.getProperty("db.username"); }
    public String getPassword() { return properties.getProperty("db.password"); }
    public String getDriver() { return properties.getProperty("db.driver"); }

    // Connection pool properties
    public int getMaximumPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.maximumPoolSize", "10"));
    }
    public int getRmiPort() {
            return Integer.parseInt(properties.getProperty("rmi.port", "1099"));
    }

    public int getMinimumIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.minimumIdle", "5"));
    }

    public long getIdleTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.idleTimeout", "300000"));
    }

    public long getConnectionTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "20000"));
    }

    // Database initialization properties
    public boolean shouldCreateDatabase() {
        return Boolean.parseBoolean(properties.getProperty("db.init.createDatabase", "false"));
    }

    public boolean shouldLoadTestData() {
        return Boolean.parseBoolean(properties.getProperty("db.init.loadTestData", "false"));
    }

    public String getSchemaScript() {
        return properties.getProperty("db.init.schema.path", "schema.sql");
    }

    public String getDataScript() {
        return properties.getProperty("db.init.data.path", "data.sql");
    }

    // Property manipulation
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}