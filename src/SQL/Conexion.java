package SQL;

import java.sql.*;

public class Conexion {

    Connection connection;
    String url = "jdbc:mysql://127.0.0.1:3306/GyroCleaner";
    String user = "root";
    String password = "";

    public void conectar() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean verificarUsuario(String usuario, String contrasena) throws SQLException {
        String query = "SELECT * FROM Usuario WHERE nombre = ? AND contraseña = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, usuario);
        ps.setString(2, contrasena);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public String[][] obtenerAspiradoras() throws SQLException {

        // Consulta SQL
        String query = """
                SELECT a.id, a.ip, m.nombre AS maquina, e.nombre AS estado, a.encendido
                FROM Aspiradora a
                INNER JOIN Maquina m ON a.maquina = m.id
                INNER JOIN Estado e ON a.estado = e.id;
                """;

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new String[0][0];
        }

        rs.beforeFirst();

        String[][] datos = new String[rowCount][8];
        int i = 0;

        while (rs.next()) {
            datos[i][0] = String.valueOf(rs.getInt("id"));
            datos[i][1] = rs.getString("ip");
            datos[i][2] = rs.getString("maquina");
            datos[i][3] = rs.getString("estado");
            datos[i][4] = rs.getBoolean("encendido") ? "Encendido" : "Apagado";
            datos[i][5] = "Detener";
            datos[i][6] = "Reiniciar";
            datos[i][7] = "Iniciar";
            i++;
        }

        for (String[] fila : datos) {
            System.out.println(String.join(", ", fila));
        }

        return datos;
    }
}
