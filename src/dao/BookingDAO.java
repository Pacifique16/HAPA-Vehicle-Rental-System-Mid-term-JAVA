/*
 * HAPA Vehicle Rental System - Booking Data Access Object Interface
 * Defines all database operations related to vehicle bookings
 * Provides comprehensive booking management functionality including CRUD operations,
 * filtering, pagination, reporting, and availability checking
 */
package dao;

/**
 * BookingDAO - Data Access Object interface for booking operations
 * Defines contract for all booking-related database operations
 * Supports advanced features like filtering, pagination, reporting, and availability management
 *
 * @author Pacifique Harerimana
 */

import model.Booking;
import model.BookingRecord;

import java.util.Date;
import java.util.List;
import model.User;

/**
 * Interface defining all booking-related database operations
 * Provides methods for booking management, filtering, reporting, and availability checking
 */
public interface BookingDAO {
    /**
     * Adds a new booking to the database
     * @param booking The booking object to be saved
     * @return true if booking was successfully added, false otherwise
     */
    boolean addBooking(Booking booking);
    
    /**
     * Checks if a booking exists for given parameters (legacy method)
     * @param customerId ID of the customer
     * @param vehicleId ID of the vehicle
     * @param startDate Start date of the booking
     * @param endDate End date of the booking
     * @return true if booking exists, false otherwise
     */
    boolean bookingExists(int customerId, int vehicleId, Date startDate, Date endDate);

    
    /**
     * Retrieves all bookings for a specific customer
     * @param customerId ID of the customer
     * @return List of booking records for the customer
     */
    List<BookingRecord> getBookingsByCustomer(int customerId);

    // Advanced filtering and pagination methods
    /**
     * Retrieves filtered bookings based on multiple criteria
     * @param customerId ID of the customer
     * @param modelLike Vehicle model search term
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @param status Booking status filter
     * @return List of filtered booking records
     */
    List<BookingRecord> getFilteredBookings(int customerId, String modelLike, Date dateFrom, Date dateTo, String status);
    
    /**
     * Retrieves filtered bookings with pagination support
     * @param customerId ID of the customer
     * @param modelLike Vehicle model search term
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @param status Booking status filter
     * @param limit Maximum number of records to return
     * @param offset Number of records to skip
     * @return List of filtered booking records with pagination
     */
    List<BookingRecord> getFilteredBookingsPaged(int customerId, String modelLike, Date dateFrom, Date dateTo, String status, int limit, int offset);
    
    /**
     * Counts total number of filtered bookings
     * @param customerId ID of the customer
     * @param modelLike Vehicle model search term
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @param status Booking status filter
     * @return Total count of matching bookings
     */
    int countFilteredBookings(int customerId, String modelLike, Date dateFrom, Date dateTo, String status);

    /**
     * Cancels an existing booking
     * @param bookingId ID of the booking to cancel
     * @return true if booking was successfully cancelled, false otherwise
     */
    boolean cancelBooking(int bookingId);
    
    /**
     * Reopens a cancelled booking with new dates
     * @param bookingId ID of the booking to reopen
     * @param newStart New start date for the booking
     * @param newEnd New end date for the booking
     * @return true if booking was successfully reopened, false otherwise
     */
    boolean reopenBooking(int bookingId, java.util.Date newStart, java.util.Date newEnd);

    /**
     * Automatically expires bookings that have passed their end date
     * Updates booking status to EXPIRED for bookings with end_date < current_date
     */
    void expireOldBookings();

    // Booking validation methods
    
    /**
     * Checks if the same customer has already booked the exact same vehicle on the exact same dates
     * Prevents duplicate bookings by the same customer
     * @param customerId ID of the customer
     * @param vehicleId ID of the vehicle
     * @param startDate Start date of the booking
     * @param endDate End date of the booking
     * @return true if duplicate booking exists, false otherwise
     */
    boolean isDuplicateBooking(int customerId, int vehicleId, java.util.Date startDate, java.util.Date endDate);

    /**
     * Checks if the vehicle is unavailable due to ANY other booking with overlapping dates
     * Prevents double-booking of vehicles
     * @param vehicleId ID of the vehicle
     * @param startDate Start date of the requested booking
     * @param endDate End date of the requested booking
     * @return true if vehicle is unavailable, false if available
     */
    boolean isVehicleUnavailable(int vehicleId, java.util.Date startDate, java.util.Date endDate);

    /**
     * Counts total number of rentals (excluding rejected bookings)
     * @return Total count of rentals
     */
    public int countTotalRentals();

    /**
     * Retrieves all pending bookings awaiting approval
     * @return List of pending booking records
     */
    public List<BookingRecord> getPendingBookings();

    /**
     * Retrieves customer information for a booking
     * @param customerId ID of the customer
     * @return User object containing customer details
     */
    public User getCustomerForBooking(int customerId);

    /**
     * Approves a pending booking
     * @param id ID of the booking to approve
     * @return true if booking was successfully approved, false otherwise
     */
    public boolean approveBooking(int id);

    /**
     * Rejects a pending booking without reason
     * @param id ID of the booking to reject
     * @return true if booking was successfully rejected, false otherwise
     */
    public boolean rejectBooking(int id);
    
    /**
     * Rejects a pending booking with a specific reason
     * @param id ID of the booking to reject
     * @param reason Reason for rejection
     * @return true if booking was successfully rejected, false otherwise
     */
    public boolean rejectBookingWithReason(int id, String reason);

    // Reporting and analytics methods
    
    /**
     * Retrieves all currently active rental bookings
     * @return List of active booking records
     */
    public List<BookingRecord> getActiveRentals();

    /**
     * Retrieves statistics of most rented vehicles
     * @return Array of objects containing vehicle model, rental count, and total income
     */
    public List<Object[]> getMostRentedVehicles();
    
    /**
     * Retrieves historical booking records
     * @return List of completed booking records
     */
    public List<BookingRecord> getBookingsHistory();
    
    /**
     * Checks if a vehicle is available on a specific date
     * @param vehicleId ID of the vehicle
     * @param date Date to check availability
     * @return true if vehicle is available, false otherwise
     */
    public boolean isVehicleAvailableOn(int vehicleId, Date date);
    
    /**
     * Generates vehicle availability report for a specific date
     * @param date Date for availability check
     * @return Array of objects containing vehicle details and availability status
     */
    public List<Object[]> getVehicleAvailabilityReport(Date date);
    
    /**
     * Generates active rentals report for admin dashboard
     * @return Array of objects containing active rental details
     */
    public List<Object[]> getActiveRentalsReport();
    
    /**
     * Generates bookings history report for admin dashboard
     * @return Array of objects containing historical booking details
     */
    public List<Object[]> getBookingsHistoryReport();
    
    /**
     * Generates pending bookings report for admin approval
     * @return Array of objects containing pending booking details
     */
    public List<Object[]> getPendingBookingsReport();
    
    /**
     * Gets next available dates for a vehicle when requested dates are unavailable
     * @param vehicleId ID of the vehicle
     * @param requestedStart Requested start date
     * @param requestedEnd Requested end date
     * @return String message indicating when vehicle will be available
     */
    public String getNextAvailableDates(int vehicleId, Date requestedStart, Date requestedEnd);
}

