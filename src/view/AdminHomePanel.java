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
import dao.VehicleDAO;
import dao.VehicleDAOImpl;
import dao.UserDAO;
import dao.UserDAOImpl;
import model.Vehicle;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Small dashboard: 4 summary cards and recent bookings quick table
 */
public class AdminHomePanel extends JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAOImpl(); // implement if missing
    private final UserDAO userDAO = new UserDAOImpl();
    private final BookingDAO bookingDAO = new BookingDAOImpl();

    private JLabel lblVehicles, lblUsers, lblRentals, lblAvailable;

    public AdminHomePanel() {
        setLayout(new BorderLayout(12,12));
        setBackground(Color.WHITE);
        buildTopCards();
        buildRecent();
        loadAnalytics();
    }

    private void buildTopCards(){
        JPanel cards = new JPanel(new GridLayout(1,4,12,12));
        cards.setOpaque(false);

        lblVehicles = makeCard("Total Vehicles", "0");
        lblUsers = makeCard("Total Users", "0");
        lblRentals = makeCard("Total Rentals", "0");
        lblAvailable = makeCard("Available Today", "0");

        cards.add(wrapCard(lblVehicles));
        cards.add(wrapCard(lblUsers));
        cards.add(wrapCard(lblRentals));
        cards.add(wrapCard(lblAvailable));

        add(cards, BorderLayout.NORTH);
    }

    private JPanel wrapCard(JLabel center){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JLabel makeCard(String title, String value){
        JLabel l = new JLabel("<html><div style='text-align:center'><div style='font-size:18px;color:#222;'>"+value+"</div><div style='font-size:12px;color:#666;'>"+title+"</div></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private void buildRecent(){
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        JLabel h = new JLabel("Recent bookings");
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(h, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(6, 10);
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        JScrollPane sp = new JScrollPane(ta);
        p.add(sp, BorderLayout.CENTER);

        add(p, BorderLayout.CENTER);
    }

    private void loadAnalytics(){
        try {
            int totalVehicles = vehicleDAO.countVehicles();
            int totalUsers = userDAO.countUsers();
            int totalRentals = bookingDAO.countTotalRentals();
            int availableToday = vehicleDAO.countAvailableToday();

            lblVehicles.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalVehicles + "</div><div style='font-size:12px;color:#666;'>Total Vehicles</div></div></html>");
            lblUsers.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalUsers + "</div><div style='font-size:12px;color:#666;'>Total Users</div></div></html>");
            lblRentals.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + totalRentals + "</div><div style='font-size:12px;color:#666;'>Total Rentals</div></div></html>");
            lblAvailable.setText("<html><div style='text-align:center'><div style='font-size:20px;color:#222;'>" + availableToday + "</div><div style='font-size:12px;color:#666;'>Vehicles Available Today</div></div></html>");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
