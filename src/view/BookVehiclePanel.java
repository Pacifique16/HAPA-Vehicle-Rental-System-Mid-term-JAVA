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
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookVehiclePanel extends JPanel {

    private User loggedInUser;
    private JPanel panelVehicles;
    private JScrollPane scrollPanel;

    public BookVehiclePanel(User user) {
        this.loggedInUser = user;
        initComponents();
        setupWrapFix();
        loadVehicles();
    }

    public BookVehiclePanel() {
        initComponents();
        setupWrapFix();
        loadVehicles();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        panelVehicles = new JPanel();
        panelVehicles.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        panelVehicles.setBackground(Color.WHITE);

        scrollPanel = new JScrollPane(panelVehicles);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPanel.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);   // <-- VERY IMPORTANT

        add(scrollPanel, BorderLayout.CENTER);
    }


    /**
     * Fix for wrapping layout: forces panelVehicles width to always match viewport width.
     */
        private void setupWrapFix() {
            scrollPanel.getViewport().addChangeListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    int vpWidth = scrollPanel.getViewport().getWidth();
                    if (vpWidth <= 0) return;

                    panelVehicles.setPreferredSize(null);    // Let wrap layout decide size
                    panelVehicles.revalidate();
                });
            });
        }



    private void loadVehicles() {
        VehicleDAO dao = new VehicleDAOImpl();
        List<Vehicle> vehicles = dao.getAllVehicles();

        panelVehicles.removeAll();

        for (Vehicle v : vehicles) {
            // Only show vehicles that are not in maintenance
            if (!"Maintenance".equals(v.getStatus())) {
                panelVehicles.add(createVehicleCard(v));
            }
        }

        SwingUtilities.invokeLater(() -> {
            // Force height to auto-calculate again
            panelVehicles.setPreferredSize(null);

            panelVehicles.revalidate();
            panelVehicles.repaint();
        });

    }

    private JPanel createVehicleCard(Vehicle v) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(245, 265));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(220, 140));

        try {
            ImageIcon icon = new ImageIcon(v.getImagePath());
            Image scaled = icon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("No Image");
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblModel = new JLabel(v.getModel());
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblModel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrice = new JLabel(v.getPricePerDay() + " RWF / day");
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(new Color(34, 109, 180));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBook = new JButton("BOOK NOW");
        btnBook.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBook.setBackground(new Color(255, 217, 102));
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnBook.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBook.setBackground(new Color(240, 200, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBook.setBackground(new Color(255, 217, 102));
            }
        });

        // inside BookVehiclePanel when creating the 'BOOK NOW' button:
        btnBook.addActionListener(evt -> new BookingForm(loggedInUser, v).setVisible(true));

        infoPanel.add(lblModel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblPrice);

        JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        iconRow.setBackground(Color.WHITE);

        // Load icons and create labels with icons
        ImageIcon fuelIcon = new ImageIcon(new ImageIcon("images/fuel.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        ImageIcon gearIcon = new ImageIcon(new ImageIcon("images/gear.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        ImageIcon seatIcon = new ImageIcon(new ImageIcon("images/seat.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        
        JLabel lblFuel = new JLabel(v.getFuelType(), fuelIcon, JLabel.LEFT);
        JLabel lblTrans = new JLabel(v.getTransmission(), gearIcon, JLabel.LEFT);
        JLabel lblSeats = new JLabel(String.valueOf(v.getSeats()), seatIcon, JLabel.LEFT);
        
        lblFuel.setIconTextGap(5);
        lblTrans.setIconTextGap(5);
        lblSeats.setIconTextGap(5);

        lblFuel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrans.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSeats.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        iconRow.add(lblFuel);
        iconRow.add(lblTrans);
        iconRow.add(lblSeats);

        infoPanel.add(iconRow);

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(btnBook);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }
}
