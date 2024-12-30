package server;

import common.interfaces.InventoryInterface;
import common.interfaces.AuthInterface;
import common.util.Logger;

import server.util.ConnectionPool;
import server.util.DatabaseConfig;
import server.util.DatabaseInitializer;
import server.services.InventoryService;
import server.services.AuthService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

/**
 * The Server class initializes and starts an RMI server that hosts multiple services.
 * It performs the following tasks:
 *
 * 1. Loads the database configuration.
 * 2. Initializes the database if necessary.
 * 3. Creates and registers RMI services in the RMI registry.
 * 4. Binds the services to the RMI registry.
 * 5. Manages graceful shutdown of the server by unbinding services
 *    and closing database connection pools.
 *
 * The server also handles startup and shutdown operations to ensure
 * proper resource management in case of errors.
 */
public class Server {
    public static void main(String[] args) {
        try {
            // Load configuration
            DatabaseConfig config = DatabaseConfig.getInstance();
            System.out.println("Using configuration from: " + config.getConfigurationSource());

            // Initialize database if needed
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.initializeDatabase();
            try {
                // Create RMI services
                InventoryInterface inventoryService = new InventoryService();
                AuthInterface authService = new AuthService();
                int port = config.getRmiPort();
                // Start the RMI registry
                Registry registry = LocateRegistry.createRegistry(port);
                Logger.log("SERVER", "RMI Registry started", "system");

                // Bind services to the registry
                registry.rebind("InventoryService", inventoryService);
                registry.rebind("AuthService", authService);
                Logger.log("SERVER", "Services bound. Server is ready.", "system");

                System.out.println("Server is running on port: " + port + "....");
                // Add shutdown hook
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        // Unbind services
                        registry.unbind("InventoryService");
                        registry.unbind("AuthService");
                        Logger.log("SERVER", "Services unbound", "system");

                        // Close connection pool
                        ConnectionPool.closePool();
                        Logger.log("SERVER", "Server shutdown completed", "system");
                    } catch (Exception e) {
                        Logger.log("ERROR", "Error during shutdown: " + e.getMessage(), "system");
                    }
                }));
            }catch (ExportException e){
                Logger.log("ERROR", "Failed to start RMI registry -- Port already in use: "+config.getRmiPort(), "system");
                System.exit(1);
            }

        } catch (Exception e) {
            Logger.log("ERROR", "Server failed to start: " + e.getMessage(), "system");
            e.printStackTrace();
            // Ensure connection pool is closed even if server fails to start
            ConnectionPool.closePool();
            System.exit(1);
        }
    }
}