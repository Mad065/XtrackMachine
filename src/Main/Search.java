package Main;

import Network.Communication;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.io.IOException;
import java.util.List;

public class Search extends JDialog {
    public JPanel panel;
    private JTable table;
    private JButton select;
    private JButton search;
    public String ipMachine = "";

    public Search(Frame parent, Communication communication, Config config) {
        super(parent, "Buscar Aspiradoras", true);
        init(communication, config);
    }

    public Search(Communication communication, Config config) {
        this((Frame) null, communication, config);
    }

    private void init(Communication communication, Config config) {
        renderTabla(new String[0][2]);
        buscarAspiradorasAsync(communication, config);

        search.addActionListener(e -> buscarAspiradorasAsync(communication, config));

        select.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                ipMachine = (String) table.getValueAt(row, 1);
                // Cerrar ventana
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor seleccione una aspiradora de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void buscarAspiradorasAsync(Communication communication, Config config) {
        search.setEnabled(false);
        search.setText("Buscando...");
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
            try {
                String broadcast = config.getBroadcast();
                String targetBroadcast = (broadcast != null && !broadcast.trim().isEmpty()) ? broadcast.trim() : "255.255.255.255";
                List<String> machines = communication.obtenerIPs(targetBroadcast);
                String[][] datos = new String[machines.size()][2];

                for (int i = 0; i < machines.size(); i++) {
                    datos[i][0] = String.valueOf(i + 1);
                    datos[i][1] = machines.get(i);
                }

                SwingUtilities.invokeLater(() -> renderTabla(datos));
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error al buscar aspiradoras: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                );
            } finally {
                SwingUtilities.invokeLater(() -> {
                    search.setEnabled(true);
                    search.setText("Recargar");
                    setCursor(java.awt.Cursor.getDefaultCursor());
                });
            }
        });
    }

    private void renderTabla(String[][] datos) {
        String[] columnas = {"Numero", "IP"};

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
}
