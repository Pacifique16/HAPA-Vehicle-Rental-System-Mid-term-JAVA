/*
 * HAPA Vehicle Rental System - Vehicle Data Access Object Interface
 * Defines all database operations related to vehicle management
 * Provides comprehensive vehicle management functionality including CRUD operations,
 * search capabilities, availability checking, and status management
 */
package dao;

/**
 * VehicleDAO - Data Access Object interface for vehicle operations
 * Defines contract for all vehicle-related database operations
 * Supports vehicle management, search, availability checking, and status updates
 *
 * @author Pacifique Harerimana
 */

import java.util.List;
import model.Vehicle;

/**
 * Interface defining all vehicle-related database operations
 * Provides methods for vehicle management, search, and availability operations
 */
public interface VehicleDAO {
    /**
     * Retrieves all vehicles from the database
     * @return List of all vehicles
     */
    List<Vehicle> getAllVehicles();
    
    /**
     * Counts total number of vehicles in the system
     * @return Total count of vehicles
     */
    public int countVehicles();
    
    /**
     * Counts number of vehicles available today
     * @return Count of available vehicles
     */
    public int countAvailableToday();
    
    /**
     * Searches vehicles based on query string
     * @param query Search term for vehicle model, plate, etc.
     * @return List of vehicles matching the search criteria
     */
    public List<Vehicle> searchVehicles(String query);
    
    /**
     * Finds vehicle by its ID
     * @param id Vehicle ID to search for
     * @return Vehicle object if found, null otherwise
     */
    public Vehicle findById(int id);
    
    /**
     * Deletes a vehicle from the database
     * @param id ID of the vehicle to delete
     * @return true if vehicle was successfully deleted, false otherwise
     */
    public boolean deleteVehicle(int id);
    
    /**
     * Adds a new vehicle to the database
     * @param vehicle Vehicle object to be added
     * @return true if vehicle was successfully added, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle);
    
    /**
     * Updates existing vehicle information
     * @param vehicle Vehicle object with updated information
     * @return true if vehicle was successfully updated, false otherwise
     */
    public boolean updateVehicle(Vehicle vehicle);
    
    /**
     * Updates vehicle status (Available, Rented, Maintenance)
     * @param vehicleId ID of the vehicle
     * @param status New status to set
     * @return true if status was successfully updated, false otherwise
     */
    public boolean updateVehicleStatus(int vehicleId, String status);
    
    /**
     * Checks if vehicle is available for booking
     * @param vehicleId ID of the vehicle to check
     * @return true if vehicle is available for booking, false otherwise
     */
    public boolean isVehicleAvailableForBooking(int vehicleId);
}

