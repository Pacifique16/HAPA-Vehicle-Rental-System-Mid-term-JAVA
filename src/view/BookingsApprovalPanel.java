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
import model.BookingRecord;
import model.Booking;
import model.Vehicle;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Show pending bookings and allow Approve / Reject
 *
 * Expected BookingDAO methods:
 *  - List<BookingRecord> getPendingBookings()
 *  - boolean approveBooking(int bookingId)
 *  - boolean rejectBooking(int bookingId)
 */
public class BookingsApprovalPanel extends JPanel {

    private final BookingDAO bookingDAO = new BookingDAOImpl();
    private JTable table;
    private DefaultTableModel model;

    public BookingsApprovalPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);
        build();
        load();
    }

    private void build(){
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        JButton btnRefresh = new JButton("Refresh"); btnRefresh.addActionListener(e -> load());
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        String[] cols = new String[]{"ID","Customer","Vehicle","Start","End","Total","Status"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER,8,8));
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        approve.addActionListener(e -> doApprove());
        reject.addActionListener(e -> doReject());
        actions.add(approve); actions.add(reject);
        add(actions, BorderLayout.SOUTH);
    }

    private void load(){
        model.setRowCount(0);
        try {
            List<BookingRecord> list = bookingDAO.getPendingBookings();
            for (BookingRecord r : list) {
                Booking b = r.getBooking();
                Vehicle v = r.getVehicle();
                User u = bookingDAO.getCustomerForBooking(b.getCustomerId()); // optional helper; else supply u data via BookingRecord
                String cname = u==null? String.valueOf(b.getCustomerId()) : u.getFullName();
                model.addRow(new Object[]{b.getId(), cname, v.getModel(), b.getStartDate().toString(), b.getEndDate().toString(), String.format("%,.0f", b.getTotalCost()), b.getStatus()});
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void doApprove(){
        int sel = table.getSelectedRow(); if (sel<0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.approveBooking(id);
        if (ok) { JOptionPane.showMessageDialog(this,"Approved."); load(); } else JOptionPane.showMessageDialog(this,"Failed to approve.");
    }

    private void doReject(){
        int sel = table.getSelectedRow(); if (sel<0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        boolean ok = bookingDAO.rejectBooking(id);
        if (ok) { JOptionPane.showMessageDialog(this,"Rejected."); load(); } else JOptionPane.showMessageDialog(this,"Failed to reject.");
    }
}
