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

import dao.UserDAO;
import dao.UserDAOImpl;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Manage users: add / edit / delete
 *
 * Expected UserDAO methods:
 *   List<User> getAllUsers()
 *   boolean addUser(User u)
 *   boolean updateUser(User u)
 *   boolean deleteUser(int id)
 */
public class ManageUsersPanel extends JPanel {

    private final UserDAO userDAO = new UserDAOImpl();
    private JTable table;
    private DefaultTableModel model;

    public ManageUsersPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(Color.WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        JButton bAdd = new JButton("Add User");
        bAdd.addActionListener(e -> openAdd());
        top.add(bAdd);
        add(top, BorderLayout.NORTH);

        String[] cols = new String[]{"ID","Full Name","Phone","Email","Username","Role"};
        model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPopupMenu pm = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("Edit");
        JMenuItem miDelete = new JMenuItem("Delete");
        miEdit.addActionListener(e -> openEdit());
        miDelete.addActionListener(e -> doDelete());
        pm.add(miEdit); pm.add(miDelete);
        table.setComponentPopupMenu(pm);

        load();
    }

    private void load(){
        model.setRowCount(0);
        try {
            List<User> list = userDAO.getAllUsers();
            for (User u : list) {
                model.addRow(new Object[]{u.getId(), u.getFullName(), u.getPhone(), u.getEmail(), u.getUsername(), u.getRole()});
            }
        } catch (Exception ex){ ex.printStackTrace(); }
    }

    private void openAdd(){
        UserFormDialog dlg = new UserFormDialog(null);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.saved) load();
    }

    private void openEdit(){
        int sel = table.getSelectedRow(); if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        try {
            User u = userDAO.findById(id);
            UserFormDialog dlg = new UserFormDialog(u);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
            if (dlg.saved) load();
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void doDelete(){
        int sel = table.getSelectedRow(); if (sel < 0) return;
        int modelIndex = table.convertRowIndexToModel(sel);
        int id = Integer.parseInt(model.getValueAt(modelIndex,0).toString());
        int c = JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c!=JOptionPane.YES_OPTION) return;
        boolean ok = userDAO.deleteUser(id);
        if (ok) { JOptionPane.showMessageDialog(this,"Deleted"); load(); } else JOptionPane.showMessageDialog(this,"Failed");
    }

    // small add/edit dialog
    private class UserFormDialog extends JDialog {
        boolean saved = false;
        private JTextField tfName, tfPhone, tfEmail, tfUsername;
        private JPasswordField pfPassword;
        private JComboBox<String> cbRole;
        private User editing;
        UserFormDialog(User u) {
            super((Frame) SwingUtilities.getWindowAncestor(ManageUsersPanel.this), true);
            editing = u;
            setTitle(u==null?"Add User":"Edit User");
            setSize(420,320);
            setLayout(new BorderLayout(8,8));
            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8,8,8,8); c.anchor = GridBagConstraints.WEST;

            tfName = new JTextField(u==null?"":u.getFullName(),18);
            tfPhone = new JTextField(u==null?"":u.getPhone(),18);
            tfEmail = new JTextField(u==null?"":u.getEmail(),18);
            tfUsername = new JTextField(u==null?"":u.getUsername(),18);
            pfPassword = new JPasswordField(18);
            cbRole = new JComboBox<>(new String[]{"CUSTOMER","ADMIN"});

            c.gridx=0; c.gridy=0; p.add(new JLabel("Full Name:"),c); c.gridx=1; p.add(tfName,c);
            c.gridx=0; c.gridy=1; p.add(new JLabel("Phone:"),c); c.gridx=1; p.add(tfPhone,c);
            c.gridx=0; c.gridy=2; p.add(new JLabel("Email:"),c); c.gridx=1; p.add(tfEmail,c);
            c.gridx=0; c.gridy=3; p.add(new JLabel("Username:"),c); c.gridx=1; p.add(tfUsername,c);
            c.gridx=0; c.gridy=4; p.add(new JLabel("Password:"),c); c.gridx=1; p.add(pfPassword,c);
            c.gridx=0; c.gridy=5; p.add(new JLabel("Role:"),c); c.gridx=1; p.add(cbRole,c);

            add(p, BorderLayout.CENTER);
            JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton save = new JButton("Save"); JButton cancel = new JButton("Cancel");
            b.add(cancel); b.add(save);
            add(b, BorderLayout.SOUTH);

            save.addActionListener(ae -> {
                try {
                    User uu = editing==null? new User() : editing;
                    uu.setFullName(tfName.getText().trim());
                    uu.setPhone(tfPhone.getText().trim());
                    uu.setEmail(tfEmail.getText().trim());
                    uu.setUsername(tfUsername.getText().trim());
                    if (editing==null) uu.setPassword(new String(pfPassword.getPassword()));
                    uu.setRole((String)cbRole.getSelectedItem());

                    boolean ok;
                    if (editing==null) ok = userDAO.addUser(uu);
                    else ok = userDAO.updateUser(uu);

                    if (ok) { saved = true; dispose(); } else JOptionPane.showMessageDialog(this,"Save failed.");
                } catch(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Invalid input."); }
            });

            cancel.addActionListener(ae -> dispose());
        }
    }
}
