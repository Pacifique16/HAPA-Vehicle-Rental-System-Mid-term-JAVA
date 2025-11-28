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
import model.BookingRecord;
import model.Booking;
import model.Vehicle;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Enhanced Bookings Approval Panel with modern UI and comprehensive functionality
 */
public class BookingsApprovalPanel extends JPanel {

    private final BookingDAO bookingDAO = new BookingDAOImpl();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JComboBox<String> statusFilter;

    public BookingsApprovalPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);
        buildTop();
        buildTable();
        loadAll();
    }

    private void buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        
        tfSearch = new JTextField(20);
        styleRounded(tfSearch);
        tfSearch.setToolTipText("Search customer, vehicle, dates...");
        tfSearch.addActionListener(e -> doSearch());

        // Filter components
        statusFilter = new JComboBox<>(new String[]{"All Status", "PENDING", "APPROVED", "REJECTED"});
        statusFilter.addActionListener(e -> doAdvancedSearch());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton bRefresh = new JButton("Refresh");
        JButton bExport = new JButton("Export CSV");
        JButton bBulkApprove = new JButton("Bulk Approve");
        JButton bBulkReject = new JButton("Bulk Reject");
        
        bRefresh.addActionListener(e -> loadAll());
        bExport.addActionListener(e -> exportToCSV());
        bBulkApprove.addActionListener(e -> bulkApprove());
        bBulkReject.addActionListener(e -> bulkReject());
        
        actionPanel.add(bExport);
        actionPanel.add(bBulkApprove);
        actionPanel.add(bBulkReject);
        actionPanel.add(bRefresh);

        top.add(searchPanel, BorderLayout.WEST);
        top.add(actionPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void buildTable() {
        String[] cols = new String[]{"ID","Customer","Phone","Vehicle","Plate","Start Date","End Date","Total Cost","Status"};
        model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Alternating row colors with status-based coloring
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPopupMenu pm = new JPopupMenu();
        JMenuItem miApprove = new JMenuItem("Approve");
        JMenuItem miReject = new JMenuItem("Reject");
        
        miApprove.addActionListener(e -> doApprove());
        miReject.addActionListener(e -> doReject());
        
        pm.add(miApprove); 
        pm.add(miReject);
        table.setComponentPopupMenu(pm);
    }

    private void loadAll(){
        model.setRowCount(0);
        try {
            List<BookingRecord> list = bookingDAO.getPendingBookings();
            for (BookingRecord r : list) {
                Booking b = r.getBooking();
                Vehicle v = r.getVehicle();
                User u = bookingDAO.getCustomerForBooking(b.getCustomerId());
                String customerName = u != null ? u.getFullName() : "Unknown";
                String customerPhone = u != null ? u.getPhone() : "N/A";
                
                model.addRow(new Object[]{
                    b.getId(), 
                    customerName,
                    customerPhone,
                    v.getModel(), 
                    v.getPlateNumber(),
                    b.getStartDate().toString(), 
                    b.getEndDate().toString(), 
                    String.format("%.0f RWF", b.getTotalCost()), 
                    b.getStatus()
                });
            }
        } catch(Exception ex){ 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    private void doSearch(){
        String q = tfSearch.getText().trim().toLowerCase();
        model.setRowCount(0);
        try {
            List<BookingRecord> allBookings = bookingDAO.getPendingBookings();
            for (BookingRecord r : allBookings) {
                Booking b = r.getBooking();
                Vehicle v = r.getVehicle();
                User u = bookingDAO.getCustomerForBooking(b.getCustomerId());
                String customerName = u != null ? u.getFullName() : "Unknown";
                String customerPhone = u != null ? u.getPhone() : "N/A";
                
                // Check if search query matches any field
                if (q.isEmpty() || matchesSearch(customerName, customerPhone, v, b, q)) {
                    model.addRow(new Object[]{
                        b.getId(), 
                        customerName,
                        customerPhone,
                        v.getModel(), 
                        v.getPlateNumber(),
                        b.getStartDate().toString(), 
                        b.getEndDate().toString(), 
                        String.format("%.0f RWF", b.getTotalCost()), 
                        b.getStatus()
                    });
                }
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    private boolean matchesSearch(String customerName, String customerPhone, Vehicle v, Booking b, String query) {
        return (customerName != null && customerName.toLowerCase().contains(query)) ||
               (customerPhone != null && customerPhone.toLowerCase().contains(query)) ||
               (v.getModel() != null && v.getModel().toLowerCase().contains(query)) ||
               (v.getPlateNumber() != null && v.getPlateNumber().toLowerCase().contains(query)) ||
               (v.getCategory() != null && v.getCategory().toLowerCase().contains(query)) ||
               (b.getStatus() != null && b.getStatus().toLowerCase().contains(query)) ||
               b.getStartDate().toString().contains(query) ||
               b.getEndDate().toString().contains(query);
    }

    private void doApprove(){
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to approve");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to approve this booking?", 
            "Confirm Approval", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.approveBooking(id);
        
        if (ok) { 
            JOptionPane.showMessageDialog(this, "Booking approved successfully!"); 
            loadAll(); 
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve booking.");
        }
    }

    private void doReject(){
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to reject");
            return;
        }
        
        String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:");
        
        if (reason == null) return; // User cancelled
        
        if (reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rejection reason is required!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to reject this booking?", 
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.rejectBookingWithReason(id, reason.trim());
        
        if (ok) { 
            JOptionPane.showMessageDialog(this, "Booking rejected successfully!"); 
            loadAll(); 
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject booking.");
        }
    }

    private void styleRounded(JComponent c){
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }
    
    // New enhanced functionality methods
    private void doAdvancedSearch() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String searchText = tfSearch.getText().trim();
        
        model.setRowCount(0);
        try {
            List<BookingRecord> list = bookingDAO.getPendingBookings();
            
            for (BookingRecord r : list) {
                Booking b = r.getBooking();
                Vehicle v = r.getVehicle();
                User u = bookingDAO.getCustomerForBooking(b.getCustomerId());
                String customerName = u != null ? u.getFullName() : "Unknown";
                String customerPhone = u != null ? u.getPhone() : "N/A";
                
                // Apply search filter
                if (!searchText.isEmpty() && !matchesSearch(customerName, customerPhone, v, b, searchText.toLowerCase())) {
                    continue;
                }
                
                // Apply status filter
                if (!selectedStatus.equals("All Status") && !b.getStatus().equals(selectedStatus)) {
                    continue;
                }
                
                model.addRow(new Object[]{
                    b.getId(), 
                    customerName,
                    customerPhone,
                    v.getModel(), 
                    v.getPlateNumber(),
                    b.getStartDate().toString(), 
                    b.getEndDate().toString(), 
                    String.format("%.0f RWF", b.getTotalCost()), 
                    b.getStatus()
                });
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    private void exportToCSV() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save CSV File");
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
                
                StringBuilder csv = new StringBuilder();
                csv.append("Customer,Phone,Vehicle,Plate,Start Date,End Date,Total Cost,Status\\n");
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 1; j < model.getColumnCount(); j++) { // Skip ID
                        csv.append(model.getValueAt(i, j));
                        if (j < model.getColumnCount() - 1) csv.append(",");
                    }
                    csv.append("\\n");
                }
                
                java.nio.file.Files.write(java.nio.file.Paths.get(path), csv.toString().getBytes());
                JOptionPane.showMessageDialog(this, "Exported to: " + path);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
    
    private void bulkApprove() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select bookings to approve");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Approve " + rows.length + " bookings?", "Confirm Bulk Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int approved = 0;
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (bookingDAO.approveBooking(id)) approved++;
            }
            JOptionPane.showMessageDialog(this, "Approved " + approved + " bookings");
            loadAll();
        }
    }
    
    private void bulkReject() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select bookings to reject");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Reject " + rows.length + " bookings?", "Confirm Bulk Rejection", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int rejected = 0;
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (bookingDAO.rejectBooking(id)) rejected++;
            }
            JOptionPane.showMessageDialog(this, "Rejected " + rejected + " bookings");
            loadAll();
        }
    }
    

    
    // Custom renderer for alternating row colors and status-based coloring
    private class AlternatingRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 248, 248));
                }
                
                // Status-based coloring
                if (column == 8 && value != null) { // Status column
                    String status = value.toString();
                    switch (status) {
                        case "PENDING":
                            c.setBackground(new Color(255, 255, 220)); // Light yellow
                            break;
                        case "APPROVED":
                            c.setBackground(new Color(220, 255, 220)); // Light green
                            break;
                        case "REJECTED":
                            c.setBackground(new Color(255, 220, 220)); // Light red
                            break;
                    }
                }
            }
            
            return c;
        }
    }
}