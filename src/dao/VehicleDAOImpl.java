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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Vehicle;

public class VehicleDAOImpl implements VehicleDAO {

    @Override
    public List<Vehicle> getAllVehicles() {

        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

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
    
    // In dao.BookingDAOImpl.java (Implementation)

    public boolean isDuplicateBooking(int customerId, int vehicleId, Date startDate, Date endDate) {
        // SQL checks for an exact match by customer, vehicle, and dates, with a non-rejected status.
        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE customer_id = ? AND vehicle_id = ? "
                   + "AND start_date = ? AND end_date = ? "
                   + "AND status NOT IN ('REJECTED', 'CANCELLED')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

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
    
    // In dao.BookingDAOImpl.java (Implementation)

    public boolean isVehicleUnavailable(int vehicleId, Date startDate, Date endDate) {
        // Standard SQL date overlap check:
        // (A.start <= B.end) AND (A.end >= B.start)
        // The query checks if the existing booking's start date is before the new end date
        // AND the existing booking's end date is after the new start date.
        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE vehicle_id = ? AND status NOT IN ('REJECTED', 'CANCELLED') "
                   + "AND (start_date <= ? AND end_date >= ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, vehicleId);
            // The dates from the form (new booking) are used for comparison
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

    
    @Override
    public int countAvailableToday() {
        String sql =
            "SELECT COUNT(*) FROM vehicles v " +
            "WHERE v.id NOT IN ( " +
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

    
}

