package Main;

import SQL.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class Manage extends JPanel {
    private JTable table;
    private JButton register;
    public JPanel panel;
    private JButton principal;
    private JButton update;

    public Manage(CardLayout cardLayout, JPanel cardPanel) throws SQLException {
        llenarTabla();

        update.addActionListener(e -> {
            try {
                llenarTabla();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        register.addActionListener(e -> {
            Register register = null;
            try {
                register = new Register();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            register.setSize(600, 400);
            register.setContentPane(register.panel);
            register.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            register.setLocationRelativeTo(null);
            register.setVisible(true);
        });

        principal.addActionListener(e -> {
            cardLayout.show(cardPanel, "Principal");
        });
    }

    public void llenarTabla() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.conectar();

        String[][] datos = conexion.obtenerAdministrarAspiradoras();

        String[] columnas = {"ID", "IP", "Máquina", "Editar", "Eliminar"};
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(modelo);

    }
}
