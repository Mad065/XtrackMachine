package Main;

import SQL.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Principal extends JPanel {
    private JButton stop;
    private JButton start;
    private JButton reboot;
    public JPanel panel;
    private JTable table;
    private JButton manage;
    private JButton update;

    public Principal(CardLayout cardLayout, JPanel cardPanel) throws SQLException {
        llenarTabla();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    if (cellValue.equals("Detener")) {
                        // Detener aspiradora
                    }

                    if (cellValue.equals("Reiniciar")) {
                        // Reiniciar aspiradora
                    }

                    if (cellValue.equals("Iniciar")) {
                        // Iniciar aspiradora
                    }
                }
            }
        });

        update.addActionListener(e -> {
            try {
                llenarTabla();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        start.addActionListener(e -> {
            // Iniciar todas las aspiradoras
        });

        stop.addActionListener(e -> {
            // Detener todas las aspiradoras
        });

        reboot.addActionListener(e -> {
            // Reiniciar todas las aspiradoras
        });

        manage.addActionListener(e -> {
            cardLayout.show(cardPanel, "Manage");
        });
    }

    public void llenarTabla() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.conectar();

        String[][] datos = conexion.obtenerAspiradoras();

        String[] columnas = {"ID", "IP", "Máquina", "Estado", "Encendido/Apagado", "Detener", "Reiniciar", "Iniciar"};
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(modelo);

    }
}
