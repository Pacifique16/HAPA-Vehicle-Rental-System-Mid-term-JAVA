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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* NOTE:
 * - Keeps the same BookingDAO usage as before.
 * - Looks for images/search.png and images/calendar.png in project root; falls back to emoji substitutes.
 */

public class MyBookingsPanel extends JPanel {

    private final User user;
    private BookingDAO bookingDAO = new BookingDAOImpl();

    // UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblImage, lblModel, lblDates, lblDays, lblTotal, lblStatus;
    private JButton btnCancelBooking, btnReopenBooking;

    // Filters
    private JTextField tfModel;
    private JComboBox<String> cbStatus;
    private JSpinner spFrom, spTo;
    private JButton bThisMonth, bPast, bUpcoming, bClearDates;
    private JButton btnExport;

    // Pagination
    private final int pageSize = 5;
    private int currentPage = 1;
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JPanel paginationPanel;
    
    // Store current search results for row selection
    private List<BookingRecord> currentSearchResults = new java.util.ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Icons (may be null if files not found)
    private ImageIcon icoSearch, icoCalendar;

    public MyBookingsPanel(User user) {
        this.user = user;
        loadIcons();
        initComponents();
        applyFiltersAndLoad();
    }

    private void loadIcons() {
        try {
            File s = new File("images/search.png");
            if (s.exists()) icoSearch = new ImageIcon(ImageIO.read(s).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            File c = new File("images/calendar.png");
            if (c.exists()) icoCalendar = new ImageIcon(ImageIO.read(c).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (Exception ignored) { /* ignore - will use emoji */ }
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Top area: filter rows
        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Vehicle + Search field (with icon)
        JPanel pSearch = new JPanel(new BorderLayout(6, 0));
        pSearch.setBackground(Color.WHITE);
        
        JLabel searchIcon = new JLabel("Search:");

        
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        tfModel = new JTextField();
        tfModel.setColumns(14);
        styleRounded(tfModel);
        tfModel.setToolTipText("Search by vehicle model, plate, status, dates, total cost...");
        pSearch.add(searchIcon, BorderLayout.WEST);
        pSearch.add(tfModel, BorderLayout.CENTER);

        // Status combo
        cbStatus = new JComboBox<>(new String[]{"All status", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "EXPIRED"});
        styleRounded(cbStatus);
        cbStatus.setPreferredSize(new Dimension(140, cbStatus.getPreferredSize().height));
        cbStatus.setToolTipText("Filter by status");

        // Date 'From' (with Start label)
        JPanel pFrom = new JPanel(new BorderLayout(6, 0));
        pFrom.setBackground(Color.WHITE);
        
        JLabel calFrom = new JLabel("Start:");
        calFrom.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        spFrom = new JSpinner(new SpinnerDateModel());
        spFrom.setEditor(new JSpinner.DateEditor(spFrom, "yyyy-MM-dd"));
        JFormattedTextField tfFrom = ((JSpinner.DefaultEditor) spFrom.getEditor()).getTextField();
        tfFrom.setForeground(Color.BLACK);
        tfFrom.setBackground(Color.WHITE);
        tfFrom.setDisabledTextColor(Color.GRAY);

        spFrom.setPreferredSize(new Dimension(120, spFrom.getPreferredSize().height));
       
        pFrom.add(calFrom, BorderLayout.WEST);
       

        // Date 'To'
        JPanel pTo = new JPanel(new BorderLayout(6, 0));
        pTo.setBackground(Color.WHITE);
        
        JLabel calTo = new JLabel("End:");
        calTo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        spTo = new JSpinner(new SpinnerDateModel());
        spTo.setEditor(new JSpinner.DateEditor(spTo, "yyyy-MM-dd"));
        JFormattedTextField tfTo = ((JSpinner.DefaultEditor) spTo.getEditor()).getTextField();
        tfTo.setForeground(Color.BLACK);
        tfTo.setBackground(Color.WHITE);
        tfTo.setDisabledTextColor(Color.GRAY);
        spTo.setPreferredSize(new Dimension(120, spTo.getPreferredSize().height));
       
        pTo.add(calTo, BorderLayout.WEST);
      
        JPanel wrapFrom = new JPanel(new BorderLayout());
        wrapFrom.setBackground(Color.WHITE);
        styleRounded(wrapFrom);
        wrapFrom.add(spFrom);

        JPanel wrapTo = new JPanel(new BorderLayout());
        wrapTo.setBackground(Color.WHITE);
        styleRounded(wrapTo);
        wrapTo.add(spTo);

        pFrom.add(wrapFrom, BorderLayout.CENTER);
        pTo.add(wrapTo, BorderLayout.CENTER);


        // Place controls into grid
        c.gridx = 0; c.gridy = 0; c.weightx = 0.45;
        top.add(pSearch, c);

        c.gridx = 1; c.gridy = 0; c.weightx = 0.18;
        top.add(cbStatus, c);

        c.gridx = 2; c.gridy = 0; c.weightx = 0.18;
        top.add(pFrom, c);

        c.gridx = 3; c.gridy = 0; c.weightx = 0.18;
        top.add(pTo, c);

        // Quick action buttons row (this month, past, upcoming, clear + export)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(Color.WHITE);

        bThisMonth = new JButton("This month");
        bPast = new JButton("Past");
        bUpcoming = new JButton("Upcoming");
        bClearDates = new JButton("Clear dates");
        btnExport = new JButton("Export CSV");

        // small pill style for quick buttons
        for (JButton bb : new JButton[]{bThisMonth, bPast, bUpcoming, bClearDates}) {
            bb.setFocusPainted(false);
            bb.setBorder(new RoundedBorder(12, new Color(200, 200, 200)));
            bb.setBackground(new Color(250, 250, 250));
            bb.setPreferredSize(new Dimension(110, 28));
        }
        btnExport.setFocusPainted(false);
        btnExport.setBackground(new Color(255, 217, 102));
        btnExport.setPreferredSize(new Dimension(110, 28));

        actions.add(bThisMonth);
        actions.add(bPast);
        actions.add(bUpcoming);
        actions.add(bClearDates);
        actions.add(Box.createHorizontalStrut(12));
        actions.add(btnExport);

        // place actions under the grid (full width)
        c.gridx = 0; c.gridy = 1; c.gridwidth = 4; c.weightx = 1;
        top.add(actions, c);

        add(top, BorderLayout.NORTH);

        // Listeners for filters
        tfModel.addActionListener(e -> goFirstAndReload());
        cbStatus.addActionListener(e -> goFirstAndReload());
        spFrom.addChangeListener(e -> goFirstAndReload());
        spTo.addChangeListener(e -> goFirstAndReload());

        bThisMonth.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            spFrom.setValue(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            spTo.setValue(cal.getTime());
            goFirstAndReload();
        });

        bPast.addActionListener(e -> {
            spFrom.setValue(new Date(0)); // epoch -> treated as 'no-from' in DAO when necessary
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DAY_OF_MONTH, -1);
            spTo.setValue(today.getTime());
            goFirstAndReload();
        });

        bUpcoming.addActionListener(e -> {
            Calendar today = Calendar.getInstance();
            spFrom.setValue(today.getTime());
            Calendar far = Calendar.getInstance();
            far.add(Calendar.YEAR, 30);
            spTo.setValue(far.getTime());
            goFirstAndReload();
        });

        bClearDates.addActionListener(e -> {
            // per request: Clear => start = 2000-01-01, end = 2100-01-01
            Calendar s = Calendar.getInstance();
            s.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
            s.set(Calendar.MILLISECOND, 0);
            spFrom.setValue(s.getTime());

            Calendar eCal = Calendar.getInstance();
            eCal.set(2100, Calendar.JANUARY, 1, 0, 0, 0);
            eCal.set(Calendar.MILLISECOND, 0);
            spTo.setValue(eCal.getTime());

            goFirstAndReload();
        });

        btnExport.addActionListener(e -> exportCsvCurrentFiltered());

        // Center: Split pane with table and preview
        String[] cols = new String[]{"ID", "Vehicle", "Start", "End", "Days", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // hide ID column in view
        table.removeColumn(table.getColumnModel().getColumn(0));
        JScrollPane spTable = new JScrollPane(table);

        // Preview (right)
        JPanel preview = new JPanel();
        preview.setLayout(new BoxLayout(preview, BoxLayout.Y_AXIS));
        preview.setBackground(Color.WHITE);
        preview.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        preview.setPreferredSize(new Dimension(360, 0));

        lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(200, 130));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblModel = new JLabel("Select a booking");
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblModel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDates = new JLabel("");
        lblDates.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDays = new JLabel("");
        lblDays.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTotal = new JLabel("");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(34, 109, 180));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus = new JLabel("");
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCancelBooking = new JButton("CANCEL BOOKING");
        btnCancelBooking.setBackground(new Color(255, 217, 102));
        btnCancelBooking.setFocusPainted(false);
        btnCancelBooking.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancelBooking.setEnabled(false);

        btnReopenBooking = new JButton("REOPEN BOOKING");
        btnReopenBooking.setBackground(new Color(240, 240, 240));
        btnReopenBooking.setFocusPainted(false);
        btnReopenBooking.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReopenBooking.setEnabled(false);

        btnCancelBooking.addActionListener(e -> cancelSelectedBooking());
        btnReopenBooking.addActionListener(e -> openReopenDialog());

        preview.add(lblImage);
        preview.add(Box.createVerticalStrut(8));
        preview.add(lblModel);
        preview.add(Box.createVerticalStrut(6));
        preview.add(lblDates);
        preview.add(Box.createVerticalStrut(4));
        preview.add(lblDays);
        preview.add(Box.createVerticalStrut(8));
        preview.add(lblTotal);
        preview.add(Box.createVerticalStrut(8));
        preview.add(lblStatus);
        preview.add(Box.createVerticalStrut(12));
        preview.add(btnCancelBooking);
        preview.add(Box.createVerticalStrut(8));
        preview.add(btnReopenBooking);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spTable, preview);
        split.setResizeWeight(0.66);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        // Bottom: pagination controls
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        paginationPanel.setBackground(Color.WHITE);
        lblPageInfo = new JLabel("Page 0 of 0");
        bottom.add(paginationPanel, BorderLayout.CENTER);
        bottom.add(lblPageInfo, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // table listeners
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onRowDoubleClicked();
                }
            }
        });

        // Set requested default dates:
        Calendar defFrom = Calendar.getInstance();
        defFrom.set(2025, Calendar.NOVEMBER, 14, 0, 0, 0);
        defFrom.set(Calendar.MILLISECOND, 0);
        spFrom.setValue(defFrom.getTime());

        Calendar defTo = Calendar.getInstance();
        defTo.set(2050, Calendar.DECEMBER, 31, 0, 0, 0);
        defTo.set(Calendar.MILLISECOND, 0);
        spTo.setValue(defTo.getTime());
    }

    // small helper: style a component with rounded border look
    private void styleRounded(JComponent comp) {
        comp.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(8, new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        comp.setBackground(Color.WHITE);
    }

    // helpers to interpret sentinel values (we treat specific dates directly)
    private Date getFilterFrom() {
        Date d = (Date) spFrom.getValue();
        return d;
    }
    private Date getFilterTo() {
        Date d = (Date) spTo.getValue();
        return d;
    }



    
    private void applyFiltersAndLoad() {
        
            // Removed auto-expire to reduce database calls

        
        String modelLike = tfModel.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        if (status != null && (status.isEmpty() || status.equals("All status"))) status = null;

        Date from = getFilterFrom();
        Date to = getFilterTo();

        // 🟦 Detect “Past” filter (to < today)
        Date today = new Date();
        long todayDays = today.getTime() / (1000L*60*60*24);
        long toDays = to.getTime() / (1000L*60*60*24);

        if (toDays < todayDays) {
            // 🚀 Past filter activated → only show EXPIRED
            status = "EXPIRED";
        }
        // Get all filtered records first
        List<BookingRecord> allFiltered = bookingDAO.getFilteredBookings(user.getId(), "", from, to, status);
        
        // Apply search filter across all criteria
        String searchQuery = tfModel.getText().trim().toLowerCase();
        currentSearchResults = new java.util.ArrayList<>();
        for (BookingRecord rec : allFiltered) {
            if (searchQuery.isEmpty() || matchesSearch(rec, searchQuery)) {
                currentSearchResults.add(rec);
            }
        }
        
        // Calculate pagination from search results
        int totalRows = currentSearchResults.size();
        totalPages = Math.max(1, (int)Math.ceil(totalRows / (double) pageSize));
        if (currentPage > totalPages) currentPage = totalPages;

        int offset = (currentPage - 1) * pageSize;
        List<BookingRecord> page = new java.util.ArrayList<>();
        for (int i = offset; i < Math.min(offset + pageSize, currentSearchResults.size()); i++) {
            page.add(currentSearchResults.get(i));
        }

        tableModel.setRowCount(0);
        for (BookingRecord rec : page) {
            Booking b = rec.getBooking();
            Vehicle v = rec.getVehicle();

            long days = 1;
            try {
                days = Math.max(1L, (b.getEndDate().getTime() - b.getStartDate().getTime())/(1000L*60*60*24) + 1);
            } catch (Exception ignored){}

            Object[] row = new Object[]{
                    b.getId(),
                    v.getModel(),
                    sdf.format(b.getStartDate()),
                    sdf.format(b.getEndDate()),
                    days,
                    String.format("%,.0f", b.getTotalCost()),
                    b.getStatus()
            };
            tableModel.addRow(row);
        }

        rebuildPaginationControls();
        lblPageInfo.setText(String.format("Page %d of %d ( %d )", currentPage, totalPages, Math.max(0, totalRows)));

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            onRowSelected();
        } else {
            clearPreview();
        }
    }

    private void goFirstAndReload() {
        currentPage = 1;
        applyFiltersAndLoad();
    }

    private void rebuildPaginationControls() {
        paginationPanel.removeAll();

        JButton first = new JButton("<< First");
        JButton prev = new JButton("< Previous");
        JButton next = new JButton("Next >");
        JButton last = new JButton("Last >>");

        first.setEnabled(currentPage > 1);
        prev.setEnabled(currentPage > 1);
        next.setEnabled(currentPage < totalPages);
        last.setEnabled(currentPage < totalPages);

        first.addActionListener(e -> { currentPage = 1; applyFiltersAndLoad(); });
        prev.addActionListener(e -> { if (currentPage > 1) currentPage--; applyFiltersAndLoad(); });
        next.addActionListener(e -> { if (currentPage < totalPages) currentPage++; applyFiltersAndLoad(); });
        last.addActionListener(e -> { currentPage = totalPages; applyFiltersAndLoad(); });

        paginationPanel.add(first);
        paginationPanel.add(prev);

        int visible = 7;
        int start = Math.max(1, currentPage - visible/2);
        int end = Math.min(totalPages, start + visible - 1);
        if (end - start + 1 < visible) start = Math.max(1, end - visible + 1);

        for (int p = start; p <= end; p++) {
            final int pi = p;
            JButton b = new JButton(String.valueOf(p));
            if (p == currentPage) b.setEnabled(false);
            b.addActionListener(e -> { currentPage = pi; applyFiltersAndLoad(); });
            paginationPanel.add(b);
        }

        paginationPanel.add(next);
        paginationPanel.add(last);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private void onRowSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) { clearPreview(); return; }
        int modelIndex = table.convertRowIndexToModel(sel);
        int bookingId = Integer.parseInt(tableModel.getValueAt(modelIndex, 0).toString());

        BookingRecord rec = null;
        for (BookingRecord r : currentSearchResults) if (r.getBooking().getId() == bookingId) { rec = r; break; }
        if (rec == null) { clearPreview(); return; }

        Booking b = rec.getBooking();
        Vehicle v = rec.getVehicle();

        lblModel.setText(v.getModel());
        lblDates.setText("From: " + sdf.format(b.getStartDate()) + "   To: " + sdf.format(b.getEndDate()));
        long days = Math.max(1L, (b.getEndDate().getTime() - b.getStartDate().getTime())/(1000L*60*60*24) + 1);
        lblDays.setText("Days: " + days);
        lblTotal.setText("Total: " + String.format("%,.0f RWF", b.getTotalCost()));
        if ("REJECTED".equals(b.getStatus()) && b.getRejectionReason() != null && !b.getRejectionReason().trim().isEmpty()) {
            lblStatus.setText("Reason: " + b.getRejectionReason());
        } else {
            lblStatus.setText("");
        }

        try {
            ImageIcon ic = new ImageIcon(v.getImagePath());
            Image img = ic.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
            lblImage.setText("");
        } catch (Exception ex) {
            lblImage.setIcon(null);
            lblImage.setText(v.getModel());
        }

        boolean canCancel = "PENDING".equalsIgnoreCase(b.getStatus());
        boolean canReopen = "CANCELLED".equalsIgnoreCase(b.getStatus());
        btnCancelBooking.setEnabled(canCancel);
        btnReopenBooking.setEnabled(canReopen);
    }

    private void clearPreview() {
        lblModel.setText("No booking selected");
        lblDates.setText("");
        lblDays.setText("");
        lblTotal.setText("");
        lblStatus.setText("");
        lblImage.setIcon(null);
        btnCancelBooking.setEnabled(false);
        btnReopenBooking.setEnabled(false);
    }

    private void onRowDoubleClicked() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        String status = tableModel.getValueAt(modelIndex, 6).toString();
        if ("CANCELLED".equalsIgnoreCase(status)) openReopenDialog();
    }

    private void cancelSelectedBooking() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int bookingId = Integer.parseInt(tableModel.getValueAt(modelIndex, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = bookingDAO.cancelBooking(bookingId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
            applyFiltersAndLoad();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openReopenDialog() {
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int bookingId = Integer.parseInt(tableModel.getValueAt(modelIndex, 0).toString());

        BookingRecord rec = null;
        for (BookingRecord r : currentSearchResults) if (r.getBooking().getId() == bookingId) { rec = r; break; }
        if (rec == null) return;
        Booking b = rec.getBooking();

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Reopen Booking", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel info = new JLabel("Choose new start and end dates:");
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(info);
        p.add(Box.createVerticalStrut(8));

        JSpinner sFrom = new JSpinner(new SpinnerDateModel(b.getStartDate(), null, null, Calendar.DAY_OF_MONTH));
        sFrom.setEditor(new JSpinner.DateEditor(sFrom, "yyyy-MM-dd"));
        JSpinner sTo = new JSpinner(new SpinnerDateModel(b.getEndDate(), null, null, Calendar.DAY_OF_MONTH));
        sTo.setEditor(new JSpinner.DateEditor(sTo, "yyyy-MM-dd"));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        row.add(new JLabel("Start:"));
        row.add(sFrom);
        row.add(new JLabel("End:"));
        row.add(sTo);
        p.add(row);

        p.add(Box.createVerticalStrut(12));

        JButton ok = new JButton("REOPEN");
        JButton cancel = new JButton("CANCEL");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btns.add(cancel);
        btns.add(ok);
        p.add(btns);

        ok.addActionListener(evt -> {
            Date ns = (Date) sFrom.getValue();
            Date ne = (Date) sTo.getValue();
            if (ne.before(ns)) {
                JOptionPane.showMessageDialog(dlg, "End date must be same or after start date.", "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok2 = bookingDAO.reopenBooking(bookingId, ns, ne);
            if (ok2) {
                JOptionPane.showMessageDialog(dlg, "Booking reopened.");
                dlg.dispose();
                applyFiltersAndLoad();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to reopen booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(evt -> dlg.dispose());

        dlg.setContentPane(p);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void exportCsvCurrentFiltered() {
        if (currentSearchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records to export.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save CSV Report");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        int ret = fc.showSaveDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) return;
        
        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
        
        try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
            // Write title and timestamp
            writer.append("HAPA Vehicle Rental System - My Bookings Report\n");
            writer.append("Generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n");
            
            // Write headers
            writer.append("Vehicle Model,Start Date,End Date,Days,Total Cost,Status\n");
            
            // Write data
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for (BookingRecord r : currentSearchResults) {
                Booking b = r.getBooking();
                Vehicle v = r.getVehicle();
                long days = Math.max(1L, (b.getEndDate().getTime() - b.getStartDate().getTime())/(1000L*60*60*24) + 1);
                
                String cellValue = v.getModel().replace(",", ";");
                writer.append(cellValue).append(",");
                writer.append(df.format(b.getStartDate())).append(",");
                writer.append(df.format(b.getEndDate())).append(",");
                writer.append(String.valueOf(days)).append(",");
                writer.append(String.format("%,.0f RWF", b.getTotalCost())).append(",");
                writer.append(b.getStatus()).append("\n");
            }
            JOptionPane.showMessageDialog(this, "Report exported successfully to: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        String out = s.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\"") || out.contains("\n")) return "\"" + out + "\"";
        return out;
    }
    
    private boolean matchesSearch(BookingRecord rec, String query) {
        Booking b = rec.getBooking();
        Vehicle v = rec.getVehicle();
        
        return (v.getModel() != null && v.getModel().toLowerCase().contains(query)) ||
               (v.getPlateNumber() != null && v.getPlateNumber().toLowerCase().contains(query)) ||
               (v.getCategory() != null && v.getCategory().toLowerCase().contains(query)) ||
               (b.getStatus() != null && b.getStatus().toLowerCase().contains(query)) ||
               (sdf.format(b.getStartDate()).contains(query)) ||
               (sdf.format(b.getEndDate()).contains(query)) ||
               String.valueOf((int)b.getTotalCost()).contains(query);
    }

    /**
     * Small rounded border implementation (visual only)
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color line;

        RoundedBorder(int radius, Color lineColor) { this.radius = radius; this.line = lineColor; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(line);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 8, 6, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 6;
            return insets;
        }
    }
}
