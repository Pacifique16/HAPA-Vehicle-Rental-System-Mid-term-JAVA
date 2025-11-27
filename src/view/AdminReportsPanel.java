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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public AdminReportsPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);
        buildTop();
        buildTabs();
    }

    private void buildTop(){
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.setBackground(Color.WHITE);
        JButton btnPdf = new JButton("Export Current Tab to PDF");
        btnPdf.addActionListener(e -> exportCurrentTab());
        top.add(btnPdf);
        add(top, BorderLayout.NORTH);
    }

    private void buildTabs(){
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Active rentals
        tblActive = new JTable(new DefaultTableModel(new String[]{"Customer","Vehicle","Start","End","Total","Status"},0));
        tabs.addTab("Active Rentals", new JScrollPane(tblActive));

        // Most rented
        tblMostRented = new JTable(new DefaultTableModel(new String[]{"Vehicle Model","Times Rented","Total Income"},0));
        tabs.addTab("Most Rented", new JScrollPane(tblMostRented));

        // Availability
        tblAvailability = new JTable(new DefaultTableModel(new String[]{"Vehicle","Category","Price/day","Available Today?"},0));
        tabs.addTab("Availability", new JScrollPane(tblAvailability));

        // History
        tblHistory = new JTable(new DefaultTableModel(new String[]{"Customer","Vehicle","Start","End","Total","Status"},0));
        tabs.addTab("History", new JScrollPane(tblHistory));

        add(tabs, BorderLayout.CENTER);

        // load data when tab changed
        tabs.addChangeListener(e -> loadCurrentTab());
        loadCurrentTab();
    }

    private void loadCurrentTab(){
        int idx = tabs.getSelectedIndex();
        try {
            if (idx == 0) loadActive();
            else if (idx == 1) loadMostRented();
            else if (idx == 2) loadAvailability();
            else if (idx == 3) loadHistory();
        } catch (Exception ex){ ex.printStackTrace(); }
    }

    private void loadActive(){
        DefaultTableModel m = (DefaultTableModel) tblActive.getModel();
        m.setRowCount(0);
        List<BookingRecord> list = bookingDAO.getActiveRentals();
        for (BookingRecord r : list) {
            m.addRow(new Object[]{ r.getBooking().getCustomerId(), r.getVehicle().getModel(), r.getBooking().getStartDate(), r.getBooking().getEndDate(), String.format("%,.0f", r.getBooking().getTotalCost()), r.getBooking().getStatus()});
        }
    }

    private void loadMostRented(){
        DefaultTableModel m = (DefaultTableModel) tblMostRented.getModel();
        m.setRowCount(0);
        List<Object[]> rows = bookingDAO.getMostRentedVehicles();
        for (Object[] r : rows) m.addRow(r);
    }

    private void loadAvailability(){
        DefaultTableModel m = (DefaultTableModel) tblAvailability.getModel();
        m.setRowCount(0);
        Date today = new Date();
        List<Vehicle> vs = vehicleDAO.getAllVehicles();
        for (Vehicle v : vs){
            boolean available = bookingDAO.isVehicleAvailableOn(v.getId(), today); // implement this
            m.addRow(new Object[]{v.getModel(), v.getCategory(), String.format("%,.0f", v.getPricePerDay()), available ? "Yes":"No"});
        }
    }

    private void loadHistory(){
        DefaultTableModel m = (DefaultTableModel) tblHistory.getModel();
        m.setRowCount(0);
        List<BookingRecord> rows = bookingDAO.getBookingsHistory();
        for (BookingRecord r : rows) {
            m.addRow(new Object[]{ r.getBooking().getCustomerId(), r.getVehicle().getModel(), r.getBooking().getStartDate(), r.getBooking().getEndDate(), String.format("%,.0f", r.getBooking().getTotalCost()), r.getBooking().getStatus()});
        }
    }

    private void exportCurrentTab(){
        int idx = tabs.getSelectedIndex();
        JTable current;
        String title;
        switch (idx) {
            case 0: current = tblActive; title = "Active Rentals"; break;
            case 1: current = tblMostRented; title = "Most Rented Vehicles"; break;
            case 2: current = tblAvailability; title = "Vehicle Availability"; break;
            default: current = tblHistory; title = "Bookings History"; break;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save PDF");
        int ret = fc.showSaveDialog(this);
        if (ret!=JFileChooser.APPROVE_OPTION) return;
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
        try {
            PdfExporter.exportTableToPdf(current, title, path);
            JOptionPane.showMessageDialog(this, "Exported to: " + path);
        } catch (Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage()); }
    }
}
