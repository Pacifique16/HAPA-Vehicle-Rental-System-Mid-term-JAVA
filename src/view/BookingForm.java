/*
 * HAPA Vehicle Rental System - Vehicle Booking Form
 * Provides a modern, card-style interface for customers to book vehicles
 * Features comprehensive validation, cost calculation, and availability checking
 * Includes duplicate booking prevention and informative error messages
 */
package view;

/**
 * BookingForm - Modal dialog for vehicle booking
 * Provides an intuitive interface for customers to select dates and confirm bookings
 * Features real-time cost calculation, validation, and availability checking
 *
 * @author Pacifique Harerimana
 */

import dao.BookingDAO;
import dao.BookingDAOImpl;
import model.Booking;
import model.User;
import model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Modern card-style booking form dialog
 * Allows customers to select dates and book vehicles with comprehensive validation
 * Usage: new BookingForm(loggedInUser, vehicle).setVisible(true);
 */
public class BookingForm extends JDialog {

    // Core data objects
    private final Vehicle vehicle;    // Vehicle being booked
    private final User user;          // Customer making the booking

    // UI Components for vehicle display
    private JLabel lblImage;          // Vehicle image display
    private JLabel lblModel;          // Vehicle model name
    private JLabel lblPrice;          // Price per day display
    
    // Date selection components
    private JSpinner spStart;         // Start date picker
    private JSpinner spEnd;           // End date picker
    
    // Calculation display components
    private JLabel lblDays;           // Number of rental days
    private JLabel lblTotal;          // Total cost display
    
    // Action buttons
    private JButton btnConfirm;       // Confirm booking button
    private JButton btnCancel;        // Cancel booking button

    /**
     * Constructor - Creates a new booking form dialog
     * Initializes the form with user and vehicle information
     * 
     * @param user The customer making the booking
     * @param vehicle The vehicle to be booked
     */
    public BookingForm(User user, Vehicle vehicle) {
        super((Frame) null, "Book Vehicle", true);
        this.vehicle = vehicle;
        this.user = user;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Initializes all UI components and layouts
     * Creates a modern card-style interface with proper styling
     */
    private void initComponents() {
        // Root panel with light background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));

        // Main card panel with white background and border
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(420, 430));

        // Top section: Vehicle image
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(220, 140));
        setVehicleImage();

        card.add(lblImage, BorderLayout.NORTH);

        // Center section: Vehicle info and date selection
        JPanel center = new JPanel();
        center.setBackground(Color.WHITE);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        lblModel = new JLabel(vehicle.getModel());
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblModel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPrice = new JLabel(String.format("%,.0f RWF / day", vehicle.getPricePerDay()));
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPrice.setForeground(new Color(34, 109, 180));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(lblModel);
        center.add(Box.createVerticalStrut(2));
        center.add(lblPrice);
        center.add(Box.createVerticalStrut(2));

        // Date selection panel with side-by-side layout
        JPanel dates = new JPanel(new GridBagLayout());
        dates.setBackground(Color.WHITE);
        GridBagConstraints d = new GridBagConstraints();
        d.insets = new Insets(4, 4, 4, 4);
        d.fill = GridBagConstraints.HORIZONTAL;

        // Start date selection
        d.gridx = 0;
        d.gridy = 0;
        dates.add(new JLabel("Start Date"), d);

        spStart = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spStart.setEditor(new JSpinner.DateEditor(spStart, "yyyy-MM-dd"));
        d.gridx = 0;
        d.gridy = 1;
        dates.add(spStart, d);

        // End date selection
        d.gridx = 1;
        d.gridy = 0;
        dates.add(new JLabel("End Date"), d);

        spEnd = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spEnd.setEditor(new JSpinner.DateEditor(spEnd, "yyyy-MM-dd"));
        d.gridx = 1;
        d.gridy = 1;
        dates.add(spEnd, d);

        center.add(dates);
        center.add(Box.createVerticalStrut(2));

        // Calculated rental information display
        lblDays = new JLabel("Days: 1");
        lblDays.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDays.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTotal = new JLabel("Total: " + String.format("%,.0f RWF", vehicle.getPricePerDay()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(34, 109, 180));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(lblDays);
        center.add(Box.createVerticalStrut(25));
        center.add(lblTotal);

        card.add(center, BorderLayout.CENTER);

        // Bottom section: Action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        bottom.setBackground(Color.WHITE);

        btnConfirm = new JButton("CONFIRM BOOKING");
        btnConfirm.setBackground(new Color(255, 217, 102));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnCancel = new JButton("CANCEL");
        btnCancel.setBackground(new Color(245, 245, 245));
        btnCancel.setFocusPainted(false);

        bottom.add(btnCancel);
        bottom.add(btnConfirm);

        card.add(bottom, BorderLayout.SOUTH);

        // Add event listeners for real-time updates and actions
        spStart.addChangeListener(e -> recalc());  // Recalculate when start date changes
        spEnd.addChangeListener(e -> recalc());    // Recalculate when end date changes

        btnConfirm.addActionListener(e -> onConfirm());  // Handle booking confirmation
        btnCancel.addActionListener(e -> dispose());     // Close dialog on cancel

        // Put card into center of root
        root.add(card);

        setContentPane(root);

        // Perform initial cost calculation
        recalc();
    }

