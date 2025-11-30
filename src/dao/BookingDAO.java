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




import model.Booking;
import model.BookingRecord;

import java.util.Date;
import java.util.List;
import model.User;

public interface BookingDAO {
    boolean addBooking(Booking booking);
    boolean bookingExists(int customerId, int vehicleId, Date startDate, Date endDate);

    
    List<BookingRecord> getBookingsByCustomer(int customerId);

    // New advanced methods:
    List<BookingRecord> getFilteredBookings(int customerId, String modelLike, Date dateFrom, Date dateTo, String status);
    List<BookingRecord> getFilteredBookingsPaged(int customerId, String modelLike, Date dateFrom, Date dateTo, String status, int limit, int offset);
    int countFilteredBookings(int customerId, String modelLike, Date dateFrom, Date dateTo, String status);

    boolean cancelBooking(int bookingId);
    boolean reopenBooking(int bookingId, java.util.Date newStart, java.util.Date newEnd);

    void expireOldBookings();

    // In dao.BookingDAO.java (Interface)

    // 1. Checks if the same customer has already booked the exact same vehicle on the exact same dates.
    boolean isDuplicateBooking(int customerId, int vehicleId, java.util.Date startDate, java.util.Date endDate);

    // 2. Checks if the vehicle is unavailable due to ANY other booking with overlapping dates.
    boolean isVehicleUnavailable(int vehicleId, java.util.Date startDate, java.util.Date endDate);

    // Note: You can now remove the old, ambiguous `bookingExists` method from the interface.

    public int countTotalRentals();

    public List<BookingRecord> getPendingBookings();

    public User getCustomerForBooking(int customerId);

    public boolean approveBooking(int id);

    public boolean rejectBooking(int id);
    
    public boolean rejectBookingWithReason(int id, String reason);

    public List<BookingRecord> getActiveRentals();

    public List<Object[]> getMostRentedVehicles();
    
    public List<BookingRecord> getBookingsHistory();
    
    public boolean isVehicleAvailableOn(int vehicleId, Date date);
    
    public List<Object[]> getVehicleAvailabilityReport(Date date);
    
    public List<Object[]> getActiveRentalsReport();
    
    public List<Object[]> getBookingsHistoryReport();
    
    public List<Object[]> getPendingBookingsReport();
    
    public String getNextAvailableDates(int vehicleId, Date requestedStart, Date requestedEnd);
}

