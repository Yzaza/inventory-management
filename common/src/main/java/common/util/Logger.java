package common.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Logger class provides functionality to log operations, user actions, and details
 * into a persistent log file while also printing the log entries to the console.
 *
 * This class maintains a log file named "system.log" and uses a specific date-time format
 * ("yyyy-MM-dd HH:mm:ss") for consistent timestamping of log entries.
 *
 * It is primarily designed to be used for tracking user activity and other important events
 * in the application.
 */
public class Logger {
    private static final String LOG_FILE = "system.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String operation, String details, String user) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            LocalDateTime now = LocalDateTime.now();
            String logEntry = String.format("[%s] %s - User: %s - %s",
                    now.format(formatter), operation, user, details);
            writer.println(logEntry);
            System.out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}