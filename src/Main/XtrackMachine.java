package Main;

import Network.Communication;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import SQL.Conexion;
import java.awt.*;
import java.sql.SQLException;

public class XtrackMachine extends JFrame {
    private JTextField username;
    private JTextField password;
    private JButton start;
    protected JPanel panel;

    public XtrackMachine()  {
        Conexion conexion = new Conexion();
        Communication communication = new Communication();
        Config config = new Config();


        conexion.conectar();

        start.addActionListener(e -> {
            try {
                String usernameText = username.getText();
                String passwordText = password.getText();
                if (conexion.verificarUsuario(usernameText, passwordText)) {
                    try {
                        UIManager.setLookAndFeel(new FlatDarkLaf());

                        UIManager.put("Button.arc", 12);
                        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 20));
                    } catch (Exception ex) {
                        System.err.println("Fallo al inicializar FlatLaf: " + ex.getMessage());
                    }

                    MainFrame frame = new MainFrame(conexion, communication, config);

                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Inicio fallido");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

            UIManager.put("Button.arc", 12);
        } catch (Exception e) {
            System.err.println("Fallo al inicializar FlatLaf: " + e.getMessage());
        }

        XtrackMachine frame = new XtrackMachine();
        frame.setSize(700, 400);
        frame.setContentPane(frame.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
