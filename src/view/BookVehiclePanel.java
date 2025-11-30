/*
 * HAPA Vehicle Rental System - Vehicle Booking Panel
 * Customer interface for browsing and booking available vehicles
 * Features responsive card layout, vehicle filtering, and integrated booking workflow
 * Displays vehicle information with images, specifications, and pricing
 */
package view;

/**
 * BookVehiclePanel - Customer panel for browsing and booking vehicles
 * Provides an intuitive interface for customers to:
 * - Browse available vehicles in a responsive card layout
 * - View vehicle details including images, specifications, and pricing
 * - Access booking functionality directly from vehicle cards
 * - Filter vehicles by availability status
 * - Navigate through vehicle options with smooth scrolling
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

    /** Currently logged in customer user */
    private User loggedInUser;
    
    /** Panel containing vehicle cards in wrap layout */
    private JPanel panelVehicles;
    
    /** Scrollable container for vehicle panel */
    private JScrollPane scrollPanel;

    /**
     * Constructor with user context - Creates BookVehiclePanel for specific customer
     * Initializes the panel with user context for booking functionality
     * 
     * @param user The customer user who will be booking vehicles
     */
    public BookVehiclePanel(User user) {
        this.loggedInUser = user;    // Store user context for booking
        initComponents();            // Initialize UI components
        setupWrapFix();             // Configure responsive layout
        loadVehicles();             // Load available vehicles
    }

    /**
     * Default constructor - Creates BookVehiclePanel without user context
     * Used for preview or administrative purposes where booking is not required
     */
    public BookVehiclePanel() {
        initComponents();    // Initialize UI components
        setupWrapFix();     // Configure responsive layout
        loadVehicles();     // Load available vehicles
    }

    /**
     * Initializes the UI components and layout
     * Sets up the main panel structure with scrollable vehicle grid
     */
    private void initComponents() {
        setLayout(new BorderLayout());                              // Main panel layout
        setBackground(new Color(222, 235, 247));                   // Light blue background

        // Create vehicle container with wrap layout for responsive design
        panelVehicles = new JPanel();
        panelVehicles.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));  // Cards wrap to new lines
        panelVehicles.setBackground(new Color(222, 235, 247));

        // Create scrollable container for vehicle panel
        scrollPanel = new JScrollPane(panelVehicles);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Optimize scrolling performance for large number of vehicle cards
        scrollPanel.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        add(scrollPanel, BorderLayout.CENTER);                     // Add to main panel
    }


    /**
     * Configures responsive layout behavior for vehicle cards
     * Ensures proper wrapping when viewport size changes
     * Critical for maintaining responsive design across different screen sizes
     */
    private void setupWrapFix() {
        // Listen for viewport size changes to trigger layout updates
        scrollPanel.getViewport().addChangeListener(e -> {
            SwingUtilities.invokeLater(() -> {
                int vpWidth = scrollPanel.getViewport().getWidth();
                if (vpWidth <= 0) return;    // Skip invalid viewport sizes

                // Reset preferred size to allow wrap layout to recalculate
                panelVehicles.setPreferredSize(null);
                panelVehicles.revalidate();   // Trigger layout recalculation
            });
        });
    }



    /**
     * Loads available vehicles from database and creates display cards
     * Filters out vehicles in maintenance status to show only bookable vehicles
     */
    private void loadVehicles() {
        VehicleDAO dao = new VehicleDAOImpl();
        List<Vehicle> vehicles = dao.getAllVehicles();    // Get all vehicles from database

        panelVehicles.removeAll();    // Clear existing vehicle cards

        // Create cards for available vehicles only
        for (Vehicle v : vehicles) {
            // Only show vehicles that are not in maintenance
            if (!"Maintenance".equals(v.getStatus())) {
                panelVehicles.add(createVehicleCard(v));
            }
        }

        // Refresh layout after adding all cards
        SwingUtilities.invokeLater(() -> {
            panelVehicles.setPreferredSize(null);    // Reset size for auto-calculation
            panelVehicles.revalidate();              // Recalculate layout
            panelVehicles.repaint();                 // Refresh display
        });
    }

    /**
     * Creates a visual card component for displaying vehicle information
     * Each card contains vehicle image, specifications, pricing, and booking button
     * 
     * @param v The vehicle object to create a card for
     * @return JPanel containing the formatted vehicle card
     */
    private JPanel createVehicleCard(Vehicle v) {
        // Create main card container
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(245, 265));    // Fixed card size for grid layout
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create vehicle image display
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(220, 140));

        // Load and scale vehicle image
        try {
            ImageIcon icon = new ImageIcon(v.getImagePath());
            Image scaled = icon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("No Image");    // Fallback for missing images
        }

        // Create vehicle information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Vehicle model label
        JLabel lblModel = new JLabel(v.getModel());
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblModel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price per day label
        JLabel lblPrice = new JLabel(v.getPricePerDay() + " RWF / day");
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(new Color(34, 109, 180));    // Blue color for price
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Book now button with hover effects
        JButton btnBook = new JButton("BOOK NOW");
        btnBook.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBook.setBackground(new Color(255, 217, 102));    // Yellow background
        btnBook.setFocusPainted(false);                     // Remove focus border
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Add hover effects for better user experience
        btnBook.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBook.setBackground(new Color(240, 200, 80));    // Darker on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBook.setBackground(new Color(255, 217, 102));   // Original color
            }
        });

        // Add booking action - opens booking form for this vehicle
        btnBook.addActionListener(evt -> new BookingForm(loggedInUser, v).setVisible(true));

        // Add model and price to info panel
        infoPanel.add(lblModel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblPrice);

        // Create vehicle specifications row with icons
        JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        iconRow.setBackground(Color.WHITE);

        // Load and scale specification icons
        ImageIcon fuelIcon = new ImageIcon(new ImageIcon("images/fuel.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        ImageIcon gearIcon = new ImageIcon(new ImageIcon("images/gear.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        ImageIcon seatIcon = new ImageIcon(new ImageIcon("images/seat.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        
        // Create specification labels with icons
        JLabel lblFuel = new JLabel(v.getFuelType(), fuelIcon, JLabel.LEFT);
        JLabel lblTrans = new JLabel(v.getTransmission(), gearIcon, JLabel.LEFT);
        JLabel lblSeats = new JLabel(String.valueOf(v.getSeats()), seatIcon, JLabel.LEFT);
        
        // Set icon spacing and font
        lblFuel.setIconTextGap(5);
        lblTrans.setIconTextGap(5);
        lblSeats.setIconTextGap(5);

        lblFuel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrans.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSeats.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Add specification labels to icon row
        iconRow.add(lblFuel);
        iconRow.add(lblTrans);
        iconRow.add(lblSeats);

        // Add specifications and booking button to info panel
        infoPanel.add(iconRow);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(btnBook);

        // Assemble final card
        card.add(imgLabel, BorderLayout.NORTH);      // Image at top
        card.add(infoPanel, BorderLayout.CENTER);    // Info at bottom

        return card;
    }
}
