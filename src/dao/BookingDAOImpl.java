/*
 * HAPA Vehicle Rental System - Booking Data Access Object Implementation
 * Implements all database operations related to booking management
 * Provides comprehensive booking functionality including CRUD operations,
 * filtering, pagination, reporting, validation, and availability management
 */
package dao;

/**
 * BookingDAOImpl - Implementation of BookingDAO interface
 * Provides concrete database operations for comprehensive booking management
 * Handles booking CRUD operations, filtering, validation, reporting, and analytics
 *
 * @author Pacifique Harerimana
 */

import model.Booking;
import model.BookingRecord;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import model.User;

/**
 * Implementation class for booking database operations
 * Provides concrete methods for booking management, validation, and reporting
 */
public class BookingDAOImpl implements BookingDAO {

    /**
     * Adds a new booking to the database
     * Creates a new booking record with all necessary details
     * 
     * @param b Booking object containing booking information
     * @return true if booking was successfully added, false otherwise
     */
    @Override
    public boolean addBooking(Booking b) {
        String sql = "INSERT INTO bookings (customer_id, vehicle_id, start_date, end_date, total_cost, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Set all booking parameters
            pst.setInt(1, b.getCustomerId());
            pst.setInt(2, b.getVehicleId());
            pst.setDate(3, new java.sql.Date(b.getStartDate().getTime()));
            pst.setDate(4, new java.sql.Date(b.getEndDate().getTime()));
            pst.setDouble(5, b.getTotalCost());
            pst.setString(6, b.getStatus());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all bookings for a specific customer (legacy method)
     * Delegates to filtered bookings method for compatibility
     * 
     * @param customerId ID of the customer
     * @return List of booking records for the customer
     */
    @Override
    public List<BookingRecord> getBookingsByCustomer(int customerId) {
        return getFilteredBookings(customerId, null, null, null, null);
    }

    /**
     * Retrieves filtered bookings without pagination
     * Delegates to paginated method with maximum limit
     * 
     * @param customerId ID of the customer
     * @param modelLike Vehicle model search term
     * @param dateFrom Start date filter
     * @param dateTo End date filter
     * @param status Booking status filter
     * @return List of filtered booking records
     */
    @Override
    public List<BookingRecord> getFilteredBookings(int customerId, String modelLike, java.util.Date dateFrom, java.util.Date dateTo, String status) {
        return getFilteredBookingsPaged(customerId, modelLike, dateFrom, dateTo, status, Integer.MAX_VALUE, 0);
    }

    @Override
    public List<BookingRecord> getFilteredBookingsPaged(int customerId, String modelLike,
                                                        java.util.Date dateFrom, java.util.Date dateTo, String status,
                                                        int limit, int offset) {
        List<BookingRecord> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.id AS b_id, b.customer_id, b.vehicle_id, b.start_date, b.end_date, b.total_cost, b.status, b.rejection_reason, ")
           .append("v.id AS v_id, v.plate_number, v.model, v.category, v.price_per_day, v.image_path, v.fuel_type, v.transmission, v.seats ")
           .append("FROM bookings b JOIN vehicles v ON b.vehicle_id = v.id WHERE b.customer_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(customerId);

        if (modelLike != null && !modelLike.trim().isEmpty()) {
            sql.append(" AND lower(v.model) LIKE ? ");
            params.add("%" + modelLike.trim().toLowerCase() + "%");
        }

        if (dateFrom != null) {
            sql.append(" AND b.start_date >= ? ");
            params.add(new java.sql.Date(dateFrom.getTime()));
        }
        if (dateTo != null) {
            sql.append(" AND b.end_date <= ? ");
            params.add(new java.sql.Date(dateTo.getTime()));
        }

        if ("EXPIRED".equalsIgnoreCase(status)) {
            // Force only EXPIRED
            sql.append(" AND b.status = 'EXPIRED' ");
        } else if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND b.status = ? ");
            params.add(status);
        }


        sql.append(" ORDER BY b.start_date DESC ");

        // Add pagination
        if (limit != Integer.MAX_VALUE) {
            sql.append(" LIMIT ? OFFSET ? ");
            params.add(limit);
            params.add(offset);
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof java.sql.Date) pst.setDate(i+1, (java.sql.Date)p);
                else if (p instanceof Integer) pst.setInt(i+1, (Integer)p);
                else if (p instanceof Long) pst.setLong(i+1, (Long)p);
                else pst.setObject(i+1, p);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getInt("b_id"));
                    b.setCustomerId(rs.getInt("customer_id"));
                    b.setVehicleId(rs.getInt("vehicle_id"));
                    b.setStartDate(rs.getDate("start_date"));
                    b.setEndDate(rs.getDate("end_date"));
                    b.setTotalCost(rs.getDouble("total_cost"));
                    b.setStatus(rs.getString("status"));
                    b.setRejectionReason(rs.getString("rejection_reason"));

                    Vehicle v = new Vehicle();
                    v.setId(rs.getInt("v_id"));
                    v.setPlateNumber(rs.getString("plate_number"));
                    v.setModel(rs.getString("model"));
                    v.setCategory(rs.getString("category"));
                    v.setPricePerDay(rs.getDouble("price_per_day"));
                    v.setImagePath(rs.getString("image_path"));
                    v.setFuelType(rs.getString("fuel_type"));
                    v.setTransmission(rs.getString("transmission"));
                    v.setSeats(rs.getInt("seats"));

                    list.add(new BookingRecord(b, v));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    @Override
    public boolean bookingExists(int customerId, int vehicleId, Date startDate, Date endDate) {

        String sql =
            "SELECT COUNT(*) FROM bookings " +
            "WHERE vehicle_id = ? " +
            "AND status != 'REJECTED' " +
            "AND (" +
            "   (start_date <= ? AND end_date >= ?)" +     // new start inside old booking
            "   OR (start_date <= ? AND end_date >= ?)" +  // new end inside old booking
            "   OR (? <= start_date AND ? >= end_date)" +  // new range covers old range
            ")";


        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, vehicleId);

        pst.setDate(2, new java.sql.Date(startDate.getTime()));
        pst.setDate(3, new java.sql.Date(startDate.getTime()));

        pst.setDate(4, new java.sql.Date(endDate.getTime()));
        pst.setDate(5, new java.sql.Date(endDate.getTime()));

        pst.setDate(6, new java.sql.Date(startDate.getTime()));
        pst.setDate(7, new java.sql.Date(endDate.getTime()));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    

    @Override
    public int countFilteredBookings(int customerId, String modelLike, java.util.Date dateFrom, java.util.Date dateTo, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM bookings b JOIN vehicles v ON b.vehicle_id = v.id WHERE b.customer_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(customerId);

        if (modelLike != null && !modelLike.trim().isEmpty()) {
            sql.append(" AND lower(v.model) LIKE ? ");
            params.add("%" + modelLike.trim().toLowerCase() + "%");
        }
        if (dateFrom != null) {
            sql.append(" AND b.start_date >= ? ");
            params.add(new java.sql.Date(dateFrom.getTime()));
        }
        if (dateTo != null) {
            sql.append(" AND b.end_date <= ? ");
            params.add(new java.sql.Date(dateTo.getTime()));
        }
        if ("EXPIRED".equalsIgnoreCase(status)) {
            // Force only EXPIRED
            sql.append(" AND b.status = 'EXPIRED' ");
        } else if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND b.status = ? ");
            params.add(status);
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof java.sql.Date) pst.setDate(i+1, (java.sql.Date)p);
                else pst.setObject(i+1, p);
            }

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "CANCELLED");
            pst.setInt(2, bookingId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reopenBooking(int bookingId, java.util.Date newStart, java.util.Date newEnd) {
        String sql = "UPDATE bookings SET start_date = ?, end_date = ?, status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setDate(1, new java.sql.Date(newStart.getTime()));
            pst.setDate(2, new java.sql.Date(newEnd.getTime()));
            pst.setString(3, "PENDING");
            pst.setInt(4, bookingId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isDuplicateBooking(int customerId, int vehicleId, Date startDate, Date endDate) {

        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE customer_id = ? AND vehicle_id = ? "
                   + "AND start_date = ? AND end_date = ? "
                   + "AND status NOT IN ('REJECTED', 'CANCELLED')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            pst.setInt(2, vehicleId);
            pst.setDate(3, new java.sql.Date(startDate.getTime()));
            pst.setDate(4, new java.sql.Date(endDate.getTime()));

            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Automatically expires bookings that have passed their end date
     * Updates booking status to EXPIRED for bookings with end_date < current_date
     * Excludes already cancelled, rejected, or expired bookings
     */
    public void expireOldBookings() {
        String sql = "UPDATE bookings " +
                     "SET status = 'EXPIRED' " +
                     "WHERE end_date < CURRENT_DATE " +
                     "AND status NOT IN ('CANCELLED', 'REJECTED', 'EXPIRED')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    

    @Override
    public boolean isVehicleUnavailable(int vehicleId, Date startDate, Date endDate) {

        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE vehicle_id = ? "
                   + "AND status NOT IN ('REJECTED', 'CANCELLED') "
                   + "AND (start_date <= ? AND end_date >= ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, vehicleId);
            pst.setDate(2, new java.sql.Date(endDate.getTime()));     // existing.start <= new.end
            pst.setDate(3, new java.sql.Date(startDate.getTime()));   // existing.end >= new.start

            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Counts total number of rentals (excluding rejected bookings)
     * Used for dashboard statistics and reporting
     * 
     * @return Total count of rentals
     */
    @Override
    public int countTotalRentals() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status != 'REJECTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Object[]> getBookingHistory() {
        List<Object[]> list = new ArrayList<>();

        String sql =
            "SELECT u.full_name, v.model, b.start_date, b.end_date, " +
            "b.total_cost, b.status " +
            "FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "JOIN vehicles v ON b.vehicle_id = v.id " +
            "WHERE b.end_date < CURRENT_DATE " +
            "ORDER BY b.end_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[] {
                    rs.getString("full_name"),
                    rs.getString("model"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getDouble("total_cost"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public User getCustomerForBooking(int customerId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean approveBooking(int id) {
        String sql = "UPDATE bookings SET status = 'APPROVED' WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rejectBooking(int id) {
        return rejectBookingWithReason(id, null);
    }
    
    @Override
    public boolean rejectBookingWithReason(int id, String reason) {
        String sql = "UPDATE bookings SET status = 'REJECTED', rejection_reason = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, reason);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Object[]> getActiveRentalsReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT u.full_name, v.model, b.start_date, b.end_date, b.total_cost, b.status " +
                     "FROM bookings b " +
                     "JOIN vehicles v ON b.vehicle_id = v.id " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "WHERE b.status = 'APPROVED' AND b.start_date <= CURRENT_DATE AND b.end_date >= CURRENT_DATE " +
                     "ORDER BY b.start_date ASC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Object[] {
                    rs.getString("full_name"),
                    rs.getString("model"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    String.format("%,.0f RWF", rs.getDouble("total_cost")),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<BookingRecord> getActiveRentals() {
        List<BookingRecord> list = new ArrayList<>();
        String sql = "SELECT b.id AS b_id, b.customer_id, b.vehicle_id, b.start_date, b.end_date, b.total_cost, b.status, " +
                     "v.id AS v_id, v.plate_number, v.model, v.category, v.price_per_day, v.image_path, v.fuel_type, v.transmission, v.seats " +
                     "FROM bookings b JOIN vehicles v ON b.vehicle_id = v.id " +
                     "WHERE b.status = 'APPROVED' AND b.start_date <= CURRENT_DATE AND b.end_date >= CURRENT_DATE " +
                     "ORDER BY b.start_date ASC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("b_id"));
                b.setCustomerId(rs.getInt("customer_id"));
                b.setVehicleId(rs.getInt("vehicle_id"));
                b.setStartDate(rs.getDate("start_date"));
                b.setEndDate(rs.getDate("end_date"));
                b.setTotalCost(rs.getDouble("total_cost"));
                b.setStatus(rs.getString("status"));
                
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("v_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                
                list.add(new BookingRecord(b, v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<Object[]> getBookingsHistoryReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT u.full_name, v.model, b.start_date, b.end_date, b.total_cost, b.status " +
                     "FROM bookings b " +
                     "JOIN vehicles v ON b.vehicle_id = v.id " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "WHERE b.status IN ('APPROVED', 'EXPIRED', 'CANCELLED') " +
                     "ORDER BY b.end_date DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Object[] {
                    rs.getString("full_name"),
                    rs.getString("model"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    String.format("%,.0f RWF", rs.getDouble("total_cost")),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<BookingRecord> getBookingsHistory() {
        List<BookingRecord> list = new ArrayList<>();
        String sql = "SELECT b.id AS b_id, b.customer_id, b.vehicle_id, b.start_date, b.end_date, b.total_cost, b.status, " +
                     "v.id AS v_id, v.plate_number, v.model, v.category, v.price_per_day, v.image_path, v.fuel_type, v.transmission, v.seats " +
                     "FROM bookings b JOIN vehicles v ON b.vehicle_id = v.id " +
                     "WHERE b.status IN ('APPROVED', 'EXPIRED', 'CANCELLED') " +
                     "ORDER BY b.end_date DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("b_id"));
                b.setCustomerId(rs.getInt("customer_id"));
                b.setVehicleId(rs.getInt("vehicle_id"));
                b.setStartDate(rs.getDate("start_date"));
                b.setEndDate(rs.getDate("end_date"));
                b.setTotalCost(rs.getDouble("total_cost"));
                b.setStatus(rs.getString("status"));
                
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("v_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                
                list.add(new BookingRecord(b, v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public boolean isVehicleAvailableOn(int vehicleId, Date date) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE vehicle_id = ? AND status NOT IN ('CANCELLED', 'REJECTED') AND ? BETWEEN start_date AND end_date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, vehicleId);
            pst.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // available if no bookings found
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Object[]> getMostRentedVehicles() {
        List<Object[]> list = new ArrayList<>();

        String sql =
            "SELECT v.model, " +
            "COUNT(CASE WHEN b.status IN ('APPROVED', 'EXPIRED') THEN 1 END) AS times_rented, " +
            "COALESCE(SUM(CASE WHEN b.status IN ('APPROVED', 'EXPIRED') THEN b.total_cost END), 0) AS total_income " +
            "FROM vehicles v " +
            "LEFT JOIN bookings b ON v.id = b.vehicle_id " +
            "GROUP BY v.model " +
            "ORDER BY times_rented DESC, total_income DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[] {
                    rs.getString("model"),
                    rs.getInt("times_rented"),
                    rs.getDouble("total_income")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }





    @Override
    public List<Object[]> getVehicleAvailabilityReport(Date date) {
        List<Object[]> list = new ArrayList<>();
        
        String sql = "SELECT v.model, v.category, v.price_per_day, " +
                     "CASE WHEN v.status = 'Maintenance' THEN 'No' " +
                     "     WHEN EXISTS (SELECT 1 FROM bookings b WHERE b.vehicle_id = v.id " +
                     "                  AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
                     "                  AND ? BETWEEN b.start_date AND b.end_date) THEN 'No' " +
                     "     ELSE 'Yes' END AS available " +
                     "FROM vehicles v ORDER BY v.model";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                        rs.getString("model"),
                        rs.getString("category"),
                        String.format("%,.0f RWF", rs.getDouble("price_per_day")),
                        rs.getString("available")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    @Override
    public List<Object[]> getPendingBookingsReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT b.id, u.full_name, u.phone, v.model, v.plate_number, " +
                     "b.start_date, b.end_date, b.total_cost, b.status " +
                     "FROM bookings b " +
                     "JOIN vehicles v ON b.vehicle_id = v.id " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "WHERE b.status = 'PENDING' " +
                     "ORDER BY b.start_date ASC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Object[] {
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("model"),
                    rs.getString("plate_number"),
                    rs.getDate("start_date").toString(),
                    rs.getDate("end_date").toString(),
                    String.format("%.0f RWF", rs.getDouble("total_cost")),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BookingRecord> getPendingBookings() {
        List<BookingRecord> list = new ArrayList<>();
        String sql = "SELECT b.id AS b_id, b.customer_id, b.vehicle_id, b.start_date, b.end_date, b.total_cost, b.status, " +
                     "v.id AS v_id, v.plate_number, v.model, v.category, v.price_per_day, v.image_path, v.fuel_type, v.transmission, v.seats " +
                     "FROM bookings b JOIN vehicles v ON b.vehicle_id = v.id " +
                     "WHERE b.status = 'PENDING' ORDER BY b.start_date ASC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("b_id"));
                b.setCustomerId(rs.getInt("customer_id"));
                b.setVehicleId(rs.getInt("vehicle_id"));
                b.setStartDate(rs.getDate("start_date"));
                b.setEndDate(rs.getDate("end_date"));
                b.setTotalCost(rs.getDouble("total_cost"));
                b.setStatus(rs.getString("status"));
                
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("v_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                
                list.add(new BookingRecord(b, v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Gets next available dates for a vehicle when requested dates are unavailable
     * Finds conflicting bookings and calculates when vehicle will be available
     * 
     * @param vehicleId ID of the vehicle
     * @param requestedStart Requested start date
     * @param requestedEnd Requested end date
     * @return String message indicating when vehicle will be available
     */
    @Override
    public String getNextAvailableDates(int vehicleId, Date requestedStart, Date requestedEnd) {
        String sql = "SELECT start_date, end_date FROM bookings " +
                     "WHERE vehicle_id = ? AND status NOT IN ('CANCELLED', 'REJECTED') " +
                     "AND (start_date <= ? AND end_date >= ?) " +
                     "ORDER BY start_date ASC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, vehicleId);
            pst.setDate(2, new java.sql.Date(requestedEnd.getTime()));
            pst.setDate(3, new java.sql.Date(requestedStart.getTime()));
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Calculate next available date (day after booking ends)
                    Date bookedEnd = rs.getDate("end_date");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(bookedEnd);
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    Date nextAvailable = cal.getTime();
                    
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    return "Vehicle will be available from " + sdf.format(nextAvailable) + " onwards.";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "Please check availability for different dates.";
    }

}
