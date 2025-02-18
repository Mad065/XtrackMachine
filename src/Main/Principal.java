package Main;

import Communication.Communication;
import SQL.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class Principal extends JPanel {
    private JButton stop;
    private JButton start;
    private JButton reboot;
    public JPanel panel;
    private JTable table;
    private JButton manage;
    private JButton update;
    private JButton settings;

    public Principal(CardLayout cardLayout, JPanel cardPanel) throws SQLException {
        llenarTabla();

        Communication communication = new Communication();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    String ipAspiradora = (String) table.getValueAt(row, 1);

                    if (cellValue.equals("Detener")) {
                        // Detener aspiradora
                        try {
                            if (!communication.detenerAspiradora(ipAspiradora)) {
                                JOptionPane.showMessageDialog(null, "Error al detener", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    if (cellValue.equals("Reiniciar")) {
                        // Reiniciar aspiradora
                        try {
                            if (!communication.reiniciarAspiradora(ipAspiradora)) {
                                JOptionPane.showMessageDialog(null, "Error al reiniciar", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    if (cellValue.equals("Iniciar")) {
                        // Iniciar aspiradora
                        try {
                            if (!communication.iniciarAspiradora(ipAspiradora)) {
                                JOptionPane.showMessageDialog(null, "Error al iniciar", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        update.addActionListener( e -> {
            actualizar(communication);
        });

        start.addActionListener(e -> {
            // Iniciar todas las aspiradoras
            try {
                String[] ips = obtenerIPs();
                Arrays.stream(ips).forEach(ip ->
                {
                    try {
                        communication.iniciarAspiradora(ip);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        stop.addActionListener(e -> {
            // Detener todas las aspiradoras
            try {
                String[] ips = obtenerIPs();
                Arrays.stream(ips).forEach(ip ->
                {
                    try {
                        communication.detenerAspiradora(ip);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        reboot.addActionListener(e -> {
            // Reiniciar todas las aspiradoras
            try {
                String[] ips = obtenerIPs();
                Arrays.stream(ips).forEach(ip ->
                {
                    try {
                        communication.reiniciarAspiradora(ip);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        manage.addActionListener(e -> {
            cardLayout.show(cardPanel, "Manage");
        });

        settings.addActionListener(e -> {
            Settings settings = null;
            settings = new Settings();
            settings.setSize(600, 400);
            settings.setContentPane(settings.panel);
            settings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            settings.setLocationRelativeTo(null);
            settings.setVisible(true);
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
        table.setRowHeight(44); // altura filas
        table.getTableHeader().setReorderingAllowed(true); // reordenamiento
        table.getTableHeader().setResizingAllowed(true); // redimensión

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Centrar el texto

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public String[] obtenerIPs() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.conectar();

        String[] ips = conexion.obtenerIPs();

        return ips;
    }

    public void actualizar(Communication communication) {
        // Actualizar estados de todas las aspiradoras
        try {
            String[] ips = obtenerIPs();
            Arrays.stream(ips).forEach(ip ->
            {
                try {
                    communication.actualizarEstado(ip);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Llenar tabla
        try {
            llenarTabla();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
