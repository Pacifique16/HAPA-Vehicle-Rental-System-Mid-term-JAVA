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

import dao.UserDAO;
import dao.UserDAOImpl;
import model.User;
import util.PasswordValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Enhanced Manage Users Panel with search, filtering, validation, and modern UI
 */
public class ManageUsersPanel extends JPanel {

    private final UserDAO userDAO = new UserDAOImpl();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JComboBox<String> roleFilter, statusFilter;

    public ManageUsersPanel() {
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
        tfSearch.setToolTipText("Search name, phone, email, username, role, status...");
        tfSearch.addActionListener(e -> doSearch());

        // Filter components
        roleFilter = new JComboBox<>(new String[]{"All Roles", "customer", "admin"});
        statusFilter = new JComboBox<>(new String[]{"All Status", "Active", "Inactive"});
        
        roleFilter.addActionListener(e -> doAdvancedSearch());
        statusFilter.addActionListener(e -> doAdvancedSearch());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(tfSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Role:"));
        searchPanel.add(roleFilter);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton bAdd = new JButton("Add User");
        JButton bExport = new JButton("Export CSV");
        JButton bBulkDelete = new JButton("Bulk Delete");
        
        bAdd.addActionListener(e -> openAdd());
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
        String[] cols = new String[]{"ID","Full Name","Phone","Email","Username","Role","Status"};
        model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Alternating row colors with role-based coloring
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPopupMenu pm = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("Edit");
        JMenuItem miDelete = new JMenuItem("Delete");
        JMenuItem miResetPassword = new JMenuItem("Reset Password");
        JMenuItem miToggleStatus = new JMenuItem("Toggle Status");
        
        miEdit.addActionListener(e -> openEdit());
        miDelete.addActionListener(e -> doDelete());
        miResetPassword.addActionListener(e -> resetPassword());
        miToggleStatus.addActionListener(e -> toggleUserStatus());
        
        pm.add(miEdit); 
        pm.add(miDelete);
        pm.add(new JSeparator());
        pm.add(miResetPassword);
        pm.add(miToggleStatus);
        table.setComponentPopupMenu(pm);
    }

    private void loadAll(){
        model.setRowCount(0);
        try {
            List<User> list = userDAO.getAllUsers();
            for (User u : list) {
                String status = getUserStatus(u);
                model.addRow(new Object[]{
                    u.getId(), 
                    u.getFullName(), 
                    u.getPhone(), 
                    u.getEmail(), 
                    u.getUsername(), 
                    u.getRole(),
                    status
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private String getUserStatus(User user) {
        return user.getStatus() != null ? user.getStatus() : "Active";
    }

    private void doSearch(){
        String q = tfSearch.getText().trim().toLowerCase();
        model.setRowCount(0);
        try {
            List<User> allUsers = userDAO.getAllUsers();
            for (User u : allUsers) {
                String status = getUserStatus(u);
                
                // Check if search query matches any field
                if (q.isEmpty() || matchesSearch(u, q)) {
                    model.addRow(new Object[]{
                        u.getId(), 
                        u.getFullName(), 
                        u.getPhone(), 
                        u.getEmail(), 
                        u.getUsername(), 
                        u.getRole(),
                        status
                    });
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private boolean matchesSearch(User u, String query) {
        String status = getUserStatus(u);
        return (u.getFullName() != null && u.getFullName().toLowerCase().contains(query)) ||
               (u.getPhone() != null && u.getPhone().toLowerCase().contains(query)) ||
               (u.getEmail() != null && u.getEmail().toLowerCase().contains(query)) ||
               (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) ||
               (u.getRole() != null && u.getRole().toLowerCase().contains(query)) ||
               (status != null && status.toLowerCase().contains(query));
    }

    private void openAdd(){
        UserFormDialog dlg = new UserFormDialog(null);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadAll();
    }

    private void openEdit(){
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Select a user"); return; }
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        try {
            User u = userDAO.findById(id);
            UserFormDialog dlg = new UserFormDialog(u);
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
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = userDAO.deleteUser(id);
        if (ok) { JOptionPane.showMessageDialog(this, "Deleted."); loadAll(); }
        else JOptionPane.showMessageDialog(this, "Failed to delete.");
    }

    private void styleRounded(JComponent c){
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }

    // Enhanced user add/edit dialog
    private class UserFormDialog extends JDialog {
        private boolean saved = false;
        private JTextField tfName, tfPhone, tfEmail, tfUsername;
        private JPasswordField pfPassword, pfConfirmPassword;
        private JComboBox<String> cbRole;
        private User editing;

        UserFormDialog(User u) {
            super((Frame) SwingUtilities.getWindowAncestor(ManageUsersPanel.this), true);
            this.editing = u;
            setTitle(u==null? "Add User" : "Edit User");
            setSize(500,400);
            setLayout(new BorderLayout(8,8));
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            GridBagConstraints c = new GridBagConstraints(); 
            c.insets = new Insets(6,6,6,6); 
            c.anchor = GridBagConstraints.WEST;

            // Initialize components with proper sizing
            tfName = new JTextField(u==null?"":u.getFullName());
            tfName.setPreferredSize(new Dimension(250, 25));
            
            tfPhone = new JTextField(u==null?"":u.getPhone());
            tfPhone.setPreferredSize(new Dimension(250, 25));
            
            tfEmail = new JTextField(u==null?"":u.getEmail());
            tfEmail.setPreferredSize(new Dimension(250, 25));
            
            tfUsername = new JTextField(u==null?"":u.getUsername());
            tfUsername.setPreferredSize(new Dimension(250, 25));
            
            pfPassword = new JPasswordField();
            pfPassword.setPreferredSize(new Dimension(250, 25));
            
            pfConfirmPassword = new JPasswordField();
            pfConfirmPassword.setPreferredSize(new Dimension(250, 25));
            
            cbRole = new JComboBox<>(new String[]{"customer", "admin"});
            cbRole.setPreferredSize(new Dimension(250, 25));
            if (u != null) cbRole.setSelectedItem(u.getRole());

            // Layout components
            c.gridx=0; c.gridy=0; p.add(new JLabel("Full Name *:"), c); c.gridx=1; p.add(tfName, c);
            c.gridx=0; c.gridy=1; p.add(new JLabel("Phone *:"), c); c.gridx=1; p.add(tfPhone, c);
            c.gridx=0; c.gridy=2; p.add(new JLabel("Email *:"), c); c.gridx=1; p.add(tfEmail, c);
            c.gridx=0; c.gridy=3; p.add(new JLabel("Username *:"), c); c.gridx=1; p.add(tfUsername, c);
            c.gridx=0; c.gridy=4; p.add(new JLabel("Password *:"), c); c.gridx=1; p.add(pfPassword, c);
            c.gridx=0; c.gridy=5; p.add(new JLabel("Confirm Password *:"), c); c.gridx=1; p.add(pfConfirmPassword, c);
            c.gridx=0; c.gridy=6; p.add(new JLabel("Role *:"), c); c.gridx=1; p.add(cbRole, c);

            add(p, BorderLayout.CENTER);
            JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton save = new JButton("Save"); 
            JButton cancel = new JButton("Cancel");
            b.add(cancel); b.add(save);
            add(b, BorderLayout.SOUTH);

            save.addActionListener(ae -> {
                if (validateInput()) {
                    try {
                        User uu = editing==null ? new User() : editing;
                        uu.setFullName(tfName.getText().trim());
                        uu.setPhone(tfPhone.getText().trim());
                        uu.setEmail(tfEmail.getText().trim().toLowerCase());
                        uu.setUsername(tfUsername.getText().trim().toLowerCase());
                        
                        String password = new String(pfPassword.getPassword());
                        if (!password.isEmpty()) {
                            uu.setPassword(password);
                        }
                        
                        uu.setRole(cbRole.getSelectedItem().toString());

                        // Check for duplicates
                        if (editing == null && isDuplicateUser(uu.getUsername(), uu.getEmail())) {
                            return;
                        }

                        boolean ok;
                        if (editing==null) ok = userDAO.addUser(uu);
                        else ok = userDAO.updateUser(uu);

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
        
        private boolean validateInput() {
            // Required fields validation
            if (tfName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full name is required!");
                tfName.requestFocus();
                return false;
            }
            
            if (tfPhone.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone is required!");
                tfPhone.requestFocus();
                return false;
            }
            
            if (tfEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required!");
                tfEmail.requestFocus();
                return false;
            }
            
            if (tfUsername.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required!");
                tfUsername.requestFocus();
                return false;
            }
            
            // Phone validation (Rwandan format)
            String phone = tfPhone.getText().trim();
            if (!phone.matches("07\\d{8}")) {
                JOptionPane.showMessageDialog(this, "Invalid Rwandan phone number format! Use 07XXXXXXXX");
                tfPhone.requestFocus();
                return false;
            }
            
            // Email validation
            String email = tfEmail.getText().trim();
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format!");
                tfEmail.requestFocus();
                return false;
            }
            
            // Password validation (only for new users or if password is provided)
            String password = new String(pfPassword.getPassword());
            String confirmPassword = new String(pfConfirmPassword.getPassword());
            
            if (editing == null || !password.isEmpty()) {
                if (!PasswordValidator.isValidPassword(password)) {
                    JOptionPane.showMessageDialog(this, PasswordValidator.getPasswordRequirements(), "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    pfPassword.requestFocus();
                    return false;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!");
                    pfConfirmPassword.requestFocus();
                    return false;
                }
            }
            
            return true;
        }
        
        private boolean isValidEmail(String email) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches();
        }
        
        private boolean isDuplicateUser(String username, String email) {
            try {
                List<User> users = userDAO.getAllUsers();
                for (User u : users) {
                    if (u.getUsername().equalsIgnoreCase(username)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!");
                        return true;
                    }
                    if (u.getEmail().equalsIgnoreCase(email)) {
                        JOptionPane.showMessageDialog(this, "Email already exists!");
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        
        public boolean isSaved(){ return saved; }
    }
    
    // New enhanced functionality methods
    private void doAdvancedSearch() {
        String searchText = tfSearch.getText().trim();
        String selectedRole = (String) roleFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        model.setRowCount(0);
        try {
            List<User> list = userDAO.getAllUsers();
            
            for (User u : list) {
                String status = getUserStatus(u);
                
                // Apply search filter
                if (!searchText.isEmpty() && !matchesSearch(u, searchText.toLowerCase())) {
                    continue;
                }
                
                // Apply role filter
                if (!selectedRole.equals("All Roles") && !u.getRole().equals(selectedRole)) {
                    continue;
                }
                
                // Apply status filter
                if (!selectedStatus.equals("All Status") && !status.equals(selectedStatus)) {
                    continue;
                }
                
                model.addRow(new Object[]{
                    u.getId(), 
                    u.getFullName(), 
                    u.getPhone(), 
                    u.getEmail(), 
                    u.getUsername(), 
                    u.getRole(),
                    status
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
                    writer.append("HAPA Vehicle Rental System - Users Report\n");
                    writer.append("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n");
                    
                    // Write headers
                    writer.append("Full Name,Phone,Email,Username,Role,Status\n");
                    
                    // Write data
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 1; j < model.getColumnCount(); j++) { // Skip ID
                            String cellValue = model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString().replace(",", ";") : "";
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
    
    private void bulkDelete() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select users to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete " + rows.length + " users?", "Confirm Bulk Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int deleted = 0;
            for (int row : rows) {
                int modelIndex = table.convertRowIndexToModel(row);
                int id = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
                if (userDAO.deleteUser(id)) deleted++;
            }
            JOptionPane.showMessageDialog(this, "Deleted " + deleted + " users");
            loadAll();
        }
    }
    

    
    private void resetPassword() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:\n" + PasswordValidator.getPasswordRequirements());
        if (newPassword == null || !PasswordValidator.isValidPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, PasswordValidator.getPasswordRequirements(), "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int modelIndex = table.convertRowIndexToModel(sel);
        int userId = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
        
        try {
            User user = userDAO.findById(userId);
            user.setPassword(newPassword);
            boolean success = userDAO.updateUser(user);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void toggleUserStatus() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        
        int modelIndex = table.convertRowIndexToModel(sel);
        int userId = Integer.parseInt(model.getValueAt(modelIndex, 0).toString());
        String currentStatus = model.getValueAt(modelIndex, 6).toString();
        String newStatus = currentStatus.equals("Active") ? "Inactive" : "Active";
        
        // Update in database
        boolean success = userDAO.updateUserStatus(userId, newStatus);
        
        if (success) {
            model.setValueAt(newStatus, modelIndex, 6);
            JOptionPane.showMessageDialog(this, "User status changed to: " + newStatus);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user status in database!");
        }
    }
    
    // Custom renderer for alternating row colors and role-based coloring
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
                
                // Role-based coloring
                if (column == 5 && value != null) { // Role column
                    String role = value.toString();
                    switch (role.toLowerCase()) {
                        case "admin":
                            c.setBackground(new Color(255, 240, 240)); // Light red
                            break;
                        case "customer":
                            c.setBackground(new Color(240, 255, 240)); // Light green
                            break;
                    }
                }
                
                // Status-based coloring
                if (column == 6 && value != null) { // Status column
                    String status = value.toString();
                    switch (status) {
                        case "Active":
                            c.setBackground(new Color(220, 255, 220));
                            break;
                        case "Inactive":
                            c.setBackground(new Color(255, 220, 220));
                            break;
                    }
                }
            }
            
            return c;
        }
    }
}