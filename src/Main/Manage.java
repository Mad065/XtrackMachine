package Main;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Manage extends JFrame {
    private JTable table;
    private JButton register;
    public JPanel panel;
    private JButton principal;

    public Manage() {
        principal.addActionListener(e -> {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
            Principal frame = null;
            try {
                frame = new Principal();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            this.setContentPane(frame.panel);
            this.revalidate();
            this.repaint();
        });
    }
}
