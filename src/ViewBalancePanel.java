import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewBalancePanel extends JPanel {
    private JTextField accountNumberField;
    private JLabel balanceLabel;

    public ViewBalancePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberField = new JTextField(20);
        JButton viewBalanceButton = new JButton("View Balance");
        balanceLabel = new JLabel();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(accountNumberLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(accountNumberField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(viewBalanceButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(balanceLabel, gbc);

        viewBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBalance();
            }
        });
    }

    private void viewBalance() {
        String accountNumber = accountNumberField.getText();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "ROHIT");
             PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM Accounts WHERE accountNumber = ?")) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                balanceLabel.setText("Balance: " + rs.getDouble("balance"));
            } else {
                balanceLabel.setText("Account not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            balanceLabel.setText("Error: " + e.getMessage());
        }
    }
}
