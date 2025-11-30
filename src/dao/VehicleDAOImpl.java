/*
 * HAPA Vehicle Rental System - Vehicle Data Access Object Implementation
 * Implements all database operations related to vehicle management
 * Provides concrete implementation of VehicleDAO interface methods
 * Handles vehicle CRUD operations, search, availability checking, and status management
 */
package dao;

/**
 * VehicleDAOImpl - Implementation of VehicleDAO interface
 * Provides concrete database operations for vehicle management
 * Handles vehicle CRUD operations, search functionality, and availability management
 *
 * @author Pacifique Harerimana
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Vehicle;

/**
 * Implementation class for vehicle database operations
 * Provides concrete methods for vehicle management and availability checking
 */
public class VehicleDAOImpl implements VehicleDAO {

    /**
     * Retrieves all vehicles from the database
     * Loads complete vehicle information including specifications and status
     * 
     * @return List of all vehicles in the system
     */
    @Override
    public List<Vehicle> getAllVehicles() {

        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                // Create vehicle object from database record
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                v.setStatus(rs.getString("status"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
     
        return list;
    }
    
    /**
     * Checks for duplicate booking by same customer for same vehicle and dates
     * Prevents customers from booking the same vehicle multiple times for identical dates
     * 
     * @param customerId ID of the customer
     * @param vehicleId ID of the vehicle
     * @param startDate Start date of the booking
     * @param endDate End date of the booking
     * @return true if duplicate booking exists, false otherwise
     */
    public boolean isDuplicateBooking(int customerId, int vehicleId, Date startDate, Date endDate) {
        // SQL checks for exact match by customer, vehicle, and dates with active status
        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE customer_id = ? AND vehicle_id = ? "
                   + "AND start_date = ? AND end_date = ? "
                   + "AND status NOT IN ('REJECTED', 'CANCELLED')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            // Set query parameters
            pst.setInt(1, customerId);
            pst.setInt(2, vehicleId);
            pst.setDate(3, new java.sql.Date(startDate.getTime()));
            pst.setDate(4, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Checks if vehicle is unavailable due to overlapping bookings
     * Uses standard date overlap logic: (A.start <= B.end) AND (A.end >= B.start)
     * 
     * @param vehicleId ID of the vehicle to check
     * @param startDate Requested start date
     * @param endDate Requested end date
     * @return true if vehicle is unavailable, false if available
     */
    public boolean isVehicleUnavailable(int vehicleId, Date startDate, Date endDate) {
        // Standard SQL date overlap check for conflicting bookings
        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE vehicle_id = ? AND status NOT IN ('REJECTED', 'CANCELLED') "
                   + "AND (start_date <= ? AND end_date >= ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, vehicleId);
            // Check for date overlaps with existing bookings
            pst.setDate(2, new java.sql.Date(endDate.getTime()));   // Compare to new END DATE
            pst.setDate(3, new java.sql.Date(startDate.getTime())); // Compare to new START DATE

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Counts total number of vehicles in the system
     * Used for dashboard statistics and reporting
     * 
     * @return Total count of vehicles
     */
    @Override
    public int countVehicles() {
        String sql = "SELECT COUNT(*) FROM vehicles";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<Vehicle> searchVehicles(String query) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE model ILIKE ? OR category ILIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, "%" + query + "%");
            pst.setString(2, "%" + query + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public Vehicle findById(int id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getInt("id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setModel(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePerDay(rs.getDouble("price_per_day"));
                v.setImagePath(rs.getString("image_path"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setTransmission(rs.getString("transmission"));
                v.setSeats(rs.getInt("seats"));
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean deleteVehicle(int id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate_number, model, category, price_per_day, image_path, fuel_type, transmission, seats) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, vehicle.getPlateNumber());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getCategory());
            pst.setDouble(4, vehicle.getPricePerDay());
            pst.setString(5, vehicle.getImagePath());
            pst.setString(6, vehicle.getFuelType());
            pst.setString(7, vehicle.getTransmission());
            pst.setInt(8, vehicle.getSeats());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET plate_number = ?, model = ?, category = ?, price_per_day = ?, image_path = ?, fuel_type = ?, transmission = ?, seats = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, vehicle.getPlateNumber());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getCategory());
            pst.setDouble(4, vehicle.getPricePerDay());
            pst.setString(5, vehicle.getImagePath());
            pst.setString(6, vehicle.getFuelType());
            pst.setString(7, vehicle.getTransmission());
            pst.setInt(8, vehicle.getSeats());
            pst.setInt(9, vehicle.getId());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
    /**
     * Counts vehicles available for booking today
     * Excludes vehicles in maintenance and those with active bookings
     * Used for dashboard availability statistics
     * 
     * @return Count of vehicles available today
     */
    @Override
    public int countAvailableToday() {
        String sql =
            "SELECT COUNT(*) FROM vehicles v " +
            "WHERE v.status != 'Maintenance' " +
            "AND v.id NOT IN ( " +
            "   SELECT vehicle_id FROM bookings " +
            "   WHERE status NOT IN ('CANCELLED', 'REJECTED', 'EXPIRED') " +
            "   AND CURRENT_DATE BETWEEN start_date AND end_date " +
            ")";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Updates vehicle status (Available, Rented, Maintenance)
     * Used for managing vehicle availability and maintenance scheduling
     * 
     * @param vehicleId ID of the vehicle to update
     * @param status New status to set
     * @return true if status was successfully updated, false otherwise
     */
    @Override
    public boolean updateVehicleStatus(int vehicleId, String status) {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, vehicleId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Checks if vehicle is available for booking based on status
     * Vehicles in maintenance are not available for booking
     * 
     * @param vehicleId ID of the vehicle to check
     * @return true if vehicle is available for booking, false otherwise
     */
    @Override
    public boolean isVehicleAvailableForBooking(int vehicleId) {
        String sql = "SELECT status FROM vehicles WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, vehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return !"Maintenance".equals(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
}

