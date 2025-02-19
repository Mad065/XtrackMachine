package Main;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    public MainFrame() throws SQLException {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());


            UIManager.put("Table.selectionBackground", new Color(170, 199, 255));
            UIManager.put("Table.selectionForeground", new Color(0, 20, 49));
            UIManager.put("Table.gridColor", new Color(62, 71, 89));
            UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 14));
            UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 16));
            // Mostrar grid
            // UIManager.put("Table.gridColor", new Color(62, 71, 89));
            // UIManager.put("Table.showHorizontalLines", true);
            // UIManager.put("Table.showVerticalLines", true);

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        Principal principalPanel = new Principal(cardLayout, cardPanel);
        Manage managePanel = new Manage(cardLayout, cardPanel);

        cardPanel.add(principalPanel.panel, "Principal");
        cardPanel.add(managePanel.panel, "Manage");

        this.add(cardPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
