package server.util;

import common.util.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.stream.Collectors;



/**
 * A utility class to initialize a database by configuring its schema and loading test data.
 *
 * The {@code DatabaseInitializer} class handles the creation of the database schema
 * and the loading of test data based on the configuration provided by {@code DatabaseConfig}.
 * It uses SQL scripts for schema creation and test data population,
 * which can be loaded either from the classpath or external file paths.
 *
 * The primary responsibilities include:
 * - Checking if the database needs to be created and if test data should be loaded.
 * - Executing the corresponding SQL scripts for schema creation and test data loading.
 * - Logging detailed debug information and any errors encountered during the process.
 */
public class DatabaseInitializer {
    private final DatabaseConfig config;

    public DatabaseInitializer() {
        this.config = DatabaseConfig.getInstance();
    }

    public void initializeDatabase() {

        Logger.log("DEBUG", "Initializing database...", "system");
        if (config.shouldCreateDatabase()) {
            Logger.log("DEBUG", "Creating database schema...", "system");
            createDatabase();
        }else{
            Logger.log("DEBUG", "Skipping Database creation", "system");
        }
        if (config.shouldLoadTestData()) {
            Logger.log("DEBUG", "Loading test data...", "system");
            loadTestData();
        }
        else{
            Logger.log("DEBUG", "Skipping test data loading", "system");
        }
        Logger.log("DEBUG", "Database initialization completed.", "system");
    }

    private void createDatabase() {
        try (Connection conn = ConnectionPool.getDataSource().getConnection()) {
            Logger.log("DEBUG", "Connected to the database via connection pool", "system");



            // Execute the schema.sql script
            String schemaSQL = loadSqlScriptFromClasspath("schema.sql");
            if (schemaSQL != null) {
                Logger.log("DEBUG", "Schema script loaded, executing...", "system");
                try (Statement stmt = conn.createStatement()) {
                    String[] schemaStatements = schemaSQL.split(";"); // Split SQL script into individual statements

                    for (String statement : schemaStatements) {
                        if (!statement.trim().isEmpty()) {// Skip empty statements
                            stmt.execute(statement.trim() + ";"); // Execute each statement
                        }
                    }
                    Logger.log("DATABASE", "Schema created successfully", "system");
                }
            } else {
                Logger.log("ERROR", "Schema script not found", "system");
                throw new RuntimeException("Schema script not found");
            }
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to create database: " + e.getMessage(), "system");
            e.printStackTrace();
            throw new RuntimeException("Failed to create database", e);
        }
    }

    private void loadTestData() {
        try (Connection conn = ConnectionPool.getDataSource().getConnection()) {
            Logger.log("DEBUG", "Connected to the database via connection pool", "system");

            // Execute the data.sql script
            String dataSQL = loadSqlScriptFromClasspath("data.sql");
            if (dataSQL != null) {
                Logger.log("DEBUG", "Data script loaded, executing...", "system");
                try (Statement stmt = conn.createStatement()) {
                    String[] schemaStatements = dataSQL.split(";"); // Split SQL script into individual statements

                    for (String statement : schemaStatements) {
                        if (!statement.trim().isEmpty()) {// Skip empty statements
                            stmt.execute(statement.trim() + ";"); // Execute each statement
                        }
                    }
                    Logger.log("DATABASE", "Test data loaded successfully", "system");
                }
                catch (SQLIntegrityConstraintViolationException E) {
                    Logger.log("ERROR", "Duplicate entry", "system");
                }
            } else {
                Logger.log("ERROR", "Data script not found", "system");
                throw new RuntimeException("Data script not found");
            }
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to load test data: " + e.getMessage(), "system");
            e.printStackTrace();
            throw new RuntimeException("Failed to load test data", e);
        }
    }
    private void executeSqlLineByLine(Connection conn, String sqlScript) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            String[] sqlStatements = sqlScript.split(";");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    try {
                        statement.execute(sql);
                        Logger.log("DEBUG", "Executed: " + sql, "system");
                    } catch (SQLException ex) {
                        Logger.log("ERROR", "Failed to execute: " + sql + ". Error: " + ex.getMessage(), "system");
                        throw ex;
                    }
                }
            }
        }
    }


    /**
     * Loads the specified SQL script file from multiple predefined locations.
     * The method searches for a file with the given filename in several directories
     * and reads its contents if found. These directories include relative paths (such as "config/" or "conf/")
     * and an absolute path "/etc/inventory-app/". If the file is not found in any location, `null` is returned.
     * If an error occurs while reading the file, an error log is generated.
     *
     * @param filename the name of the SQL script file to be loaded. It is expected to be
     *                 the filename only, without directory structure as the method appends
     *                 predefined directories during its search.
     * @return the content of the SQL script file as a String if the file is found and read successfully;
     *         otherwise, returns `null` if the file is not found or an error occurs during the read operation.
     */
    private String loadSqlScript(String filename) {
        String[] locations = {
                "config/" + filename,
                "conf/" + filename,
                filename,
                "/etc/inventory-app/" + filename
        };

        for (String location : locations) {
            Path path = Paths.get(location);
            if (Files.exists(path)) {
                try {
                    return Files.readString(path);
                } catch (Exception e) {
                    Logger.log("ERROR", "Failed to read SQL script from " + path + ": " + e.getMessage(), "system");
                }
            }
        }
        return null;
    }

    /**
     * Loads an SQL script from a specified classpath location or external location.
     * The method first attempts to load the script from a list of external locations
     * using the `loadSqlScript` method. If the script is not found externally, it tries
     * to load it from the application's classpath. If the script cannot be found in either
     * location, the method returns null.
     *
     * @param filename the name of the SQL script file to be loaded
     * @return the content of the SQL script as a String, or null if the script cannot be found
     */
    private String loadSqlScriptFromClasspath(String filename) {
        // First, try to load from external locations
        String externalScript = loadSqlScript(filename);
        if (externalScript != null) {
            return externalScript;
        }

        // If not found externally, fall back to classpath
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
        } catch (Exception e) {
            Logger.log("ERROR", "Failed to read SQL script from classpath: " + e.getMessage(), "system");
        }
        return null;
    }
}