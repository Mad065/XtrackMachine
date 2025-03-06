package Main;

import Communication.Communication;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.List;

public class Search extends JFrame {
    public JPanel panel;
    private JTable table;
    private JButton select;
    private JButton search;
    public Integer ipMachine = 0;

    public Search(Communication communication, Config config) throws IOException {
        llenarTabla(communication, config);

        search.addActionListener(e -> {
            try {
                llenarTabla(communication, config);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        select.addActionListener(e -> {
            int row = table.getSelectedRow();
            ipMachine = (Integer) table.getValueAt(row, 1);

            // Cerrar ventana
            this.dispose();
        });
    }

    public void llenarTabla(Communication communication, Config config) throws IOException {
        List<String> machines = communication.obtenerIPs(config.getBroadcast());
        String[][] datos = new String[machines.size()][2];

        for (int i = 0; i < machines.size(); i++) {
            datos[i][0] = String.valueOf(i+1);
            datos[i][1] = machines.get(i);
        }

        String[] columnas = {"Numero", "IP"};

        // Crear el modelo de la tabla con una sola columna
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas);

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
