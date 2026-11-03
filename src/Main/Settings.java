package Main;

import Network.Communication;
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
    private JTextField gateway;
    private JTextField broadcast;
    private JTextField mask;

    public boolean actualizarUsuario = false;
    public boolean actualizarMachine = false;

    public String nameUser = "";
    public String nameMachine = "";

    public Settings(Conexion conexion, Communication communication, Config config) throws SQLException {
        obtenerUsuarios(conexion);
        obtenerRed(config);
        obtenerFresadoras(conexion);

        machines.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = machines.rowAtPoint(e.getPoint());
                int col = machines.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    Object cellValue = machines.getValueAt(row, col);
                    nameMachine = (String) machines.getValueAt(row, 0);

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
                        int confirm = JOptionPane.showConfirmDialog(Settings.this,
                                "¿Está seguro de eliminar la máquina '" + nameMachine + "'?",
                                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                conexion.eliminarMaquina(nameMachine);
                                obtenerFresadoras(conexion);
                                JOptionPane.showMessageDialog(Settings.this, "Máquina eliminada exitosamente");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(Settings.this, "Error al eliminar máquina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
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
                    nameUser = (String) users.getValueAt(row, 0);
                    String passwordUser = (String) users.getValueAt(row, 1);

                    if (cellValue.equals("Editar")) {
                        actualizarUsuario = !actualizarUsuario;

                        if (actualizarUsuario) {
                            nameField.setText(nameUser);
                            passwordField.setText(""); // Dejar en blanco; si se deja vacía al actualizar, no se modifica
                            buttonUser.setText("Actualizar usuario");
                        } else {
                            nameField.setText("");
                            passwordField.setText("");
                            buttonUser.setText("Crear nuevo usuario");
                        }
                    }

                    if (cellValue.equals("Eliminar")) {
                        int confirm = JOptionPane.showConfirmDialog(Settings.this,
                                "¿Está seguro de eliminar el usuario '" + nameUser + "'?",
                                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                conexion.eliminarUsuario(nameUser);
                                obtenerUsuarios(conexion);
                                JOptionPane.showMessageDialog(Settings.this, "Usuario eliminado exitosamente");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(Settings.this, "Error al eliminar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        // Actualizar Usuarios en la base de datos
        buttonUser.addActionListener(e -> {
            String newUsername = nameField.getText().trim();
            String newPass = new String(passwordField.getPassword());

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío", "Error de validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (actualizarUsuario) {
                try {
                    conexion.actualizarUsuario(nameUser, newPass, newUsername);
                    actualizarUsuario = false;
                    nameField.setText("");
                    passwordField.setText("");
                    buttonUser.setText("Crear nuevo usuario");
                    obtenerUsuarios(conexion);
                    JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (newPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe especificar una contraseña para el nuevo usuario", "Error de validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    conexion.registrarUsuario(newUsername, newPass);
                    nameField.setText("");
                    passwordField.setText("");
                    obtenerUsuarios(conexion);
                    JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Boton para actualizar las credenciales de red en config
        update.addActionListener(e -> {
            String ssidText = ssid.getText();
            String passwordText = new String(password.getPassword());
            String gatewayText = gateway.getText();
            String broadcastText = broadcast.getText();
            String maskText = mask.getText();

            // Actualizar en config
            config.setSsid(ssidText);
            config.setPassword(passwordText);
            config.setGateway(gatewayText);
            config.setBroadcast(broadcastText);
            config.setMask(maskText);
            JOptionPane.showMessageDialog(this, "Configuración de red guardada correctamente");
        });

        // Boton para actualizar las credenciales de red en ESPs de aspiradoras
        updateESP.addActionListener(e -> {
            String ssidText = ssid.getText();
            String passwordText = new String(password.getPassword());

            updateESP.setEnabled(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

            java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                try {
                    String[] ips = conexion.obtenerIPs();
                    java.util.List<String> fallidas = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

                    java.util.List<java.util.concurrent.CompletableFuture<Void>> futures = Arrays.stream(ips)
                            .map(ip -> java.util.concurrent.CompletableFuture.runAsync(() -> {
                                try {
                                    if (!communication.actualizarRed(ip, ssidText, passwordText)) {
                                        fallidas.add(ip);
                                    }
                                } catch (IOException ex) {
                                    fallidas.add(ip + " (" + ex.getMessage() + ")");
                                }
                            }))
                            .toList();

                    java.util.concurrent.CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0])).join();

                    SwingUtilities.invokeLater(() -> {
                        if (fallidas.isEmpty()) {
                            JOptionPane.showMessageDialog(Settings.this, "Red actualizada exitosamente en todas las aspiradoras (" + ips.length + ")");
                        } else {
                            JOptionPane.showMessageDialog(Settings.this, "No se pudo actualizar la red en:\n" + String.join("\n", fallidas), "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                    });
                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(Settings.this, "Error al obtener aspiradoras: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        updateESP.setEnabled(true);
                        setCursor(java.awt.Cursor.getDefaultCursor());
                    });
                }
            });
        });

        // Actualizar Maquinas en la base de datos
        buttonMachine.addActionListener(e -> {
            String newMachineName = nameMachineField.getText().trim();
            if (newMachineName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de la máquina no puede estar vacío", "Error de validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (actualizarMachine) {
                try {
                    conexion.actualizarMaquina(nameMachine, newMachineName);
                    actualizarMachine = false;
                    nameMachineField.setText("");
                    buttonMachine.setText("Añadir nueva fresadora");
                    obtenerFresadoras(conexion);
                    JOptionPane.showMessageDialog(this, "Máquina actualizada exitosamente");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar máquina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                try {
                    conexion.registrarMaquina(newMachineName);
                    nameMachineField.setText("");
                    obtenerFresadoras(conexion);
                    JOptionPane.showMessageDialog(this, "Máquina registrada exitosamente");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al registrar máquina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void obtenerRed(Config config) {
        // Obtener ssid y contraseña
        String ssidText = config.getSsid();
        String passwordText = config.getPassword();
        String gatewayText = config.getGateway();
        String broadcastText = config.getBroadcast();
        String maskText = config.getMask();

        password.setText(passwordText);
        ssid.setText(ssidText);
        gateway.setText(gatewayText);
        broadcast.setText(broadcastText);
        mask.setText(maskText);
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
