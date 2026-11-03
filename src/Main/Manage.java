package Main;

import Network.Communication;
import SQL.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Manage extends JPanel {
    private JTable table;
    private JButton register;
    public JPanel panel;
    private JButton principal;
    private JButton update;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "Manage-Scheduler");
        t.setDaemon(true);
        return t;
    });

    public Manage(CardLayout cardLayout, JPanel cardPanel, Conexion conexion, Communication communication, Config config) throws SQLException {
        llenarTabla(conexion);

        scheduler.scheduleWithFixedDelay(
                () -> actualizar(conexion),
                config.getDelay(),
                Math.max(config.getInterval(), 1),
                TimeUnit.SECONDS
        );

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    int idAspiradora = Integer.parseInt((String) table.getValueAt(row, 0));

                    if ("Editar".equals(cellValue)) {
                        try {
                            Edit edit = new Edit(idAspiradora, conexion, communication, config);
                            edit.setSize(600, 400);
                            edit.setContentPane(edit.panel);
                            edit.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            edit.setLocationRelativeTo(null);
                            edit.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent ev) {
                                    actualizar(conexion);
                                }
                            });
                            edit.setVisible(true);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(Manage.this, "Error al abrir edición: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if ("Eliminar".equals(cellValue)) {
                        int confirm = JOptionPane.showConfirmDialog(Manage.this,
                                "¿Está seguro de eliminar la aspiradora con ID " + idAspiradora + "?",
                                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                conexion.eliminarAspiradora(idAspiradora);
                                actualizar(conexion);
                                JOptionPane.showMessageDialog(Manage.this, "Aspiradora eliminada exitosamente");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(Manage.this, "Error al eliminar aspiradora: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        update.addActionListener(e -> {
            update.setEnabled(false);
            Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                try {
                    actualizar(conexion);
                } finally {
                    SwingUtilities.invokeLater(() -> update.setEnabled(true));
                }
            });
        });

        register.addActionListener(e -> {
            try {
                Register registerFrame = new Register(conexion, communication, config);
                registerFrame.setSize(600, 400);
                registerFrame.setContentPane(registerFrame.panel);
                registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                registerFrame.setLocationRelativeTo(null);
                registerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent ev) {
                        actualizar(conexion);
                    }
                });
                registerFrame.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(Manage.this, "Error al abrir registro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        principal.addActionListener(e -> {
            cardLayout.show(cardPanel, "Principal");
        });
    }

    public void llenarTabla(Conexion conexion) throws SQLException {
        conexion.conectar();
        String[][] datos = conexion.obtenerAdministrarAspiradoras();
        if (SwingUtilities.isEventDispatchThread()) {
            renderTabla(datos);
        } else {
            SwingUtilities.invokeLater(() -> renderTabla(datos));
        }
    }

    private void renderTabla(String[][] datos) {
        String[] columnas = {"ID", "IP", "Máquina", "Editar", "Eliminar"};
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

    public void actualizar(Conexion conexion) {
        System.out.println("Actualizando Manage");
        try {
            llenarTabla(conexion);
        } catch (SQLException ex) {
            System.err.println("Error al actualizar tabla Manage: " + ex.getMessage());
        }
    }
}
