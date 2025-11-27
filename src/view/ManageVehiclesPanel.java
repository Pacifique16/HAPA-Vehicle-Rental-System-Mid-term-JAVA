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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Manage vehicles: add / edit / delete / search
 *
 * Expected VehicleDAO methods:
 *  - List<Vehicle> getAllVehicles()
 *  - List<Vehicle> searchVehicles(String q)
 *  - boolean addVehicle(Vehicle v)
 *  - boolean updateVehicle(Vehicle v)
 *  - boolean deleteVehicle(int id)
 */
public class ManageVehiclesPanel extends JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAOImpl();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;

    public ManageVehiclesPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);
        buildTop();
        buildTable();
        loadAll();
    }

    private void buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        top.setBackground(Color.WHITE);

        tfSearch = new JTextField(20);
        styleRounded(tfSearch);
        tfSearch.setToolTipText("Search plate, model, category, price...");
        tfSearch.addActionListener(e -> doSearch());

        JButton bSearch = new JButton("Search");
        bSearch.addActionListener(e -> doSearch());

        JButton bAdd = new JButton("Add Vehicle");
        bAdd.addActionListener(e -> openAddDialog());

        top.add(tfSearch);
        top.add(bSearch);
        top.add(Box.createHorizontalStrut(20));
        top.add(bAdd);

        add(top, BorderLayout.NORTH);
    }

    private void buildTable() {
        String[] cols = new String[]{"ID","Plate","Model","Category","Price/day","Image","Seats"};
        model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPopupMenu pm = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("Edit");
        JMenuItem miDelete = new JMenuItem("Delete");
        miEdit.addActionListener(e -> openEditDialog());
        miDelete.addActionListener(e -> doDelete());
        pm.add(miEdit); pm.add(miDelete);
        table.setComponentPopupMenu(pm);
    }

    private void loadAll(){
        model.setRowCount(0);
        try {
            List<Vehicle> list = vehicleDAO.getAllVehicles();
            for (Vehicle v : list) {
                model.addRow(new Object[]{v.getId(), v.getPlateNumber(), v.getModel(), v.getCategory(), String.format("%,.0f", v.getPricePerDay()), v.getImagePath(), v.getSeats()});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void doSearch(){
        String q = tfSearch.getText().trim();
        model.setRowCount(0);
        try {
            List<Vehicle> list = q.isEmpty() ? vehicleDAO.getAllVehicles() : vehicleDAO.searchVehicles(q);
            for (Vehicle v : list) {
                model.addRow(new Object[]{v.getId(), v.getPlateNumber(), v.getModel(), v.getCategory(), String.format("%,.0f", v.getPricePerDay()), v.getImagePath(), v.getSeats()});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void openAddDialog(){
        VehicleFormDialog dlg = new VehicleFormDialog(null);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadAll();
    }

    private void openEditDialog(){
        int sel = table.getSelectedRow();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Select a vehicle"); return; }
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        try {
            Vehicle v = vehicleDAO.findById(id);
            VehicleFormDialog dlg = new VehicleFormDialog(v);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadAll();
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void doDelete(){
        int sel = table.getSelectedRow();
        if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete vehicle?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = vehicleDAO.deleteVehicle(id);
        if (ok) { JOptionPane.showMessageDialog(this, "Deleted."); loadAll(); }
        else JOptionPane.showMessageDialog(this, "Failed to delete.");
    }

    private void styleRounded(JComponent c){
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200),1,true),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }

    // SIMPLE vehicle add/edit dialog (inner class)
    private class VehicleFormDialog extends JDialog {
        private boolean saved = false;
        private JTextField tfPlate, tfModel, tfCategory, tfPrice, tfSeats;
        private JTextField tfImagePath;
        private Vehicle editing;

        VehicleFormDialog(Vehicle v) {
            super((Frame) SwingUtilities.getWindowAncestor(ManageVehiclesPanel.this), true);
            this.editing = v;
            setTitle(v==null? "Add Vehicle" : "Edit Vehicle");
            setSize(420,340);
            setLayout(new BorderLayout(8,8));
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.WEST;

            tfPlate = new JTextField(v==null?"":v.getPlateNumber(),18);
            tfModel = new JTextField(v==null?"":v.getModel(),18);
            tfCategory = new JTextField(v==null?"":v.getCategory(),18);
            tfPrice = new JTextField(v==null? "0": String.valueOf((long)v.getPricePerDay()),12);
            tfSeats = new JTextField(v==null? "4": String.valueOf(v.getSeats()),4);
            tfImagePath = new JTextField(v==null?"":v.getImagePath(),18);

            c.gridx=0; c.gridy=0; p.add(new JLabel("Plate:"), c); c.gridx=1; p.add(tfPlate, c);
            c.gridx=0; c.gridy=1; p.add(new JLabel("Model:"), c); c.gridx=1; p.add(tfModel, c);
            c.gridx=0; c.gridy=2; p.add(new JLabel("Category:"), c); c.gridx=1; p.add(tfCategory, c);
            c.gridx=0; c.gridy=3; p.add(new JLabel("Price/day:"), c); c.gridx=1; p.add(tfPrice, c);
            c.gridx=0; c.gridy=4; p.add(new JLabel("Seats:"), c); c.gridx=1; p.add(tfSeats, c);
            c.gridx=0; c.gridy=5; p.add(new JLabel("Image Path:"), c); c.gridx=1; p.add(tfImagePath, c);

            add(p, BorderLayout.CENTER);
            JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton save = new JButton("Save"); JButton cancel = new JButton("Cancel");
            b.add(cancel); b.add(save);
            add(b, BorderLayout.SOUTH);

            save.addActionListener(ae -> {
                try {
                    Vehicle vv = editing==null ? new Vehicle() : editing;
                    vv.setPlateNumber(tfPlate.getText().trim());
                    vv.setModel(tfModel.getText().trim());
                    vv.setCategory(tfCategory.getText().trim());
                    vv.setPricePerDay(Double.parseDouble(tfPrice.getText().trim()));
                    vv.setSeats(Integer.parseInt(tfSeats.getText().trim()));
                    vv.setImagePath(tfImagePath.getText().trim());

                    boolean ok;
                    if (editing==null) ok = vehicleDAO.addVehicle(vv);
                    else ok = vehicleDAO.updateVehicle(vv);

                    if (ok) { saved = true; dispose(); }
                    else JOptionPane.showMessageDialog(this, "Save failed.");
                } catch (Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Invalid input."); }
            });

            cancel.addActionListener(ae -> dispose());
        }

        public boolean isSaved(){ return saved; }
    }
}
