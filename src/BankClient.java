import javax.swing.*;
import java.awt.*;

public class BankClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Online Banking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel viewBalancePanel = new ViewBalancePanel();
            JPanel transferFundsPanel = new TransferFundsPanel();

            tabbedPane.addTab("View Balance", viewBalancePanel);
            tabbedPane.addTab("Transfer Funds", transferFundsPanel);

            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
