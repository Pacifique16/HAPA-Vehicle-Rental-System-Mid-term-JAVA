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
import model.Booking;
import model.User;
import model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Modern card-style booking form.
 * Usage:
 *   new BookingForm(loggedInUser, vehicle).setVisible(true);
 */
public class BookingForm extends JDialog {

    private final Vehicle vehicle;
    private final User user;

    private JLabel lblImage;
    private JLabel lblModel;
    private JLabel lblPrice;
    private JSpinner spStart;
    private JSpinner spEnd;
    private JLabel lblDays;
    private JLabel lblTotal;
    private JButton btnConfirm;
    private JButton btnCancel;

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

    private void initComponents() {
        // Root panel — dim background effect already handled by JDialog modality
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));

        // Card panel (white, rounded-ish via border)
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(420, 430));

        // Top: Image
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(220, 140));
        setVehicleImage();

        card.add(lblImage, BorderLayout.NORTH);

        // Center: info + date pickers
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

        // Dates panel (side-by-side)
        JPanel dates = new JPanel(new GridBagLayout());
        dates.setBackground(Color.WHITE);
        GridBagConstraints d = new GridBagConstraints();
        d.insets = new Insets(4, 4, 4, 4);
        d.fill = GridBagConstraints.HORIZONTAL;

        // Start
        d.gridx = 0;
        d.gridy = 0;
        dates.add(new JLabel("Start Date"), d);

        spStart = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spStart.setEditor(new JSpinner.DateEditor(spStart, "yyyy-MM-dd"));
        d.gridx = 0;
        d.gridy = 1;
        dates.add(spStart, d);

        // End
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

        // Calculated info
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

        // Bottom: buttons
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

        // Add listeners
        spStart.addChangeListener(e -> recalc());
        spEnd.addChangeListener(e -> recalc());

        btnConfirm.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> dispose());

        // Put card into center of root
        root.add(card);

        setContentPane(root);

        // initial calculation
        recalc();
    }

    private void setVehicleImage() {
        try {
            ImageIcon ic = new ImageIcon(vehicle.getImagePath());
            Image img = ic.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            lblImage.setText(vehicle.getModel());
        }
    }

    private void recalc() {
        try {
            Date s = (Date) spStart.getValue();
            Date e = (Date) spEnd.getValue();

            // normalize (zero time part) by using days difference via milliseconds
            long dif = e.getTime() - s.getTime();
            long days = dif / (1000L * 60 * 60 * 24) + 1;
            if (days <= 0) days = 1;

            double total = days * vehicle.getPricePerDay();

            lblDays.setText("Days: " + days);
            lblTotal.setText("Total: " + String.format("%,.0f RWF", total));
        } catch (Exception ex) {
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
           JOptionPane.showMessageDialog(this,
                   "This vehicle is already booked by another customer in the selected date range.\n"
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
