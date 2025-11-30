/*
 * HAPA Vehicle Rental System - Database Connection Manager
 * Handles PostgreSQL database connections for the entire application
 * Provides centralized connection management with proper error handling
 * Uses singleton pattern to ensure consistent database connectivity
 */
package dao;

/**
 * DBConnection - Database connection utility class
 * Manages PostgreSQL database connections for the HAPA Vehicle Rental System
 * Provides static method to obtain database connections with error handling
 *
 * @author Pacifique Harerimana
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections
 * Handles PostgreSQL connection setup and error management
 */
public class DBConnection {

    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/hapa_vehicle_rental_system_db";
    private static final String USER = "postgres";  // Database username
    private static final String PASSWORD = "postgres"; // Database password 

    /**
     * Establishes and returns a database connection
     * Loads PostgreSQL JDBC driver and creates connection to the database
     * Includes comprehensive error handling and logging
     * 
     * @return Connection object if successful, null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            System.out.println("Attempting to connect to: " + URL);
            
            // Establish database connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!");
            return conn;

        } catch (ClassNotFoundException ex) {
            // Handle missing JDBC driver
            System.out.println("PostgreSQL JDBC Driver not found!");
            ex.printStackTrace();

        } catch (SQLException ex) {
            // Handle database connection errors
            System.out.println("Connection to PostgreSQL failed!");
            System.out.println("Error: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }

        // Return null if connection failed
        return null;
    }
}

    