    /**
     * Sets the vehicle image in the display label
     * Falls back to vehicle model text if image cannot be loaded
     */
    private void setVehicleImage() {
        try {
            // Load and scale vehicle image
            ImageIcon ic = new ImageIcon(vehicle.getImagePath());
            Image img = ic.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            // Fallback to text display if image fails to load
            lblImage.setText(vehicle.getModel());
        }
    }

    /**
     * Recalculates rental days and total cost based on selected dates
     * Updates the display labels with calculated values
     * Handles edge cases and calculation errors gracefully
     */
    private void recalc() {
        try {
            Date s = (Date) spStart.getValue();
            Date e = (Date) spEnd.getValue();

            // Calculate days difference (normalize time component)
            long dif = e.getTime() - s.getTime();
            long days = dif / (1000L * 60 * 60 * 24) + 1;
            if (days <= 0) days = 1;  // Minimum 1 day rental

            // Calculate total cost
            double total = days * vehicle.getPricePerDay();

            // Update display labels
            lblDays.setText("Days: " + days);
            lblTotal.setText("Total: " + String.format("%,.0f RWF", total));
        } catch (Exception ex) {
            // Handle calculation errors gracefully
            lblDays.setText("Days: ?");
            lblTotal.setText("Total: ?");
        }
    }

    private void onConfirm() {
       if (user == null) {
           JOptionPane.showMessageDialog(this, 
               "You must be logged in to book.", 
               "Error", JOptionPane.ERROR_MESSAGE);
           return;
       }

       Date s = (Date) spStart.getValue();
       Date e = (Date) spEnd.getValue();
       
       
                // 0️⃣ Start date cannot be in the past
         Date today = new Date();  // system current date (with time)
      
         // normalize both to ignore time component
         long now = today.getTime() / (1000 * 60 * 60 * 24);
         long start = s.getTime() / (1000 * 60 * 60 * 24);

         if (start < now) {
             JOptionPane.showMessageDialog(this,
                     "Start date cannot be in the past.\nPlease select today or a future date.",
                     "Invalid Start Date",
                     JOptionPane.WARNING_MESSAGE);
             return;
         }


       if (e.before(s)) {
           JOptionPane.showMessageDialog(this, 
               "End date must be the same or after start date.", 
               "Invalid dates", JOptionPane.WARNING_MESSAGE);
           return;
       }

       long dif = e.getTime() - s.getTime();
       long days = dif / (1000L * 60 * 60 * 24) + 1;
       if (days <= 0) days = 1;

       double total = days * vehicle.getPricePerDay();
       BookingDAO dao = new BookingDAOImpl();
       
       // Auto-expire old bookings before checking availability
        
       dao.expireOldBookings();


       // 1️⃣ SAME USER — same vehicle and SAME exact dates
       if (dao.isDuplicateBooking(user.getId(), vehicle.getId(), s, e)) {
           JOptionPane.showMessageDialog(this,
                   "You already booked this vehicle on these exact dates.",
                   "Duplicate Booking",
                   JOptionPane.WARNING_MESSAGE);
           return;
       }

       // 2️⃣ ANY USER — date overlaps (vehicle unavailable)
       if (dao.isVehicleUnavailable(vehicle.getId(), s, e)) {
           String availabilityInfo = dao.getNextAvailableDates(vehicle.getId(), s, e);
           JOptionPane.showMessageDialog(this,
                   "This vehicle is already booked by another customer in the selected date range.\n\n"
                   + availabilityInfo + "\n\n"
                   + "Please choose different dates.",
                   "Vehicle Unavailable",
                   JOptionPane.WARNING_MESSAGE);
           return;
       }

       // 3️⃣ SAVE BOOKING — safe to proceed
       Booking b = new Booking();
       b.setCustomerId(user.getId());
       b.setVehicleId(vehicle.getId());
       b.setStartDate(s);
       b.setEndDate(e);
       b.setTotalCost(total);
       b.setStatus("PENDING");

       boolean ok = dao.addBooking(b);

       if (ok) {
           JOptionPane.showMessageDialog(this, 
               "Booking confirmed!", 
               "Success", JOptionPane.INFORMATION_MESSAGE);
           dispose();
       } else {
           JOptionPane.showMessageDialog(this, 
               "Failed to save booking. Try again.", 
               "Error", JOptionPane.ERROR_MESSAGE);
       }
   }
}
