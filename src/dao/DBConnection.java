/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Pacifique Harerimana
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/hapa_vehicle_rental_system_db";
    private static final String USER = "postgres";  
    private static final String PASSWORD = "postgres"; 

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Attempting to connect to: " + URL);
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!");
            return conn;

        } catch (ClassNotFoundException ex) {
            System.out.println("PostgreSQL JDBC Driver not found!");
            ex.printStackTrace();

        } catch (SQLException ex) {
            System.out.println("Connection to PostgreSQL failed!");
            System.out.println("Error: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }

        return null;
    }
}

    

