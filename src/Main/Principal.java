package Main;

import Network.Communication;
import SQL.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Principal extends JPanel {
    private JButton start;
    public JPanel panel;
    private JTable table;
    private JButton manage;
    private JButton update;
    private JButton settings;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "Principal-Scheduler");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService networkExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public Principal(CardLayout cardLayout, JPanel cardPanel, Conexion conexion, Communication communication, Config config) throws SQLException {
        llenarTabla(conexion);

        // Actualizar periódicamente con delay fijo para evitar solapamientos
        scheduler.scheduleWithFixedDelay(
                () -> actualizar(conexion, communication),
                config.getDelay(), // Retraso inicial
                Math.max(config.getInterval(), 1), // Intervalo mínimo de 1 seg
                TimeUnit.SECONDS
        );

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    String ipAspiradora = (String) table.getValueAt(row, 1);

                    if ("Iniciar".equals(cellValue)) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        networkExecutor.submit(() -> {
                            try {
                                if (!communication.iniciarAspiradora(ipAspiradora)) {
                                    SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(Principal.this, "Error al iniciar aspiradora " + ipAspiradora, "Error", JOptionPane.ERROR_MESSAGE)
                                    );
                                } else {
                                    actualizar(conexion, communication);
                                }
                            } catch (IOException ex) {
                                SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(Principal.this, "Fallo de comunicación con " + ipAspiradora + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                                );
                            } finally {
                                SwingUtilities.invokeLater(() -> setCursor(Cursor.getDefaultCursor()));
                            }
                        });
                    }
                }
            }
        });

        update.addActionListener(e -> {
            update.setEnabled(false);
            networkExecutor.submit(() -> {
                try {
                    actualizar(conexion, communication);
                } finally {
                    SwingUtilities.invokeLater(() -> update.setEnabled(true));
                }
            });
        });

        start.addActionListener(e -> {
            start.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            networkExecutor.submit(() -> {
                try {
                    String[] ips = conexion.obtenerIPs();
                    List<CompletableFuture<Void>> startFutures = Arrays.stream(ips)
                            .map(ip -> CompletableFuture.runAsync(() -> {
                                try {
                                    if (!communication.iniciarAspiradora(ip)) {
                                        System.err.println("No se pudo iniciar aspiradora: " + ip);
                                    }
                                } catch (IOException ex) {
                                    System.err.println("Error comunicando con " + ip + ": " + ex.getMessage());
                                }
                            }, networkExecutor))
                            .toList();

                    CompletableFuture.allOf(startFutures.toArray(new CompletableFuture[0])).join();
                    actualizar(conexion, communication);
                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(Principal.this, "Error al consultar aspiradoras: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        start.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    });
                }
            });
        });

        manage.addActionListener(e -> {
            cardLayout.show(cardPanel, "Manage");
        });

        settings.addActionListener(e -> {
            try {
                Settings settingsWindow = new Settings(conexion, communication, config);
                settingsWindow.setSize(600, 400);
                settingsWindow.setContentPane(settingsWindow.panel);
                settingsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                settingsWindow.setLocationRelativeTo(null);
                settingsWindow.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(Principal.this, "Error al abrir ajustes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void llenarTabla(Conexion conexion) throws SQLException {
        conexion.conectar();
        String[][] datos = conexion.obtenerAspiradoras();
        if (SwingUtilities.isEventDispatchThread()) {
            renderTabla(datos);
        } else {
            SwingUtilities.invokeLater(() -> renderTabla(datos));
        }
    }

    private void renderTabla(String[][] datos) {
        String[] columnas = {"ID", "IP", "Máquina", "Estado", "Iniciar"};
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

    public void actualizar(Conexion conexion, Communication communication) {
        System.out.println("Actualizando Principal en paralelo...");
        try {
            String[] ips = conexion.obtenerIPs();
            List<CompletableFuture<Void>> futures = Arrays.stream(ips)
                    .map(ip -> CompletableFuture.runAsync(() -> {
                        try {
                            communication.actualizarEstado(ip, conexion);
                        } catch (Exception ex) {
                            System.err.println("Fallo al actualizar estado de " + ip + ": " + ex.getMessage());
                        }
                    }, networkExecutor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            llenarTabla(conexion);
        } catch (SQLException ex) {
            System.err.println("Error de base de datos durante actualización de Principal: " + ex.getMessage());
        }
    }
}
