/*
 * HAPA Vehicle Rental System - Vehicle Model Class
 * Represents a vehicle entity in the rental system
 * Contains all vehicle-related information and properties
 * Used for vehicle management, booking, and rental operations
 */
package model;

/**
 * Vehicle - Model class representing a rental vehicle
 * Contains vehicle information including specifications, pricing, and status
 * Supports various vehicle categories with detailed attributes
 *
 * @author Pacifique Harerimana
 */

/**
 * Vehicle entity class representing rental vehicles
 * Stores vehicle details, specifications, pricing, and availability status
 */
public class Vehicle {

    // Vehicle identification
    private int id;                  // Unique vehicle identifier
    private String plateNumber;      // Vehicle license plate number
    
    // Vehicle specifications
    private String model;            // Vehicle model/name
    private String category;         // Vehicle category (SUV, Sedan, etc.)
    private String fuelType;         // Fuel type (Petrol, Diesel, Electric, Hybrid)
    private String transmission;     // Transmission type (Manual, Automatic, CVT)
    private int seats;               // Number of seats
    
    // Pricing and media
    private double pricePerDay;      // Daily rental price
    private String imagePath;        // Path to vehicle image
    
    // System properties
    private String status;           // Vehicle status (Available, Rented, Maintenance)

    
    /**
     * Default constructor
     * Creates an empty Vehicle object
     */
    public Vehicle() {}

    /**
     * Parameterized constructor
     * Creates a Vehicle object with basic information
     * 
     * @param id Vehicle ID
     * @param plateNumber License plate number
     * @param model Vehicle model
     * @param category Vehicle category
     * @param pricePerDay Daily rental price
     * @param imagePath Path to vehicle image
     */
    public Vehicle(int id, String plateNumber, String model, String category, double pricePerDay, String imagePath) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.model = model;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.imagePath = imagePath;
    }

    // Getter and Setter methods for all properties

    /**
     * Gets the vehicle ID
     * @return Vehicle ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the vehicle ID
     * @param id Vehicle ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
    
    /**
     * Gets the vehicle status
     * @return Vehicle status (Available/Rented/Maintenance)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the vehicle status
     * @param status Status to set (Available/Rented/Maintenance)
     */
    public void setStatus(String status) {
        this.status = status;
    }

}

