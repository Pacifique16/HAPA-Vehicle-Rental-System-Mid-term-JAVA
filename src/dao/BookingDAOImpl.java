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
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import model.User;

public class BookingDAOImpl implements BookingDAO {

    @Override
    public boolean addBooking(Booking b) {
        String sql = "INSERT INTO bookings (customer_id, vehicle_id, start_date, end_date, total_cost, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

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

    // basic method left for compatibility
    @Override
    public List<BookingRecord> getBookingsByCustomer(int customerId) {
        return getFilteredBookings(customerId, null, null, null, null);
    }

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
        sql.append("SELECT b.id AS b_id, b.customer_id, b.vehicle_id, b.start_date, b.end_date, b.total_cost, b.status, ")
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean approveBooking(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean rejectBooking(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BookingRecord> getActiveRentals() {
        List<BookingRecord> list = new ArrayList<>();
        // Simplified implementation - return empty list for now
        return list;
    }
    
    @Override
    public List<BookingRecord> getBookingsHistory() {
        List<BookingRecord> list = new ArrayList<>();
        // Simplified implementation - return empty list for now
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
            "SELECT v.model, COUNT(b.id) AS times_rented, " +
            "SUM(b.total_cost) AS total_income " +
            "FROM bookings b " +
            "JOIN vehicles v ON b.vehicle_id = v.id " +
            "WHERE b.status = 'APPROVED' " +
            "GROUP BY v.model " +
            "ORDER BY times_rented DESC";

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
    public List<BookingRecord> getPendingBookings() {
        throw new UnsupportedOperationException("getPendingBookings() not implemented yet.");
    }



}
