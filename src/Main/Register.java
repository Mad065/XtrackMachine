package Main;

import Network.Communication;
import SQL.Conexion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

public class Register extends JFrame {
    private JTextField id;
    private JTextField ip;
    private JComboBox maquina;
    private JComboBox estado;
    private JButton verify;
    private JButton register;
    public JPanel panel;
    private JLabel idLabel;
    private JLabel ipLabel;
    private JLabel maquinaLabel;
    private JLabel estadoLabel;
    private JLabel verification;
    private JButton searchButton;
    private JLabel verifyLabel;

    public Register(Conexion conexion, Communication communication, Config config) throws SQLException {

        llenarComboBox(conexion);

        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maquinaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        verify.addActionListener(e -> {
            String ipStr = ip.getText().trim();
            if (ipStr.isEmpty()) {
                verifyLabel.setText("Ingrese una IP primero");
                return;
            }
            verify.setEnabled(false);
            verifyLabel.setText("Verificando...");
            java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
                try {
                    boolean ok = communication.verificarConexion(ipStr);
                    SwingUtilities.invokeLater(() ->
                        verifyLabel.setText(ok ? "Conexión verificada" : "No se pudo verificar la conexión")
                    );
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() ->
                        verifyLabel.setText("Error: " + ex.getMessage())
                    );
                } finally {
                    SwingUtilities.invokeLater(() -> verify.setEnabled(true));
                }
            });
        });

        searchButton.addActionListener(e -> {
            Search search = new Search(this, communication, config);
            search.setSize(600, 400);
            search.setContentPane(search.panel);
            search.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            search.setLocationRelativeTo(this);
            search.setVisible(true);

            if (search.ipMachine != null && !search.ipMachine.trim().isEmpty()) {
                ip.setText(search.ipMachine);
            }
        });

        register.addActionListener(e -> {
            int idInt;
            try {
                idInt = Integer.parseInt(id.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número entero válido", "Error de validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ipText = ip.getText().trim();
            if (ipText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La dirección IP no puede estar vacía", "Error de validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nameMaquina = String.valueOf(maquina.getSelectedItem());
            String nameEstado = String.valueOf(estado.getSelectedItem());

            int idMaquina;
            int idEstado;

            try {
                idMaquina = conexion.obtenerIdMaquina(nameMaquina);
                idEstado = conexion.obtenerIdEstado(nameEstado);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al obtener datos relacionados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                conexion.conectar();
                conexion.registrarAspiradora(idInt, ipText, idMaquina, idEstado);
                this.dispose();
                JOptionPane.showMessageDialog(null, "Aspiradora registrada exitosamente");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar la aspiradora en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void llenarComboBox(Conexion conexion) throws SQLException {
        conexion.conectar();

        String[] estados = conexion.obtenerEstados();
        String[] maquinas = conexion.obtenerMaquinas();

        DefaultComboBoxModel<String> modelEstados = new DefaultComboBoxModel<>(estados);
        estado.setModel(modelEstados);

        DefaultComboBoxModel<String> modelMaquinas = new DefaultComboBoxModel<>(maquinas);
        maquina.setModel((modelMaquinas));
    }
}