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

import dao.VehicleDAO;
import dao.VehicleDAOImpl;
import model.Vehicle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage vehicles: add / edit / delete / search
 *
 * Expected VehicleDAO methods:
 *  - List<Vehicle> getAllVehicles()
 *  - List<Vehicle> searchVehicles(String q)
 *  - boolean addVehicle(Vehicle v)
 *  - boolean updateVehicle(Vehicle v)
 *  - boolean deleteVehicle(int id)
 */
public class ManageVehiclesPanel extends JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAOImpl();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JComboBox<String> categoryFilter, statusFilter;

    public ManageVehiclesPanel() {
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
        tfSearch.setToolTipText("Search plate, model, category, fuel, transmission, status, seats, price...");
        tfSearch.addActionListener(e -> doSearch());

        // Filter components
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SUV", "Sedan", "Hatchback", "Truck", "Van", "Coupe", "Luxury", "Supercar", "Off Road", "Ultra Luxury"});
        statusFilter = new JComboBox<>(new String[]{"All Status", "Available", "Rented", "Maintenance"});
        
        categoryFilter.addActionListener(e -> doAdvancedSearch());
        statusFilter.addActionListener(e -> doAdvancedSearch());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton bAdd = new JButton("Add Vehicle");
        JButton bExport = new JButton("Export CSV");
        JButton bBulkDelete = new JButton("Bulk Delete");
        
        bAdd.addActionListener(e -> openAddDialog());
        bExport.addActionListener(e -> exportToCSV());
        bBulkDelete.addActionListener(e -> bulkDelete());
        
        actionPanel.add(bExport);
        actionPanel.add(bBulkDelete);
        actionPanel.add(bAdd);

        top.add(searchPanel, BorderLayout.WEST);
        top.add(actionPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void buildTable() {
        String[] cols = new String[]{"ID","Plate","Model","Category","Fuel","Transmission","Seats","Price/day","Status","Image"};
        model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPopupMenu pm = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("Edit");
        JMenuItem miDelete = new JMenuItem("Delete");
        JMenuItem miViewImage = new JMenuItem("View Image");
        JMenuItem miSetMaintenance = new JMenuItem("Set Maintenance");
        miEdit.addActionListener(e -> openEditDialog());
        miDelete.addActionListener(e -> doDelete());
        miViewImage.addActionListener(e -> viewVehicleImage());
        miSetMaintenance.addActionListener(e -> toggleMaintenance());
        pm.add(miEdit); 
        pm.add(miDelete);
        pm.add(new JSeparator());
        pm.add(miViewImage);
        pm.add(miSetMaintenance);
        table.setComponentPopupMenu(pm);
    }

    private void loadAll(){
        model.setRowCount(0);
        try {
            List<Vehicle> list = vehicleDAO.getAllVehicles();
            for (Vehicle v : list) {
                String status = getVehicleStatus(v);
                model.addRow(new Object[]{
                    v.getId(), 
                    v.getPlateNumber(), 
                    v.getModel(), 
                    v.getCategory(),
                    v.getFuelType() != null ? v.getFuelType() : "Petrol",
                    v.getTransmission() != null ? v.getTransmission() : "Manual",
                    v.getSeats(),
                    String.format("%.0f RWF", v.getPricePerDay()),
                    status,
                    v.getImagePath()
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private String getVehicleStatus(Vehicle vehicle) {
        // Get status from vehicle object (already loaded from database)
        return vehicle.getStatus() != null ? vehicle.getStatus() : "Available";
    }

    private void doSearch(){
        String q = tfSearch.getText().trim().toLowerCase();
        model.setRowCount(0);
        try {
            List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
            for (Vehicle v : allVehicles) {
                String status = getVehicleStatus(v);
                
                // Check if search query matches any field
                if (q.isEmpty() || matchesSearch(v, status, q)) {
                    model.addRow(new Object[]{
                        v.getId(), 
                        v.getPlateNumber(), 
                        v.getModel(), 
                        v.getCategory(),
                        v.getFuelType() != null ? v.getFuelType() : "Petrol",
                        v.getTransmission() != null ? v.getTransmission() : "Manual",
                        v.getSeats(),
                        String.format("%.0f RWF", v.getPricePerDay()),
                        status,
                        v.getImagePath()
                    });
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private boolean matchesSearch(Vehicle v, String status, String query) {
        return (v.getPlateNumber() != null && v.getPlateNumber().toLowerCase().contains(query)) ||
               (v.getModel() != null && v.getModel().toLowerCase().contains(query)) ||
               (v.getCategory() != null && v.getCategory().toLowerCase().contains(query)) ||
               (v.getFuelType() != null && v.getFuelType().toLowerCase().contains(query)) ||
               (v.getTransmission() != null && v.getTransmission().toLowerCase().contains(query)) ||
               (status != null && status.toLowerCase().contains(query)) ||
               String.valueOf(v.getSeats()).contains(query) ||
               String.valueOf((int)v.getPricePerDay()).contains(query);
    }

    private void openAddDialog(){
        VehicleFormDialog dlg = new VehicleFormDialog(null);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadAll();
    }

    private void openEditDialog(){
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Select a vehicle"); return; }
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        try {
            Vehicle v = vehicleDAO.findById(id);
            VehicleFormDialog dlg = new VehicleFormDialog(v);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadAll();
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void doDelete(){
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete vehicle?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = vehicleDAO.deleteVehicle(id);
        if (ok) { JOptionPane.showMessageDialog(this, "Deleted."); loadAll(); }
        else JOptionPane.showMessageDialog(this, "Failed to delete.");
    }

    private void styleRounded(JComponent c){
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }

    // Enhanced vehicle add/edit dialog (inner class)
    private class VehicleFormDialog extends JDialog {
        private boolean saved = false;
        private JTextField tfPlate, tfModel, tfPrice, tfSeats;
        private JComboBox<String> cbCategory, cbFuelType, cbTransmission;
        private JTextField tfImagePath;
        private JButton btnBrowseImage;
        private Vehicle editing;

        VehicleFormDialog(Vehicle v) {
            super((Frame) SwingUtilities.getWindowAncestor(ManageVehiclesPanel.this), true);
            this.editing = v;
            setTitle(v==null? "Add Vehicle" : "Edit Vehicle");
            setSize(550,450);
            setLayout(new BorderLayout(8,8));
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.WEST;

            // Initialize components with validation
            tfPlate = new JTextField(v==null?"":v.getPlateNumber());
            tfPlate.setPreferredSize(new java.awt.Dimension(200, 25));
            
            tfModel = new JTextField(v==null?"":v.getModel());
            tfModel.setPreferredSize(new java.awt.Dimension(200, 25));
            
            cbCategory = new JComboBox<>(new String[]{"SUV", "Sedan", "Hatchback", "Truck", "Van", "Coupe", "Luxury", "Supercar", "Off Road", "Ultra Luxury"});
            cbCategory.setPreferredSize(new java.awt.Dimension(200, 25));
            if (v != null) cbCategory.setSelectedItem(v.getCategory());
            
            cbFuelType = new JComboBox<>(new String[]{"Petrol", "Diesel", "Electric", "Hybrid"});
            cbFuelType.setPreferredSize(new java.awt.Dimension(200, 25));
            if (v != null && v.getFuelType() != null) cbFuelType.setSelectedItem(v.getFuelType());
            
            cbTransmission = new JComboBox<>(new String[]{"Manual", "Automatic", "CVT"});
            cbTransmission.setPreferredSize(new java.awt.Dimension(200, 25));
            if (v != null && v.getTransmission() != null) cbTransmission.setSelectedItem(v.getTransmission());
            
            tfPrice = new JTextField(v==null? "0": String.valueOf((long)v.getPricePerDay()));
            tfPrice.setPreferredSize(new java.awt.Dimension(200, 25));
            
            tfSeats = new JTextField(v==null? "4": String.valueOf(v.getSeats()));
            tfSeats.setPreferredSize(new java.awt.Dimension(200, 25));
            
            tfImagePath = new JTextField(v==null?"":v.getImagePath());
            tfImagePath.setPreferredSize(new java.awt.Dimension(200, 25));
            
            btnBrowseImage = new JButton("Browse");
            btnBrowseImage.addActionListener(e -> browseImage());

            // Layout components
            c.gridx=0; c.gridy=0; p.add(new JLabel("Plate Number *:"), c); c.gridx=1; p.add(tfPlate, c);
            c.gridx=0; c.gridy=1; p.add(new JLabel("Model *:"), c); c.gridx=1; p.add(tfModel, c);
            c.gridx=0; c.gridy=2; p.add(new JLabel("Category *:"), c); c.gridx=1; p.add(cbCategory, c);
            c.gridx=0; c.gridy=3; p.add(new JLabel("Fuel Type:"), c); c.gridx=1; p.add(cbFuelType, c);
            c.gridx=0; c.gridy=4; p.add(new JLabel("Transmission:"), c); c.gridx=1; p.add(cbTransmission, c);
            c.gridx=0; c.gridy=5; p.add(new JLabel("Price/day *:"), c); c.gridx=1; p.add(tfPrice, c);
            c.gridx=0; c.gridy=6; p.add(new JLabel("Seats:"), c); c.gridx=1; p.add(tfSeats, c);
            c.gridx=0; c.gridy=7; p.add(new JLabel("Image:"), c); 
            JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            imgPanel.add(tfImagePath); 
            imgPanel.add(Box.createHorizontalStrut(5));
            imgPanel.add(btnBrowseImage);
            c.gridx=1; p.add(imgPanel, c);

            add(p, BorderLayout.CENTER);
            JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton save = new JButton("Save"); 
            JButton cancel = new JButton("Cancel");
            b.add(cancel); b.add(save);
            add(b, BorderLayout.SOUTH);

            save.addActionListener(ae -> {
                if (validateInput()) {
                    try {
                        Vehicle vv = editing==null ? new Vehicle() : editing;
                        vv.setPlateNumber(tfPlate.getText().trim().toUpperCase());
                        vv.setModel(tfModel.getText().trim());
                        vv.setCategory(cbCategory.getSelectedItem().toString());
                        vv.setFuelType(cbFuelType.getSelectedItem().toString());
                        vv.setTransmission(cbTransmission.getSelectedItem().toString());
                        vv.setPricePerDay(Double.parseDouble(tfPrice.getText().trim()));
                        vv.setSeats(Integer.parseInt(tfSeats.getText().trim()));
                        vv.setImagePath(tfImagePath.getText().trim());

                        // Check for duplicate plate number
                        if (editing == null && isDuplicatePlate(vv.getPlateNumber())) {
                            JOptionPane.showMessageDialog(this, "Plate number already exists!");
                            return;
                        }

                        boolean ok;
                        if (editing==null) ok = vehicleDAO.addVehicle(vv);
                        else ok = vehicleDAO.updateVehicle(vv);

                        if (ok) { saved = true; dispose(); }
                        else JOptionPane.showMessageDialog(this, "Save failed.");
                    } catch (Exception ex){ 
                        ex.printStackTrace(); 
                        JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage()); 
                    }
                }
            });
            
            cancel.addActionListener(ae -> dispose());
        }
        
        private void browseImage() {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfImagePath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
        
        private boolean validateInput() {
            if (tfPlate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plate number is required!");
                tfPlate.requestFocus();
                return false;
            }
            
            if (tfModel.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Model is required!");
                tfModel.requestFocus();
                return false;
            }
            
            try {
                double price = Double.parseDouble(tfPrice.getText().trim());
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Price must be greater than 0!");
                    tfPrice.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price format!");
                tfPrice.requestFocus();
                return false;
            }
            
            try {
                int seats = Integer.parseInt(tfSeats.getText().trim());
                if (seats < 1 || seats > 50) {
                    JOptionPane.showMessageDialog(this, "Seats must be between 1 and 50!");
                    tfSeats.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid seats format!");
                tfSeats.requestFocus();
                return false;
            }
            
            return true;
        }
        
        private boolean isDuplicatePlate(String plateNumber) {
            try {
                List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
                return vehicles.stream().anyMatch(v -> v.getPlateNumber().equalsIgnoreCase(plateNumber));
            } catch (Exception e) {
                return false;
            }
        }
        
        public boolean isSaved(){ return saved; }
    }
    
    // New methods for enhanced functionality
    private void doAdvancedSearch() {
        String searchText = tfSearch.getText().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        model.setRowCount(0);
        try {
            List<Vehicle> list = searchText.isEmpty() ? vehicleDAO.getAllVehicles() : vehicleDAO.searchVehicles(searchText);
            
            for (Vehicle v : list) {
                String status = getVehicleStatus(v);
                
                // Apply category filter
                if (!selectedCategory.equals("All Categories") && !v.getCategory().equals(selectedCategory)) {
                    continue;
                }
                
                // Apply status filter
                if (!selectedStatus.equals("All Status") && !status.equals(selectedStatus)) {
                    continue;
                }
                
                model.addRow(new Object[]{
                    v.getId(), 
                    v.getPlateNumber(), 
                    v.getModel(), 
                    v.getCategory(),
                    v.getFuelType() != null ? v.getFuelType() : "Petrol",
                    v.getTransmission() != null ? v.getTransmission() : "Manual",
                    v.getSeats(),
                    String.format("%.0f RWF", v.getPricePerDay()),
                    status,
                    v.getImagePath()
                });
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    private void exportToCSV() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save CSV Report");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
                
                try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                    // Write title and timestamp
                    writer.append("HAPA Vehicle Rental System - Vehicles Report\n");
                    writer.append("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n");
                    
                    // Write headers
                    writer.append("Plate Number,Model,Category,Fuel Type,Transmission,Seats,Price per Day,Status\n");
                    
                    // Write data
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 1; j < model.getColumnCount() - 1; j++) { // Skip ID and Image
                            String cellValue = model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString().replace(",", ";") : "";
                            writer.append(cellValue);
                            if (j < model.getColumnCount() - 2) writer.append(",");
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
    
    private void bulkDelete() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select vehicles to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete " + rows.length + " vehicles?", "Confirm Bulk Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int deleted = 0;
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (vehicleDAO.deleteVehicle(id)) deleted++;
            }
            JOptionPane.showMessageDialog(this, "Deleted " + deleted + " vehicles");
            loadAll();
        }
    }
    
    private void viewVehicleImage() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        
        int modelIndex = table.convertRowIndexToModel(sel);
        String imagePath = model.getValueAt(modelIndex, 9).toString(); // Image column
        
        if (imagePath == null || imagePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No image available");
            return;
        }
        
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            if (icon.getIconWidth() > 0) {
                // Scale image to fit dialog
                Image img = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                JOptionPane.showMessageDialog(this, new ImageIcon(img), "Vehicle Image", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Image not found: " + imagePath);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
        }
    }
    
    private void toggleMaintenance() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        
        int modelIndex = table.convertRowIndexToModel(sel);
        int vehicleId = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
        String currentStatus = model.getValueAt(modelIndex, 8).toString(); // Status column
        
        String newStatus = currentStatus.equals("Maintenance") ? "Available" : "Maintenance";
        
        // Save status to database
        boolean success = vehicleDAO.updateVehicleStatus(vehicleId, newStatus);
        
        if (success) {
            model.setValueAt(newStatus, modelIndex, 8);
            JOptionPane.showMessageDialog(this, "Status changed to: " + newStatus);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status in database!");
        }
    }
    
    // Custom renderer for alternating row colors
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
                        case "Available":
                            c.setBackground(new Color(220, 255, 220));
                            break;
                        case "Rented":
                            c.setBackground(new Color(255, 255, 220));
                            break;
                        case "Maintenance":
                            c.setBackground(new Color(255, 220, 220));
                            break;
                    }
                }
            }
            
            return c;
        }
    }
}
