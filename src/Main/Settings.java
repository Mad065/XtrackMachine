package Main;

import Communication.Communication;
import SQL.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class Settings extends JFrame {
    public JPanel panel;
    private JTable users;
    private JButton buttonUser;
    protected JPasswordField password;
    private JTextField ssid;
    private JButton update;
    private JTable machines;
    private JButton buttonMachine;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField nameMachineField;
    private JButton updateESP;

    public boolean actualizarUsuario = false;
    public boolean actualizarMachine = false;

    public String nameUser = "";
    public String nameMachine = "";

    public Settings(Conexion conexion, Communication communication, Config config) throws SQLException {
        obtenerUsuarios(conexion);
        obtenerRed(config);
        obtenerFresadoras(conexion);

        // TODO poder establecer y editar la ip de la red y el broadcast

        machines.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = machines.rowAtPoint(e.getPoint());
                int col = machines.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = machines.getValueAt(row, col);
                    nameMachine = (String) machines.getValueAt(row, 1);

                    if (cellValue.equals("Editar")) {
                        actualizarMachine = !actualizarMachine;

                        if (actualizarMachine) {
                            nameMachineField.setText(nameMachine);
                            buttonMachine.setText("Actualizar fresadora");
                        } else {
                            nameMachineField.setText("");
                            buttonMachine.setText("Añadir nueva fresadora");
                        }
                    }

                    if (cellValue.equals("Eliminar")) {
                        try {
                            conexion.eliminarMaquina(nameMachine);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = users.rowAtPoint(e.getPoint());
                int col = users.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = users.getValueAt(row, col);
                    nameUser = (String) users.getValueAt(row, 1);
                    String passwordUser = (String) users.getValueAt(row, 1);

                    if (cellValue.equals("Editar")) {
                        actualizarUsuario = !actualizarUsuario;

                        if (actualizarUsuario) {
                            nameField.setText(nameUser);
                            passwordField.setText(passwordUser);
                            buttonUser.setText("Actualizar usuario");
                        } else {
                            nameField.setText("");
                            passwordField.setText("");
                            buttonUser.setText("Crear nuevo usuario");
                        }
                    }

                    if (cellValue.equals("Eliminar")) {
                        try {
                            conexion.eliminarUsuario(nameUser);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        // Actualizar Usuarios en la base de datos
        buttonUser.addActionListener(e -> {
            if (actualizarUsuario) {
                try {
                    conexion.actualizarUsuario(nameUser, Arrays.toString(passwordField.getPassword()), nameField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    conexion.registrarUsuario(nameField.getText(), Arrays.toString(passwordField.getPassword()));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Boton para actualizar las credenciales de red en config
        update.addActionListener(e -> {
            String ssidText = ssid.getText();
            String passwordText = Arrays.toString(password.getPassword());

            // Actualizar en config
            config.setSsid(ssidText);
            config.setPassword(passwordText);
        });

        // Boton para actualizar las credenciales de red en ESPs de aspiradoras
        updateESP.addActionListener(e -> {
            String ssidText = ssid.getText();
            String passwordText = Arrays.toString(password.getPassword());

            // Actualizar red de las esp de las aspiradoras
            try {
                String[] ips = conexion.obtenerIPs();
                Arrays.stream(ips).forEach(ip ->
                {
                    try {
                        if (!communication.actualizarRed(ip, ssidText, passwordText)) {
                            JOptionPane.showMessageDialog(null, "Error al actualizar red de aspiradora " + ip, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Actualizar Maquinas en la base de datos
        buttonMachine.addActionListener(e -> {
            if (actualizarMachine) {
                try {
                    conexion.actualizarMaquina(nameMachine, nameMachineField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    conexion.registrarMaquina(nameMachineField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void obtenerRed(Config config) {
        // Obtener ssid y contraseña
        String ssidText = config.ssid;
        String passwordText = config.password;

        password.setText(passwordText);
        ssid.setText(ssidText);
    }

    public void obtenerFresadoras(Conexion conexion) throws SQLException {
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

    public void obtenerUsuarios(Conexion conexion) throws SQLException {
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
