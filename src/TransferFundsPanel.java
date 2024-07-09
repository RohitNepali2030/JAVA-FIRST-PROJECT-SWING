import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransferFundsPanel extends JPanel {
    private JTextField fromAccountField;
    private JTextField toAccountField;
    private JTextField amountField;
    private JLabel resultLabel;

    public TransferFundsPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel fromAccountLabel = new JLabel("From Account:");
        fromAccountField = new JTextField(20);
        JLabel toAccountLabel = new JLabel("To Account:");
        toAccountField = new JTextField(20);
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(20);
        JButton transferButton = new JButton("Transfer");
        resultLabel = new JLabel();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(fromAccountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(fromAccountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(toAccountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(toAccountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(transferButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(resultLabel, gbc);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferFunds();
            }
        });
    }

    private void transferFunds() {
        String fromAccount = fromAccountField.getText();
        String toAccount = toAccountField.getText();
        double amount = Double.parseDouble(amountField.getText());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "ROHIT")) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement("SELECT balance FROM accounts WHERE accountNumber = ?");
                 PreparedStatement stmt2 = conn.prepareStatement("UPDATE Accounts SET balance = ? WHERE accountNumber = ?");
                 PreparedStatement stmt3 = conn.prepareStatement("SELECT balance FROM accounts WHERE accountNumber = ?");
                 PreparedStatement stmt4 = conn.prepareStatement("UPDATE Accounts SET balance = ? WHERE accountNumber = ?")) {

                stmt1.setString(1, fromAccount);
                ResultSet rs1 = stmt1.executeQuery();
                if (!rs1.next() || rs1.getDouble("balance") < amount) {
                    resultLabel.setText("Insufficient funds or account not found");
                    conn.rollback();
                    return;
                }
                double fromBalance = rs1.getDouble("balance");

                stmt3.setString(1, toAccount);
                ResultSet rs2 = stmt3.executeQuery();
                if (!rs2.next()) {
                    resultLabel.setText("Recipient account not found");
                    conn.rollback();
                    return;
                }
                double toBalance = rs2.getDouble("balance");

                stmt2.setDouble(1, fromBalance - amount);
                stmt2.setString(2, fromAccount);
                stmt2.executeUpdate();

                stmt4.setDouble(1, toBalance + amount);
                stmt4.setString(2, toAccount);
                stmt4.executeUpdate();

                conn.commit();
                resultLabel.setText("Transfer successful");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                resultLabel.setText("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error: " + e.getMessage());
        }
    }
}
