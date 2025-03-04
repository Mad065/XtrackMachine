package Main;

import Communication.Communication;
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

    public Manage(CardLayout cardLayout, JPanel cardPanel, Conexion conexion, Communication communication, Config config) throws SQLException {
        llenarTabla(conexion);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Actializar cada 3 segundos
        scheduler.scheduleAtFixedRate(
                () -> actualizar(conexion),
                config.delay, // Retraso inicial
                config.interval, // Intervalo
                TimeUnit.SECONDS // Unidad de tiempo
        );

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    int idAspiradora = Integer.parseInt((String) table.getValueAt(row, 0)) ;
                    if (cellValue.equals("Editar")) {
                        Edit edit = null;
                        try {
                            edit = new Edit(idAspiradora, conexion);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        edit.setSize(600, 400);
                        edit.setContentPane(edit.panel);
                        edit.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        edit.setLocationRelativeTo(null);
                        edit.setVisible(true);
                    }

                    if (cellValue.equals("Eliminar")) {
                        try {
                            conexion.eliminarAspiradora(idAspiradora);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        update.addActionListener(e -> {
            actualizar(conexion);
        });

        register.addActionListener(e -> {
            Register register = null;
            try {
                register = new Register(conexion, communication, config);
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

    public void llenarTabla(Conexion conexion) throws SQLException {
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
        // Llenar tabla
        try {
            llenarTabla(conexion);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
