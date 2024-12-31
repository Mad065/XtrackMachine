package Main;

import SQL.Conexion;

import javax.swing.*;
import java.awt.*;
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

    public Register() throws SQLException {
        llenarComboBox();

        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maquinaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        verify.addActionListener(e -> {
            // Verificar aspiradora
            // Tratar de conectarse por sockets
        });

        register.addActionListener(e -> {
            int idInt = Integer.parseInt(id.getText());
            String ipText = ip.getText();
            int idMaquina = maquina.getSelectedIndex() + 1;
            int idEstado = estado.getSelectedIndex() + 1;


            try {
                Conexion conexion = new Conexion();
                conexion.conectar();
                conexion.registrarAspiradora(idInt, ipText, idMaquina, idEstado);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al realizar la operación en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            this.dispose();
            JOptionPane.showMessageDialog(null, "Aspiradora registrada exitosamente");
        });
    }

    public void llenarComboBox() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.conectar();

        String[] estados = conexion.obtenerEstados();
        String[] maquinas = conexion.obtenerMaquinas();

        DefaultComboBoxModel<String> modelEstados = new DefaultComboBoxModel<>(estados);
        estado.setModel(modelEstados);

        DefaultComboBoxModel<String> modelMaquinas = new DefaultComboBoxModel<>(maquinas);
        maquina.setModel((modelMaquinas));
    }
}
