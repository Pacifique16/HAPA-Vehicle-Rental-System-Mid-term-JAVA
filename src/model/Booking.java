/*
 * HAPA Vehicle Rental System - Booking Model Class
 * Represents a vehicle booking/rental transaction in the system
 * Contains all booking-related information including dates, cost, and status
 * Used for managing rental requests, approvals, and tracking
 */
package model;

/**
 * Booking - Model class representing a vehicle rental booking
 * Contains booking information including customer, vehicle, dates, cost, and status
 * Supports various booking statuses and rejection reasons for comprehensive booking management
 *
 * @author Pacifique Harerimana
 */

import java.util.Date;

/**
 * Booking entity class representing vehicle rental bookings
 * Stores booking details, dates, pricing, and status information
 */
public class Booking {
    // Booking identification
    private int id;                    // Unique booking identifier
    
    // Relationship identifiers
    private int customerId;            // ID of the customer making the booking
    private int vehicleId;             // ID of the vehicle being booked
    
    // Booking period
    private Date startDate;            // Rental start date
    private Date endDate;              // Rental end date
    
    // Financial information
    private double totalCost;          // Total cost of the rental
    
    // Status and management
    private String status;             // Booking status (PENDING, APPROVED, REJECTED, CANCELLED, EXPIRED)
    private String rejectionReason;    // Reason for rejection (if applicable)

    /**
     * Default constructor
     * Creates an empty Booking object
     */
    public Booking() {}

    // Getter and Setter methods for all properties
    
    /**
     * Gets the booking ID
     * @return Booking ID
     */
    public int getId() { return id; }
    
    /**
     * Sets the booking ID
     * @param id Booking ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gets the customer ID
     * @return Customer ID who made the booking
     */
    public int getCustomerId() { return customerId; }
    
    /**
     * Sets the customer ID
     * @param customerId Customer ID to set
     */
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    /**
     * Gets the vehicle ID
     * @return Vehicle ID being booked
     */
    public int getVehicleId() { return vehicleId; }
    
    /**
     * Sets the vehicle ID
     * @param vehicleId Vehicle ID to set
     */
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    /**
     * Gets the rental start date
     * @return Start date of the rental
     */
    public Date getStartDate() { return startDate; }
    
    /**
     * Sets the rental start date
     * @param startDate Start date to set
     */
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    /**
     * Gets the rental end date
     * @return End date of the rental
     */
    public Date getEndDate() { return endDate; }
    
    /**
     * Sets the rental end date
     * @param endDate End date to set
     */
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    /**
     * Gets the total cost of the rental
     * @return Total cost amount
     */
    public double getTotalCost() { return totalCost; }
    
    /**
     * Sets the total cost of the rental
     * @param totalCost Total cost to set
     */
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    /**
     * Gets the booking status
     * @return Booking status (PENDING/APPROVED/REJECTED/CANCELLED/EXPIRED)
     */
    public String getStatus() { return status; }
    
    /**
     * Sets the booking status
     * @param status Status to set (PENDING/APPROVED/REJECTED/CANCELLED/EXPIRED)
     */
    public void setStatus(String status) { this.status = status; }
    
    /**
     * Gets the rejection reason (if booking was rejected)
     * @return Rejection reason or null if not rejected
     */
    public String getRejectionReason() { return rejectionReason; }
    
    /**
     * Sets the rejection reason
     * @param rejectionReason Reason for rejection
     */
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
