/*
 * HAPA Vehicle Rental System - Booking Record Model Class
 * Represents a composite object containing both booking and vehicle information
 * Used for displaying complete booking details with associated vehicle data
 * Simplifies data handling in UI components and reporting functions
 */
package model;

/**
 * BookingRecord - Composite model class combining booking and vehicle information
 * Contains both Booking and Vehicle objects for complete booking details
 * Used in UI components to display comprehensive booking information
 *
 * @author Pacifique Harerimana
 */

/**
 * Composite class that combines booking and vehicle information
 * Provides convenient access to both booking details and vehicle specifications
 */
public class BookingRecord {
    // Composite objects
    private Booking booking;    // Booking information (dates, cost, status, etc.)
    private Vehicle vehicle;    // Vehicle information (model, category, specs, etc.)

    /**
     * Default constructor
     * Creates an empty BookingRecord object
     */
    public BookingRecord() {}

    /**
     * Parameterized constructor
     * Creates a BookingRecord with specified booking and vehicle
     * 
     * @param booking Booking object containing booking details
     * @param vehicle Vehicle object containing vehicle information
     */
    public BookingRecord(Booking booking, Vehicle vehicle) {
        this.booking = booking;
        this.vehicle = vehicle;
    }

    /**
     * Gets the booking object
     * @return Booking object containing booking details
     */
    public Booking getBooking() { return booking; }
    
    /**
     * Sets the booking object
     * @param booking Booking object to set
     */
    public void setBooking(Booking booking) { this.booking = booking; }

    /**
     * Gets the vehicle object
     * @return Vehicle object containing vehicle information
     */
    public Vehicle getVehicle() { return vehicle; }
    
    /**
     * Sets the vehicle object
     * @param vehicle Vehicle object to set
     */
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
