/*
 * HAPA Vehicle Rental System - Booking Approval Management Panel
 * Administrative interface for reviewing and processing customer booking requests
 * Features comprehensive booking management with search, filtering, and bulk operations
 * Supports approval workflow, rejection with reasons, and CSV export functionality
 */
package view;

/**
 * BookingsApprovalPanel - Administrative panel for managing customer booking approvals
 * Provides comprehensive booking management functionality including:
 * - Pending booking review and approval workflow
 * - Advanced search and filtering capabilities
 * - Bulk approval and rejection operations
 * - CSV export for reporting and record keeping
 * - Status-based visual indicators and color coding
 * - Context menu for quick actions
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
 * Handles all aspects of booking approval workflow for administrators
 */
public class BookingsApprovalPanel extends JPanel {

    /** Data access object for booking operations */
    private final BookingDAO bookingDAO = new BookingDAOImpl();
    
    /** Main table displaying booking information */
    private JTable table;
    
    /** Table model for managing booking data */
    private DefaultTableModel model;
    
    /** Search text field for filtering bookings */
    private JTextField tfSearch;
    
    /** Status filter dropdown for booking status filtering */
    private JComboBox<String> statusFilter;

    /**
     * Constructor - Initializes the booking approval panel
     * Sets up the user interface components and loads initial data
     */
    public BookingsApprovalPanel() {
        setLayout(new BorderLayout(8,8));    // Set layout with spacing
        setBackground(Color.WHITE);          // Set background color
        buildTop();                          // Build search and action controls
        buildTable();                        // Build main booking table
        loadAll();                          // Load initial booking data
    }

    /**
     * Builds the top control panel with search, filter, and action buttons
     * Creates search functionality and bulk operation controls
     */
    private void buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Create search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        
        // Initialize search text field
        tfSearch = new JTextField(20);
        styleRounded(tfSearch);
        tfSearch.setToolTipText("Search customer, vehicle, dates...");
        tfSearch.addActionListener(e -> doSearch());

        // Initialize status filter dropdown
        statusFilter = new JComboBox<>(new String[]{"All Status", "PENDING", "APPROVED", "REJECTED"});
        statusFilter.addActionListener(e -> doAdvancedSearch());

        // Add search components to panel
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        // Create action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        // Initialize action buttons
        JButton bRefresh = new JButton("Refresh");
        JButton bExport = new JButton("Export CSV");
        JButton bBulkApprove = new JButton("Bulk Approve");
        JButton bBulkReject = new JButton("Bulk Reject");
        
        // Add button event handlers
        bRefresh.addActionListener(e -> loadAll());
        bExport.addActionListener(e -> exportToCSV());
        bBulkApprove.addActionListener(e -> bulkApprove());
        bBulkReject.addActionListener(e -> bulkReject());
        
        // Add buttons to action panel
        actionPanel.add(bExport);
        actionPanel.add(bBulkApprove);
        actionPanel.add(bBulkReject);
        actionPanel.add(bRefresh);

        // Add panels to top container
        top.add(searchPanel, BorderLayout.WEST);
        top.add(actionPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    /**
     * Builds the main booking table with context menu and custom rendering
     * Sets up table structure, styling, and interactive features
     */
    private void buildTable() {
        // Define table columns
        String[] cols = new String[]{"ID","Customer","Phone","Vehicle","Plate","Start Date","End Date","Total Cost","Status"};
        
        // Create non-editable table model
        model = new DefaultTableModel(cols,0) { 
            public boolean isCellEditable(int r,int c){return false;}
        };
        
        // Initialize table with model
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // Hide ID column for cleaner display
        table.setRowHeight(30);                                  // Set comfortable row height
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));     // Set readable font
        
        // Apply custom renderer for alternating colors and status highlighting
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        // Add table to scrollable container
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        // Create context menu for quick actions
        JPopupMenu pm = new JPopupMenu();
        JMenuItem miApprove = new JMenuItem("Approve");
        JMenuItem miReject = new JMenuItem("Reject");
        
        // Add context menu event handlers
        miApprove.addActionListener(e -> doApprove());
        miReject.addActionListener(e -> doReject());
        
