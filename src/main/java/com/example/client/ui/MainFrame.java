package com.example.client.ui;

import com.example.client.net.NetworkClient;
import com.example.common.model.Tenant;
import com.example.common.protocol.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final NetworkClient networkClient;
    private final Gson gson = new Gson();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfId;
    private JTextField tfName;
    private JTextField tfContact;
    private JTextField tfGender;
    private JTextField tfRoom;

    public MainFrame(NetworkClient client) {
        super("Tenant Manager");
        this.networkClient = client;
        buildUI();
        wireNetwork();
        fetchAll();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact", "Gender", "Room"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        // Labels and fields
        JLabel lbId = new JLabel("ID:");
        tfId = new JTextField(10); tfId.setEditable(false);
        JLabel lbName = new JLabel("Name:");
        tfName = new JTextField(20);
        JLabel lbContact = new JLabel("Contact:");
        tfContact = new JTextField(15);
        JLabel lbGender = new JLabel("Gender:");
        tfGender = new JTextField(10);
        JLabel lbRoom = new JLabel("Room:");
        tfRoom = new JTextField(5);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        panel.add(scroll);
        panel.add(lbId); panel.add(tfId);
        panel.add(lbName); panel.add(tfName);
        panel.add(lbContact); panel.add(tfContact);
        panel.add(lbGender); panel.add(tfGender);
        panel.add(lbRoom); panel.add(tfRoom);
        panel.add(btnAdd); panel.add(btnUpdate); panel.add(btnDelete); panel.add(btnRefresh);

        // Layout constraints using SpringLayout
        layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, scroll, 300, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.NORTH, lbId, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, lbId, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tfId, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, tfId, 60, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, lbName, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, lbName, 200, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tfName, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, tfName, 250, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, lbContact, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, lbContact, 500, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tfContact, 20, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, tfContact, 560, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, lbGender, 60, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, lbGender, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tfGender, 60, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, tfGender, 60, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, lbRoom, 60, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, lbRoom, 200, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, tfRoom, 60, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, tfRoom, 250, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, btnAdd, 110, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, btnAdd, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, btnUpdate, 110, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, btnUpdate, 90, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, btnDelete, 110, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, btnDelete, 180, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, btnRefresh, 110, SpringLayout.SOUTH, scroll);
        layout.putConstraint(SpringLayout.WEST, btnRefresh, 270, SpringLayout.WEST, panel);

        setContentPane(panel);

        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tfId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
                tfName.setText(String.valueOf(tableModel.getValueAt(row, 1)));
                tfContact.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                tfGender.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                tfRoom.setText(String.valueOf(tableModel.getValueAt(row, 4)));
            }
        });

        // Button actions
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> fetchAll());
    }

    private void wireNetwork() {
        networkClient.setOnMessage(msg -> {
            String action = msg.getAction();
            if ("read_all".equals(action)) {
                SwingUtilities.invokeLater(() -> updateTableFromReadAll(msg));
            } else if ("broadcast".equals(action)) {
                // On any server-side change, refresh list
                fetchAll();
            }
        });
    }

    private void updateTableFromReadAll(Message<?> msg) {
        java.lang.reflect.Type listType = new TypeToken<List<Tenant>>(){}.getType();
        List<Tenant> tenants = gson.fromJson(gson.toJson(msg.getData()), listType);
        tableModel.setRowCount(0);
        for (Tenant t : tenants) {
            tableModel.addRow(new Object[]{ t.getUser_id(), t.getName(), t.getContact_number(), t.getGender(), t.getRoomNumber() });
        }
    }

    private void fetchAll() {
        new Thread(() -> {
            Message<Object> req = new Message<>("read_all", null);
            networkClient.sendMessage(req);
        }, "FetchAll").start();
    }

    private void onAdd() {
        new Thread(() -> {
            try {
                Tenant t = new Tenant(null, tfContact.getText(), tfGender.getText(), parseInt(tfRoom.getText()), tfName.getText());
                Message<Tenant> req = new Message<>("create", t);
                networkClient.sendMessage(req);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }, "AddThread").start();
    }

    private void onUpdate() {
        new Thread(() -> {
            try {
                Integer id = parseInt(tfId.getText());
                if (id == null) { showError("Select a row first"); return; }
                Tenant t = new Tenant(id, tfContact.getText(), tfGender.getText(), parseInt(tfRoom.getText()), tfName.getText());
                Message<Tenant> req = new Message<>("update", t);
                networkClient.sendMessage(req);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }, "UpdateThread").start();
    }

    private void onDelete() {
        new Thread(() -> {
            try {
                Integer id = parseInt(tfId.getText());
                if (id == null) { showError("Select a row first"); return; }
                Message<Object> req = new Message<>("delete", Map.of("user_id", id));
                networkClient.sendMessage(req);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }, "DeleteThread").start();
    }

    private Integer parseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }

    private void showError(String msg) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE));
    }
}
