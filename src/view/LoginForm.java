/*
 * HAPA Vehicle Rental System - Login Form
 * This is the main entry point for user authentication
 * Supports both customer and staff login with role-based access control
 * 
 * Features:
 * - Placeholder text for better UX
 * - Logo display with proper scaling
 * - Role-based authentication (Customer/Staff)
 * - Input validation and error handling
 * - Navigation to signup form
 */
package view;

/**
 * LoginForm - Main authentication interface for HAPA Vehicle Rental System
 * Provides secure login functionality for both customers and staff members
 * 
 * @author Pacifique Harerimana
 */

import dao.UserDAO;
import dao.UserDAOImpl;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import model.User;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class LoginForm extends javax.swing.JFrame {

    // DAO instance for user authentication operations
    private UserDAO userDAO = new UserDAOImpl();
    

    
    /**
     * Creates new form LoginForm
     * Initializes the login interface with proper styling and placeholder text
     */
    
    /**
     * Adds placeholder text functionality to text fields
     * Shows gray placeholder text when field is empty, removes it when focused
     * 
     * @param field The text field to add placeholder to
     * @param placeholder The placeholder text to display
     */
    private void addPlaceholder(javax.swing.JTextField field, String placeholder) {
    field.setText(placeholder);
    field.setForeground(new java.awt.Color(153,153,153));

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (field.getText().equals(placeholder)) {
                field.setText("");
                field.setForeground(new java.awt.Color(0,0,0));
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getText().isEmpty()) {
                field.setText(placeholder);
                field.setForeground(new java.awt.Color(153,153,153));
            }
        }
    });
}

    
    /**
     * Adds placeholder text functionality to password fields
     * Handles password masking properly - shows placeholder as plain text,
     * switches to masked input when user starts typing
     * 
     * @param field The password field to add placeholder to
     * @param placeholder The placeholder text to display
     */
    private void addPlaceholderPassword(javax.swing.JPasswordField field, String placeholder) {
    field.setEchoChar((char) 0); // show text normally when placeholder
    field.setText(placeholder);
    field.setForeground(new java.awt.Color(153,153,153));

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            String pass = new String(field.getPassword());
            if (pass.equals(placeholder)) {
                field.setText("");
                field.setEchoChar('•'); // restore password hiding
                field.setForeground(new java.awt.Color(0,0,0));
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            String pass = new String(field.getPassword());
            if (pass.isEmpty()) {
                field.setText(placeholder);
                field.setEchoChar((char) 0);
                field.setForeground(new java.awt.Color(153,153,153));
            }
        }
    });
}

    
    /**
     * Constructor - Initializes the login form
     * Sets up the UI components, loads logo, and configures placeholder text
     */
    public LoginForm() {
        initComponents();

        setIconImage(new ImageIcon("images/logo.png").getImage());

        // Configure image label properties (override NetBeans auto-generated constraints)
        imageLabel.setMaximumSize(null); // override the 0,0 limit
        imageLabel.setPreferredSize(new java.awt.Dimension(120, 120));
        imageLabel.setMinimumSize(new java.awt.Dimension(120, 120));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Load company logo and scale it to fit the label dimensions
        ImageIcon logo = new ImageIcon("images/logo.png");
        Image img = logo.getImage().getScaledInstance(120, 43, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        // End of image configuration

        // Add placeholder text to input fields for better user experience
        addPlaceholder(txtUsername, "   Enter Username");
        addPlaceholderPassword(txtPassword, "    Enter Password");
        
        // CENTER THE FRAME
        setLocationRelativeTo(null);
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jColorChooser1 = new javax.swing.JColorChooser();
        jColorChooser2 = new javax.swing.JColorChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        chkStaff = new javax.swing.JCheckBox();
        btnLogin = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();
        lblSignup = new javax.swing.JLabel();
        imageLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Perpetua", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("H A P A");

        jLabel2.setFont(new java.awt.Font("Arial Black", 1, 20)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Sign In");

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("To access HAPA Vehicle Rental System");

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setText("Don't have a HAPA account?");

        txtUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtUsername.setCaretColor(new java.awt.Color(34, 109, 180));

        chkStaff.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        chkStaff.setText(" I'm a staff.");
        chkStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkStaffActionPerformed(evt);
            }
        });

        btnLogin.setBackground(new java.awt.Color(255, 217, 102));
        btnLogin.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        btnLogin.setText("LOGIN");
        btnLogin.setAlignmentX(0.5F);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblSignup.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        lblSignup.setForeground(new java.awt.Color(34, 109, 180));
        lblSignup.setText("Sign Up");
        lblSignup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSignup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSignupMouseClicked(evt);
            }
        });

        imageLabel.setAlignmentX(0.5F);
        imageLabel.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkStaff)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSignup))
                            .addComponent(txtUsername)
                            .addComponent(txtPassword)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(btnLogin)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(13, 13, 13)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogin)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblSignup))
                .addGap(42, 42, 42))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Event handler for staff checkbox
     * Currently no specific action needed when checkbox state changes
     */
    private void chkStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkStaffActionPerformed
        // No action required - checkbox state is checked during login
    }//GEN-LAST:event_chkStaffActionPerformed

    /**
     * Event handler for login button click
     * Performs user authentication and role-based navigation
     * Validates input, authenticates user, and redirects to appropriate dashboard
     */
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // Extract user input from form fields
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        boolean isStaff = chkStaff.isSelected();
        
        // Clear placeholder text if user didn't enter real values
        if (username.equals("   Enter Username")) username = "";
        if (password.equals("    Enter Password")) password = "";
        
        // Validate that both username and password are provided
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Attempt to authenticate user with provided credentials
        User user = userDAO.login(username, password);
        
        if (user != null) {
            // User exists - now verify role matches the selected option
            if (isStaff && user.getRole().equals("admin")) {
                // Staff member logging in - redirect to admin dashboard
                new AdminDashboard(user).setVisible(true);
                this.dispose();
            } else if (!isStaff && user.getRole().equals("customer")) {
                // Customer logging in - redirect to customer dashboard
                new CustomerDashboard(user).setVisible(true);
                this.dispose();
            } else {
                // Role mismatch - user exists but selected wrong role
                if (isStaff && user.getRole().equals("customer")) {
                    JOptionPane.showMessageDialog(this, "You're not registered as staff", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid role selection!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Authentication failed - invalid credentials
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    /**
     * Event handler for signup label click
     * Navigates user to the registration form
     */
    private void lblSignupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSignupMouseClicked
        // Open signup form and close current login form
        new SignupForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblSignupMouseClicked

    /**
     * Main method - Application entry point
     * Sets up the look and feel and launches the login form
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String args[]) {
        /* Configure Nimbus look and feel for better UI appearance */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Launch the login form on the Event Dispatch Thread */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox chkStaff;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JColorChooser jColorChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblSignup;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}