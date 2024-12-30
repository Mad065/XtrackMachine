package Main;

import SQL.Conexion;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Principal extends JFrame {
    private JButton stop;
    private JButton start;
    private JButton reboot;
    public JPanel panel;
    private JTable table;
    private JButton manage;

    public Principal() throws SQLException {
        LlenarTabla();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());  // Obtener fila
                int col = table.columnAtPoint(e.getPoint()); // Obtener columna

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    if (cellValue == "Detener") {
                        // Detener aspiradora
                    }
                    if (cellValue == "Reiniciar") {
                        // Reiniciar aspiradora
                    }
                    if (cellValue == "Iniciar") {
                        // Iniciar aspiradora
                    }
                }
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
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
            Manage frame = new Manage();
            this.setContentPane(frame.panel);
            this.revalidate();
            this.repaint();
        });
    }


    public void LlenarTabla() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.conectar();

        String[][] datos = conexion.obtenerAspiradoras();

        if (datos.length == 0) {
            JOptionPane.showMessageDialog(null, "No hay aspiradoras registradas");
            return;
        }

        String[] columnas = {"ID", "IP", "Máquina", "Estado", "Encendido", "Detener", "Reiniciar", "Iniciar"};
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas);

        table.setModel(modelo);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
    }

}
