package Main;

import SQL.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class Settings extends JFrame {
    public JPanel panel;
    private JTable users;
    private JButton buttonUser;
    protected JTextField password;
    private JTextField ssid;
    private JButton update;
    private JTable machines;
    private JButton buttonMachine;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField nameMachine;

    public Settings() throws SQLException {
        obtenerUsuarios();
        obtenerRed();
        obtenerFresadoras();

        // Para actualizar usuarios y fresadoras, al presionar editar poner esos datos en los textfield correspondientes y actualizar texto de botones
        buttonUser.addActionListener(e -> {
            // TODO actualizar base de datos agregando lo de nameField y passwordField
        });

        update.addActionListener(e -> {
            // TODO actualizar ssid y password en config.properties y llamar funcion de actualizar red de esp32 actualizarRed() llamar para cada esp
        });

        buttonMachine.addActionListener(e -> {
            // TODO actualizar base de datos agrgando lo de nameMachine
        });
    }

    public void obtenerRed() {
        // Obtener ssid y contraseña
        Config config = new Config();
        String ssidText = config.ssid;
        String passwordText = config.password;

        password.setText(passwordText);
        ssid.setText(ssidText);
    }

    public void obtenerFresadoras() throws SQLException {
        Conexion conexion = new Conexion();
        String[] maquinas = conexion.obtenerMaquinas();

        // Matriz de datos
        String[][] datos = new String[maquinas.length][3];
        for(int i = 0; i < maquinas.length; i++) {
            datos[i][0] = maquinas[i];
            datos[i][1] = "Editar";
            datos[i][2] = "Eliminar";
        }

        String[] columnas = {"Nombre", "Editar", "Eliminar"};

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        machines.setModel(modelo);
        machines.setRowHeight(44); // altura filas
        machines.getTableHeader().setReorderingAllowed(true); // reordenamiento
        machines.getTableHeader().setResizingAllowed(true); // redimensión

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Centrar el texto

        for (int i = 0; i < machines.getColumnCount(); i++) {
            machines.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void obtenerUsuarios() throws SQLException {
        Conexion conexion = new Conexion();
        String[][] datos = conexion.obtenerUsuarios();

        String[] columnas = {"Nombre","Contraseña", "Editar", "Eliminar"};

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        users.setModel(modelo);
        users.setRowHeight(44); // altura filas
        users.getTableHeader().setReorderingAllowed(true); // reordenamiento
        users.getTableHeader().setResizingAllowed(true); // redimensión

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Centrar el texto

        for (int i = 0; i < users.getColumnCount(); i++) {
            users.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
