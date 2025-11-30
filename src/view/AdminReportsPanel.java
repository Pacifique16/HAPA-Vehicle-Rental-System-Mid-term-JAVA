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
import model.BookingRecord;
import model.Vehicle;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * Reports panel with tabs:
 * - Active Rentals
 * - Most Rented Vehicles
 * - Vehicle Availability
 * - Bookings History
 * - Export to PDF button
 *
 * Expected BookingDAO methods:
 *  - List<BookingRecord> getActiveRentals()
 *  - List<Object[]> getMostRentedVehicles() // model, times, totalIncome
 *  - List<BookingRecord> getBookingsHistory()
 *  - List<Vehicle> getVehiclesAvailability(Date date)
 */
public class AdminReportsPanel extends JPanel {

    private BookingDAO bookingDAO = new BookingDAOImpl();
    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    private JTabbedPane tabs;
    private JTable tblActive, tblMostRented, tblAvailability, tblHistory;
    private boolean[] tabsLoaded = new boolean[4]; // Track which tabs have been loaded

    public AdminReportsPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);
        buildTop();
        buildTabs();
    }

    private void buildTop(){
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(34, 109, 180));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnPdf = new JButton("Export to CSV");
        btnPdf.setBackground(new Color(255, 217, 102));
        btnPdf.setFocusPainted(false);
        btnPdf.addActionListener(e -> exportCurrentTab());
        
        buttonPanel.add(btnPdf);
        
        top.add(titleLabel, BorderLayout.WEST);
        top.add(buttonPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void buildTabs(){
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Active rentals
        tblActive = new JTable(new DefaultTableModel(new String[]{"Customer","Vehicle","Start Date","End Date","Total Cost","Status"},0));
        tblActive.setRowHeight(25);
        tblActive.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblActive.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("Active Rentals", new JScrollPane(tblActive));

        // Most rented
        tblMostRented = new JTable(new DefaultTableModel(new String[]{"Vehicle Model","Times Rented","Total Income"},0));
        tblMostRented.setRowHeight(25);
        tblMostRented.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblMostRented.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("Most Rented", new JScrollPane(tblMostRented));

        // Availability
        tblAvailability = new JTable(new DefaultTableModel(new String[]{"Vehicle Model","Category","Price/Day","Available Today"},0));
        tblAvailability.setRowHeight(25);
        tblAvailability.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAvailability.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add custom renderer for availability column coloring
        tblAvailability.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if ("Yes".equals(value)) {
                        c.setBackground(new Color(144, 238, 144)); // Light green
                    } else {
                        c.setBackground(new Color(255, 182, 193)); // Light red
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });
        
        tabs.addTab("Availability", new JScrollPane(tblAvailability));

        // History
        tblHistory = new JTable(new DefaultTableModel(new String[]{"Customer","Vehicle","Start Date","End Date","Total Cost","Status"},0));
        tblHistory.setRowHeight(25);
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblHistory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("History", new JScrollPane(tblHistory));

        add(tabs, BorderLayout.CENTER);

        // load data when tab changed
        tabs.addChangeListener(e -> loadCurrentTabAsync());
        loadCurrentTabAsync();
    }

    private void loadCurrentTabAsync(){
        int idx = tabs.getSelectedIndex();
        if (idx < 0 || idx >= tabsLoaded.length) return;
        
        // Only load if not already loaded
        if (tabsLoaded[idx]) return;
        
        // Use SwingWorker for background loading
        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                // Load data in background thread
                if (idx == 0) return bookingDAO.getActiveRentalsReport();
                else if (idx == 1) return bookingDAO.getMostRentedVehicles();
                else if (idx == 2) return bookingDAO.getVehicleAvailabilityReport(new Date());
                else if (idx == 3) return bookingDAO.getBookingsHistoryReport();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    Object data = get();
                    if (idx == 0) populateActiveTable(data);
                    else if (idx == 1) populateMostRentedTable(data);
                    else if (idx == 2) populateAvailabilityTable(data);
                    else if (idx == 3) populateHistoryTable(data);
                    tabsLoaded[idx] = true;
                } catch (Exception ex){ ex.printStackTrace(); }
            }
        };
        worker.execute();
    }
    
    public void refreshAllTabs() {
        // Reset loaded flags and reload current tab
        for (int i = 0; i < tabsLoaded.length; i++) {
            tabsLoaded[i] = false;
        }
        loadCurrentTabAsync();
    }

    private void loadActive(){
        List<Object[]> list = bookingDAO.getActiveRentalsReport();
        populateActiveTable(list);
    }
    
    private void populateActiveTable(Object data) {
        DefaultTableModel m = (DefaultTableModel) tblActive.getModel();
        m.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Object[]> list = (List<Object[]>) data;
        for (Object[] row : list) {
            m.addRow(row);
        }
    }

    private void loadMostRented(){
        List<Object[]> rows = bookingDAO.getMostRentedVehicles();
        populateMostRentedTable(rows);
    }
    
    private void populateMostRentedTable(Object data) {
        DefaultTableModel m = (DefaultTableModel) tblMostRented.getModel();
        m.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = (List<Object[]>) data;
        for (Object[] r : rows) {
            Object[] formatted = new Object[]{
                r[0], // model
                r[1], // times rented
                String.format("%,.0f RWF", (Double)r[2]) // total income
            };
            m.addRow(formatted);
        }
    }

    private void loadAvailability(){
        List<Object[]> availabilityData = bookingDAO.getVehicleAvailabilityReport(new Date());
        populateAvailabilityTable(availabilityData);
    }
    
    private void populateAvailabilityTable(Object data) {
        DefaultTableModel m = (DefaultTableModel) tblAvailability.getModel();
        m.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Object[]> availabilityData = (List<Object[]>) data;
        for (Object[] row : availabilityData) {
            m.addRow(row);
        }
        tblAvailability.repaint();
    }

    private void loadHistory(){
        List<Object[]> rows = bookingDAO.getBookingsHistoryReport();
        populateHistoryTable(rows);
    }
    
    private void populateHistoryTable(Object data) {
        DefaultTableModel m = (DefaultTableModel) tblHistory.getModel();
        m.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = (List<Object[]>) data;
        for (Object[] row : rows) {
            m.addRow(row);
        }
    }

    private void exportCurrentTab(){
        int idx = tabs.getSelectedIndex();
        JTable current;
        String title;
        switch (idx) {
            case 0: current = tblActive; title = "Active Rentals Report"; break;
            case 1: current = tblMostRented; title = "Most Rented Vehicles Report"; break;
            case 2: current = tblAvailability; title = "Vehicle Availability Report"; break;
            default: current = tblHistory; title = "Bookings History Report"; break;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save CSV Report");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        int ret = fc.showSaveDialog(this);
        if (ret!=JFileChooser.APPROVE_OPTION) return;
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
        try {
            exportTableToCsv(current, title, path);
            JOptionPane.showMessageDialog(this, "Report exported successfully to: " + path);
        } catch (Exception ex){ 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    private void exportTableToCsv(JTable table, String title, String filePath) throws Exception {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            // Write title and timestamp
            writer.append("HAPA Vehicle Rental System - " + title + "\n");
            writer.append("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n");
            
            // Write headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                writer.append(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) writer.append(",");
            }
            writer.append("\n");
            
            // Write data
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    String cellValue = value != null ? value.toString().replace(",", ";") : "";
                    writer.append(cellValue);
                    if (col < table.getColumnCount() - 1) writer.append(",");
                }
                writer.append("\n");
            }
        }
    }
}
