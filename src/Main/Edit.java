package Main;

import SQL.Conexion;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Edit extends JFrame {
    private JComboBox estado;
    private JComboBox maquina;
    private JTextField ip;
    private JTextField id;
    private JLabel idLabel;
    private JLabel ipLabel;
    private JLabel estadoLabel;
    private JLabel maquinaLabel;
    private JButton verify;
    private JButton update;
    public JPanel panel;

    protected int idAspiradora;


    public Edit(int idAspiradora, Conexion conexion) throws SQLException {

        this.idAspiradora = idAspiradora;

        llenarComboBox(conexion);
        llenarDatos(conexion);

        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maquinaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        verify.addActionListener(e -> {
            // Verificar aspiradora
            // Tratar de conectarse por sockets
        });

        update.addActionListener(e -> {
            int idInt = Integer.parseInt(id.getText());
            String ipText = ip.getText();
            String nameMaquina = String.valueOf(maquina.getSelectedItem());
            String nameEstado = String.valueOf(estado.getSelectedItem());

            int idMaquina = 1;
            int idEstado = 1;

            try {
                idMaquina = conexion.obtenerIdMaquina(nameMaquina);
                idEstado = conexion.obtenerIdEstado(nameEstado);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                conexion.conectar();
                conexion.actualizarAspiradora(idInt, ipText, idMaquina, idEstado);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al realizar la operación en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            this.dispose();
            JOptionPane.showMessageDialog(null, "Aspiradora actualizada exitosamente");
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

    public void llenarDatos(Conexion conexion) throws SQLException {
        conexion.conectar();

        String[] datos = conexion.obtenerAspiradora(idAspiradora);

        id.setText(datos[0]);
        ip.setText(datos[1]);
        maquina.setSelectedItem(datos[2]);
        estado.setSelectedItem(datos[3]);
    }
}