        // Add menu items and attach to table
        pm.add(miApprove); 
        pm.add(miReject);
        table.setComponentPopupMenu(pm);
    }

    /**
     * Loads all pending bookings from the database
     * Clears existing table data and populates with fresh booking information
     */
    private void loadAll(){
        model.setRowCount(0);    // Clear existing table data
        try {
            // Retrieve pending bookings from database
            List<Object[]> list = bookingDAO.getPendingBookingsReport();
            
            // Add each booking to the table model
            for (Object[] row : list) {
                model.addRow(row);
            }
        } catch(Exception ex){ 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    /**
     * Performs search filtering on booking data
     * Filters bookings based on search text across all visible columns
     */
    private void doSearch(){
        String q = tfSearch.getText().trim().toLowerCase();    // Get search query
        model.setRowCount(0);                                  // Clear current results
        
        try {
            // Get all bookings from database
            List<Object[]> allBookings = bookingDAO.getPendingBookingsReport();
            
            // Filter bookings based on search criteria
            for (Object[] row : allBookings) {
                if (q.isEmpty() || matchesSearchRow(row, q)) {
                    model.addRow(row);
                }
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    /**
     * Checks if a booking row matches the search query
     * Searches across all visible columns for the query string
     * 
     * @param row The booking data row to check
     * @param query The search query string (lowercase)
     * @return true if the row matches the query, false otherwise
     */
    private boolean matchesSearchRow(Object[] row, String query) {
        // Check each column (skip ID column at index 0)
        for (int i = 1; i < row.length; i++) {
            if (row[i] != null && row[i].toString().toLowerCase().contains(query)) {
                return true;    // Match found
            }
        }
        return false;    // No match found
    }

    /**
     * Approves a selected booking after user confirmation
     * Updates booking status to APPROVED in the database
     */
    private void doApprove(){
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to approve");
            return;
        }
        
        // Confirm approval action with user
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to approve this booking?", 
            "Confirm Approval", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Get booking ID and perform approval
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.approveBooking(id);
        
        // Show result and refresh data
        if (ok) { 
            JOptionPane.showMessageDialog(this, "Booking approved successfully!"); 
            loadAll();    // Refresh table data
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve booking.");
        }
    }

    /**
     * Rejects a selected booking with a required reason
     * Prompts for rejection reason and updates booking status to REJECTED
     */
    private void doReject(){
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to reject");
            return;
        }
        
        // Prompt for rejection reason
        String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:");
        
        if (reason == null) return; // User cancelled
        
        // Validate rejection reason
        if (reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rejection reason is required!");
            return;
        }
        
        // Confirm rejection action with user
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to reject this booking?", 
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Get booking ID and perform rejection with reason
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.rejectBookingWithReason(id, reason.trim());
        
        // Show result and refresh data
        if (ok) { 
            JOptionPane.showMessageDialog(this, "Booking rejected successfully!"); 
            loadAll();    // Refresh table data
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject booking.");
        }
    }

    /**
     * Applies rounded styling to UI components
     * Creates a bordered appearance with padding for better visual appeal
     * 
     * @param c The component to style
     */
    private void styleRounded(JComponent c){
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }
    
    /**
     * Performs advanced search with both text and status filtering
     * Combines search text and status filter for refined results
     */
    private void doAdvancedSearch() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String searchText = tfSearch.getText().trim().toLowerCase();
        
        model.setRowCount(0);    // Clear current results
        
        try {
            List<Object[]> list = bookingDAO.getPendingBookingsReport();
            
            for (Object[] row : list) {
                // Apply text search filter
                if (!searchText.isEmpty() && !matchesSearchRow(row, searchText)) {
                    continue;
                }
                
                // Apply status filter
                String status = row[8].toString(); // Status is at index 8
                if (!selectedStatus.equals("All Status") && !status.equals(selectedStatus)) {
                    continue;
                }
                
                model.addRow(row);    // Add row if it passes all filters
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    /**
     * Exports current booking data to CSV file
     * Allows user to save filtered booking results for external analysis
     */
    private void exportToCSV() {
        try {
            // Show file save dialog
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save CSV Report");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
            
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
                
                // Write CSV file
                try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                    // Write report header with timestamp
                    writer.append("HAPA Vehicle Rental System - Bookings Approval Report\n");
                    writer.append("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n");
                    
                    // Write column headers
                    writer.append("Customer,Phone,Vehicle,Plate,Start Date,End Date,Total Cost,Status\n");
                    
                    // Write booking data
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 1; j < model.getColumnCount(); j++) { // Skip ID column
                            String cellValue = model.getValueAt(i, j) != null ? 
                                model.getValueAt(i, j).toString().replace(",", ";") : "";
                            writer.append(cellValue);
                            if (j < model.getColumnCount() - 1) writer.append(",");
                        }
                        writer.append("\n");
                    }
                }
                JOptionPane.showMessageDialog(this, "Report exported successfully to: " + path);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Performs bulk approval of selected bookings
     * Approves multiple bookings at once for efficiency
     */
    private void bulkApprove() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select bookings to approve");
            return;
        }
        
        // Confirm bulk approval action
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Approve " + rows.length + " bookings?", "Confirm Bulk Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int approved = 0;
            
            // Process each selected booking
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (bookingDAO.approveBooking(id)) approved++;
            }
            
            // Show results and refresh data
            JOptionPane.showMessageDialog(this, "Approved " + approved + " bookings");
            loadAll();
        }
    }
    
    /**
     * Performs bulk rejection of selected bookings
     * Rejects multiple bookings at once for efficiency
     */
    private void bulkReject() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select bookings to reject");
            return;
        }
        
        // Confirm bulk rejection action
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Reject " + rows.length + " bookings?", "Confirm Bulk Rejection", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int rejected = 0;
            
            // Process each selected booking
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (bookingDAO.rejectBooking(id)) rejected++;
            }
            
            // Show results and refresh data
            JOptionPane.showMessageDialog(this, "Rejected " + rejected + " bookings");
            loadAll();
        }
    }
    

    
    /**
     * Custom table cell renderer for enhanced visual presentation
     * Provides alternating row colors and status-based color coding
     */
    private class AlternatingRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        /**
         * Renders table cells with alternating row colors and status highlighting
         * 
         * @param table The table being rendered
         * @param value The cell value
         * @param isSelected Whether the cell is selected
         * @param hasFocus Whether the cell has focus
         * @param row The row index
         * @param column The column index
         * @return The rendered component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                // Apply alternating row colors
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 248, 248));
                }
                
                // Apply status-based color coding
                if (column == 8 && value != null) { // Status column
                    String status = value.toString();
                    switch (status) {
                        case "PENDING":
                            c.setBackground(new Color(255, 255, 220)); // Light yellow for pending
                            break;
                        case "APPROVED":
                            c.setBackground(new Color(220, 255, 220)); // Light green for approved
                            break;
                        case "REJECTED":
                            c.setBackground(new Color(255, 220, 220)); // Light red for rejected
                            break;
                    }
                }
            }
            
            return c;
        }
    }
}