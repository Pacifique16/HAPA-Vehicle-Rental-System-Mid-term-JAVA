/*
 * HAPA Vehicle Rental System - User Registration Form
 * Provides user registration interface for new customers
 * Features comprehensive validation, password strength checking, and placeholder text
 * Includes duplicate prevention and proper error handling
 */
package view;

/**
 * SignupForm - User registration interface for HAPA Vehicle Rental System
 * Provides secure user registration with comprehensive validation
 * Features password strength validation, phone number validation, and duplicate prevention
 *
 * @author Pacifique Harerimana
 */

import dao.UserDAO;
import dao.UserDAOImpl;
import java.awt.Image;
import javax.swing.ImageIcon;
import model.User;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import util.PasswordValidator;

/**
 * User registration form with comprehensive validation and security features
 * Handles new customer account creation with proper input validation
 */
public class SignupForm extends javax.swing.JFrame {

    /**
     * Creates new form SignupForm
     * Initializes the registration interface with placeholder text and logo
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
    field.setEchoChar((char) 0);
    field.setText(placeholder);
    field.setForeground(new java.awt.Color(153,153,153));

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            String pass = new String(field.getPassword());
            if (pass.equals(placeholder)) {
                field.setText("");
                field.setEchoChar('•');
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
     * Constructor - Initializes the signup form
     * Sets up UI components, loads logo, and configures placeholder text for all fields
     */
    public SignupForm() {
        
        // Set application icon
        setIconImage(new ImageIcon("images/logo.png").getImage());
        initComponents();

        // Add placeholder text to all input fields for better user experience
        addPlaceholder(txtFullName, "Enter your full name");
        addPlaceholder(txtUsername, "Enter Username");
        addPlaceholder(txtPhone, "Enter Phone number");
        addPlaceholder(txtEmail, "Enter Email");
        addPlaceholderPassword(txtPassword, "Enter Password");
        addPlaceholderPassword(txtConfirmPassword, "Confirm Password");
        
        // Configure company logo display
        imageLabel.setMaximumSize(null); // override NetBeans constraints
        imageLabel.setPreferredSize(new java.awt.Dimension(120, 120));
        imageLabel.setMinimumSize(new java.awt.Dimension(120, 120));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Load and scale company logo
        ImageIcon logo = new ImageIcon("images/logo.png");
        Image img = logo.getImage().getScaledInstance(120, 43, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlelabel = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        btnSignup = new javax.swing.JButton();
        lblLogin = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        imageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        titlelabel.setFont(new java.awt.Font("Perpetua", 0, 18)); // NOI18N
        titlelabel.setText("H A P A -- Sign Up");

        txtFullName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFullNameActionPerformed(evt);
            }
        });

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        btnSignup.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnSignup.setText("SIGN UP");
        btnSignup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignupActionPerformed(evt);
            }
        });

        lblLogin.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        lblLogin.setForeground(new java.awt.Color(34, 109, 180));
        lblLogin.setText("Login");
        lblLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLoginMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Already have an account?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titlelabel)
                    .addComponent(btnSignup))
                .addGap(76, 76, 76))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(titlelabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSignup)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblLogin))
                .addGap(27, 27, 27))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Event handler for full name field action (Enter key pressed)
     * Currently no specific action needed
     */
    private void txtFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullNameActionPerformed
        // No action required - field is for input only
    }//GEN-LAST:event_txtFullNameActionPerformed

    /**
     * Event handler for email field action (Enter key pressed)
     * Currently no specific action needed
     */
    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // No action required - field is for input only
    }//GEN-LAST:event_txtEmailActionPerformed

    /**
     * Event handler for login label click
     * Navigates user back to the login form
     */
    private void lblLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLoginMouseClicked
        // Navigate to login form and close signup form
        new LoginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblLoginMouseClicked

    /**
     * Event handler for signup button click
     * Performs comprehensive validation and creates new user account
     * Includes password strength validation, phone number validation, and duplicate prevention
     */
    private void btnSignupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignupActionPerformed
        // Extract user input from form fields
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

    // Clear placeholder text if user didn't enter real values
    if (fullName.equals("Enter your full name")) fullName = "";
    if (username.equals("Enter Username")) username = "";
    if (phone.equals("Enter Phone number")) phone = "";
    if (email.equals("Enter Email")) email = "";
    if (password.equals("Enter Password")) password = "";
    if (confirm.equals("Confirm Password")) confirm = "";

    // Comprehensive input validation
    
    // Check that all required fields are filled
    if (fullName.isEmpty() || username.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Verify password confirmation matches
    if (!password.equals(confirm)) {
        JOptionPane.showMessageDialog(this, "Passwords do NOT match!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validate password strength using PasswordValidator utility
    if (!PasswordValidator.isValidPassword(password)) {
        JOptionPane.showMessageDialog(this, PasswordValidator.getPasswordRequirements(), "Invalid Password", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validate Rwandan phone number format (07xxxxxxxx)
    if (!phone.matches("07\\d{8}")) {
        JOptionPane.showMessageDialog(this, "Invalid Rwandan phone number!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Basic email format validation
    if (!email.contains("@") || !email.contains(".")) {
        JOptionPane.showMessageDialog(this, "Invalid Email format!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create and save new user account
    User u = new User();
    u.setFullName(fullName);
    u.setUsername(username);
    u.setPhone(phone);
    u.setEmail(email);
    u.setPassword(password);
    u.setRole("customer");  // All signups are customers by default

    // Attempt to save user to database
    UserDAO dao = new UserDAOImpl();
    boolean success = dao.addUser(u);

    if (success) {
        // Registration successful - redirect to login
        JOptionPane.showMessageDialog(this, "Account created successfully!");
        new LoginForm().setVisible(true);
        this.dispose();
    } else {
        // Registration failed - likely duplicate username/email
        JOptionPane.showMessageDialog(this, "Failed to register! Username or Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_btnSignupActionPerformed

    /**
     * Event handler for username field action (Enter key pressed)
     * Currently no specific action needed
     */
    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // No action required - field is for input only
    }//GEN-LAST:event_txtUsernameActionPerformed

    /**
     * Main method - Application entry point for signup form
     * Sets up look and feel and launches the registration form
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
            java.util.logging.Logger.getLogger(SignupForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignupForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignupForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignupForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Launch the signup form on the Event Dispatch Thread */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignupForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSignup;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel titlelabel;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}