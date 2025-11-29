/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author Pacifique Harerimana
 */

import dao.BookingDAO;
import dao.BookingDAOImpl;
import dao.VehicleDAO;
import dao.VehicleDAOImpl;
import dao.UserDAO;
import dao.UserDAOImpl;
import dao.DBConnection;
import model.Vehicle;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Small dashboard: 4 summary cards and recent bookings quick table
 */
public class AdminHomePanel extends JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAOImpl(); // implement if missing
    private final UserDAO userDAO = new UserDAOImpl();
    private final BookingDAO bookingDAO = new BookingDAOImpl();

    private JLabel lblVehicles, lblUsers, lblRentals, lblAvailable;
    private JTable recentBookingsTable;

    public AdminHomePanel() {
        setLayout(new BorderLayout(12,12));
        setBackground(Color.WHITE);
        buildTopCards();
        buildRecent();
        loadAnalytics();
        loadRecentBookings();
    }

    private void buildTopCards(){
        JPanel cards = new JPanel(new GridLayout(1,4,12,12));
        cards.setOpaque(false);

        lblVehicles = makeCard("Total Vehicles", "0");
        lblUsers = makeCard("Total Users", "0");
        lblRentals = makeCard("Total Rentals", "0");
        lblAvailable = makeCard("Available Today", "0");

        cards.add(wrapCard(lblVehicles));
        cards.add(wrapCard(lblUsers));
        cards.add(wrapCard(lblRentals));
        cards.add(wrapCard(lblAvailable));

        add(cards, BorderLayout.NORTH);
    }

    private JPanel wrapCard(JLabel center){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JLabel makeCard(String title, String value){
        JLabel l = new JLabel("<html><div style='text-align:center'><div style='font-size:18px;color:#222;'>"+value+"</div><div style='font-size:12px;color:#666;'>"+title+"</div></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private void buildRecent(){
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        JLabel h = new JLabel("Today's Bookings");
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(h, BorderLayout.NORTH);

        String[] columns = {"Customer", "Vehicle", "Pick-up", "Drop-off", "Status", "Total"};
        recentBookingsTable = new JTable(new javax.swing.table.DefaultTableModel(columns, 0));
        recentBookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recentBookingsTable.setRowHeight(25);
        recentBookingsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Add click listener for booking details
        recentBookingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showBookingDetails();
                }
            }
        });
        
        // Custom renderer for status colors
        recentBookingsTable.setDefaultRenderer(Object.class, new StatusTableRenderer());
        
        JScrollPane sp = new JScrollPane(recentBookingsTable);
        p.add(sp, BorderLayout.CENTER);

        add(p, BorderLayout.CENTER);
    }

    private void loadAnalytics(){
        try {
            // Single query to get all analytics data
            String sql = "SELECT " +
                        "(SELECT COUNT(*) FROM vehicles) as total_vehicles, " +
                        "(SELECT COUNT(*) FROM users) as total_users, " +
                        "(SELECT COUNT(*) FROM bookings WHERE status != 'REJECTED') as total_rentals, " +
                        "(SELECT COUNT(*) FROM vehicles v WHERE v.status != 'Maintenance' " +
                        " AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.vehicle_id = v.id " +
                        " AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
                        " AND CURRENT_DATE BETWEEN b.start_date AND b.end_date)) as available_today";
            
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                
                if (rs.next()) {
                    int totalVehicles = rs.getInt("total_vehicles");
                    int totalUsers = rs.getInt("total_users");
                    int totalRentals = rs.getInt("total_rentals");
                    int availableToday = rs.getInt("available_today");
                    
                    lblVehicles.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalVehicles + "</div><div style='font-size:12px;color:#666;'>Total Vehicles</div></div></html>");
                    lblUsers.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalUsers + "</div><div style='font-size:12px;color:#666;'>Total Users</div></div></html>");
                    lblRentals.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalRentals + "</div><div style='font-size:12px;color:#666;'>Total Rentals</div></div></html>");
                    lblAvailable.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + availableToday + "</div><div style='font-size:12px;color:#666;'>Vehicles Available Today</div></div></html>");
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void loadRecentBookings() {
        try {
            DefaultTableModel model = (DefaultTableModel) recentBookingsTable.getModel();
            model.setRowCount(0); // Clear existing data
            
            // Get today's bookings from database
            String sql = "SELECT u.full_name, v.model, b.start_date, b.end_date, b.status, b.total_cost " +
                        "FROM bookings b " +
                        "JOIN users u ON b.customer_id = u.id " +
                        "JOIN vehicles v ON b.vehicle_id = v.id " +
                        "WHERE DATE(b.start_date) = CURRENT_DATE " +
                        "ORDER BY b.id DESC";
            
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("full_name"),
                        rs.getString("model"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status"),
                        String.format("$%.2f", rs.getDouble("total_cost"))
                    });
                }
                
            } catch (Exception e) {
                model.addRow(new Object[]{"Error loading bookings", e.getMessage(), "", "", ""});
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // Removed auto-refresh to prevent unnecessary database calls
    
    private void showBookingDetails() {
        int row = recentBookingsTable.getSelectedRow();
        if (row >= 0) {
            String customer = (String) recentBookingsTable.getValueAt(row, 0);
            String vehicle = (String) recentBookingsTable.getValueAt(row, 1);
            String pickupDate = recentBookingsTable.getValueAt(row, 2).toString();
            String dropoffDate = recentBookingsTable.getValueAt(row, 3).toString();
            String status = (String) recentBookingsTable.getValueAt(row, 4);
            String total = (String) recentBookingsTable.getValueAt(row, 5);
            
            String details = String.format(
                "Booking Details:\n\n" +
                "Customer: %s\n" +
                "Vehicle: %s\n" +
                "Pick-up Date: %s\n" +
                "Drop-off Date: %s\n" +
                "Status: %s\n" +
                "Total: %s",
                customer, vehicle, pickupDate, dropoffDate, status, total
            );
            
            JOptionPane.showMessageDialog(this, details, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Custom renderer for status color coding
    private class StatusTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 4 && value != null) { // Status column (now at index 4)
                String status = value.toString();
                if (!isSelected) {
                    switch (status.toUpperCase()) {
                        case "APPROVED":
                            c.setBackground(new Color(220, 255, 220)); // Light green
                            break;
                        case "PENDING":
                            c.setBackground(new Color(255, 255, 220)); // Light yellow
                            break;
                        case "REJECTED":
                        case "CANCELLED":
                            c.setBackground(new Color(255, 220, 220)); // Light red
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
            } else if (!isSelected) {
                c.setBackground(Color.WHITE);
            }
            
            return c;
        }
    }
}
