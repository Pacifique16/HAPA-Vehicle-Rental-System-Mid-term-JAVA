/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javax.swing.JPanel;

/**
 *
 * @author Pacifique Harerimana
 */



import model.User;
import dao.UserDAO;
import dao.UserDAOImpl;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final User user;

    private JButton btnProfileTab;
    private JButton btnPasswordTab;

    private JPanel cardsPanel;
    private JPanel profilePanel;
    private JPanel passwordPanel;

    public SettingsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {

        // -------------------------- TOP TABS --------------------------
        JPanel tabs = new JPanel(new GridLayout(1, 2));
        tabs.setBackground(new Color(199, 224, 199));
        tabs.setPreferredSize(new Dimension(100, 60)); // <-- Taller tabs

        btnProfileTab = new JButton("Profile");
        btnPasswordTab = new JButton("Change Password");

        styleTab(btnProfileTab, true);
        styleTab(btnPasswordTab, false);

        btnProfileTab.addActionListener(e -> showCard("profile"));
        btnPasswordTab.addActionListener(e -> showCard("password"));

        tabs.add(btnProfileTab);
        tabs.add(btnPasswordTab);

        add(tabs, BorderLayout.NORTH);


        // -------------------------- CARD AREA --------------------------
        cardsPanel = new JPanel(new CardLayout());
        cardsPanel.setBackground(new Color(234, 242, 250));

        buildProfilePanel();
        buildPasswordPanel();

        cardsPanel.add(profilePanel, "profile");
        cardsPanel.add(passwordPanel, "password");

        add(cardsPanel, BorderLayout.CENTER);

        showCard("profile");
    }


    private void styleTab(JButton b, boolean selected) {
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16)); // bigger text
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(100, 60));      // taller
        b.setBorder(BorderFactory.createEmptyBorder());

        if (selected) b.setBackground(Color.WHITE);
        else b.setBackground(new Color(199, 224, 199));
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) cardsPanel.getLayout();
        cl.show(cardsPanel, name);

        styleTab(btnProfileTab, name.equals("profile"));
        styleTab(btnPasswordTab, name.equals("password"));
    }


    // ============================================================
    // PROFILE PANEL
    // ============================================================
    private void buildProfilePanel() {
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(new Color(234, 242, 250));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(14, 14, 14, 14);
        c.anchor = GridBagConstraints.WEST;

        JLabel lName  = label("NAME:");
        JLabel lTel   = label("Tel:");
        JLabel lEmail = label("Email:");
        JLabel lUser  = label("Username:");

        JTextField tfName  = roundedField(user.getFullName());
        JTextField tfTel   = roundedField(user.getPhone());
        JTextField tfEmail = roundedField(user.getEmail());
        JTextField tfUser  = roundedField(user.getUsername());

        // Allow edit (admin or customer)
        tfName.setEditable(true);
        tfTel.setEditable(true);
        tfEmail.setEditable(true);
        tfUser.setEditable(false); // usually username should not change

        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(255, 217, 102));
        btnSave.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            try {
                UserDAO dao = new UserDAOImpl();

                user.setFullName(tfName.getText());
                user.setPhone(tfTel.getText());
                user.setEmail(tfEmail.getText());

                boolean ok = dao.updateUserProfile(user);

                if (ok)
                    JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                else
                    JOptionPane.showMessageDialog(this, "Failed to update profile.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating profile.");
            }
        });

        // Layout rows
        c.gridx = 0; c.gridy = 0; profilePanel.add(lName, c);
        c.gridx = 1; profilePanel.add(tfName, c);

        c.gridx = 0; c.gridy = 1; profilePanel.add(lTel, c);
        c.gridx = 1; profilePanel.add(tfTel, c);

        c.gridx = 0; c.gridy = 2; profilePanel.add(lEmail, c);
        c.gridx = 1; profilePanel.add(tfEmail, c);

        c.gridx = 0; c.gridy = 3; profilePanel.add(lUser, c);
        c.gridx = 1; profilePanel.add(tfUser, c);

        c.gridx = 1; c.gridy = 4; profilePanel.add(btnSave, c);
    }


    // ============================================================
    // PASSWORD PANEL
    // ============================================================
    private void buildPasswordPanel() {
        passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(new Color(234, 242, 250));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(14, 14, 14, 14);
        c.anchor = GridBagConstraints.WEST;

        JLabel lOld     = label("Old Password:");
        JLabel lNew     = label("New Password:");
        JLabel lConfirm = label("Confirm New:");

        JPasswordField tfOld     = roundedPassword();
        JPasswordField tfNew     = roundedPassword();
        JPasswordField tfConfirm = roundedPassword();

        JButton btnSave = new JButton("Save Password");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(255, 217, 102));
        btnSave.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            String oldP = new String(tfOld.getPassword());
            String newP = new String(tfNew.getPassword());
            String conf = new String(tfConfirm.getPassword());

            if (oldP.isEmpty() || newP.isEmpty() || conf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            if (!newP.equals(conf)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.");
                return;
            }

            UserDAO dao = new UserDAOImpl();
            boolean ok = dao.changePassword(user.getId(), oldP, newP);

            if (ok)
                JOptionPane.showMessageDialog(this, "Password changed successfully.");
            else
                JOptionPane.showMessageDialog(this, "Incorrect old password.");
        });

        // layout
        c.gridx = 0; c.gridy = 0; passwordPanel.add(lOld, c);
        c.gridx = 1; passwordPanel.add(tfOld, c);

        c.gridx = 0; c.gridy = 1; passwordPanel.add(lNew, c);
        c.gridx = 1; passwordPanel.add(tfNew, c);

        c.gridx = 0; c.gridy = 2; passwordPanel.add(lConfirm, c);
        c.gridx = 1; passwordPanel.add(tfConfirm, c);

        c.gridx = 1; c.gridy = 3; passwordPanel.add(btnSave, c);
    }


    // ============================================================
    // HELPERS (styled label & rounded text fields)
    // ============================================================
    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return l;
    }

    private JTextField roundedField(String value) {
        JTextField tf = new JTextField(value, 18);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setPreferredSize(new Dimension(240, 38)); // <-- bigger height
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JPasswordField roundedPassword() {
        JPasswordField tf = new JPasswordField(18);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setPreferredSize(new Dimension(240, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }
}
