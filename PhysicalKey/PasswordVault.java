import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PasswordVault extends JFrame {

    private DefaultTableModel tableModel;
//The actual program
    public PasswordVault() {
        setTitle("Password Vault");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table Columns
        String[] columns = {"Website", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0);

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Input fields
        JTextField websiteField = new JTextField(10);
        JTextField usernameField = new JTextField(10);
        JTextField passwordField = new JTextField(10);

        JButton addButton = new JButton("Add");
        JButton saveButton = new JButton("Save to File");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Website:"));
        inputPanel.add(websiteField);
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(addButton);
        inputPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        loadVault();

        addButton.addActionListener(e -> {
            String site = websiteField.getText().trim();
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (site.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            tableModel.addRow(new String[]{site, user, pass});

            websiteField.setText("");
            usernameField.setText("");
            passwordField.setText("");
        });

        saveButton.addActionListener(e -> saveVault());
    }

    // Load from vault.txt
    private void loadVault() {
        File file = new File("vault.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    tableModel.addRow(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading vault.txt");
        }
    }

    // Save to vault.txt
    private void saveVault() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("vault.txt"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.println(
                    tableModel.getValueAt(i, 0) + "|" +
                    tableModel.getValueAt(i, 1) + "|" +
                    tableModel.getValueAt(i, 2)
                );
            }
            JOptionPane.showMessageDialog(this, "Saved!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving vault.txt");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordVault().setVisible(true));
    }
}
