package SQL;

import java.sql.*;

public class Conexion {

    Connection connection;
    String url = "jdbc:mysql://127.0.0.1:3306/XtrackMachine";
    String user = "root";
    String password = "Joseantonio+0";

    public Conexion () {

    }

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
        String query = "SELECT * FROM Usuario WHERE nombre = ? AND contrasena = ?";
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
                INNER JOIN Estado e ON a.estado = e.id
                ORDER BY a.id
                """;

        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
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

    public String[][] obtenerAdministrarAspiradoras() throws SQLException {

        // Consulta SQL
        String query = """
                SELECT a.id, a.ip, m.nombre AS maquina
                FROM Aspiradora a
                INNER JOIN Maquina m ON a.maquina = m.id
                ORDER BY a.id
                """;

        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new String[0][0];
        }

        rs.beforeFirst();

        String[][] datos = new String[rowCount][5];

        int i = 0;
        while (rs.next()) {
            datos[i][0] = String.valueOf(rs.getInt("id"));
            datos[i][1] = rs.getString("ip");
            datos[i][2] = rs.getString("maquina");
            datos[i][3] = "Editar";
            datos[i][4] = "Eliminar";
            i++;
        }

        for (String[] fila : datos) {
            System.out.println(String.join(", ", fila));
        }

        return datos;
    }

    public String[] obtenerAspiradora(int idAspiradora) throws SQLException {
        String query = """
                SELECT a.id, a.ip, m.nombre AS maquina, e.nombre AS estado
                FROM Aspiradora a
                INNER JOIN Maquina m ON a.maquina = m.id
                INNER JOIN Estado e ON a.estado = e.id
                WHERE a.id = ?;
                """;
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, idAspiradora);
        ResultSet rs = ps.executeQuery();

        String[] datos = new String[4];

        if (rs.next()) { // Mover el cursor a la primera fila
            datos[0] = rs.getString("id");
            datos[1] = rs.getString("ip");
            datos[2] = rs.getString("maquina");
            datos[3] = rs.getString("estado");

            System.out.println(String.join(", ", datos));
        } else {
            System.out.println("No se encontró la aspiradora con ID: " + idAspiradora);
        }

        System.out.println(String.join(", ", datos));

        return datos;
    }

    public String[][] obtenerUsuarios() throws SQLException {

        // Consulta SQL
        String query = "SELECT nombre, contrasena FROM Usuario";

        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new String[0][0];
        }

        rs.beforeFirst();

        String[][] datos = new String[rowCount][4];

        int i = 0;
        while (rs.next()) {
            datos[i][0] = String.valueOf(rs.getString("nombre"));
            datos[i][1] = rs.getString("contrasena");
            datos[i][2] = "Editar";
            datos[i][3] = "Eliminar";
            i++;
        }

        for (String[] fila : datos) {
            System.out.println(String.join(", ", fila));
        }

        return datos;
    }

    public  String[] obtenerIPs() throws SQLException {
        String query = "SELECT ip FROM Aspiradora";
        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new String[0];
        }

        rs.beforeFirst();

        String[] datos = new String[rowCount];

        int i = 0;
        while (rs.next()) { // Mover el cursor a la primera fila
            datos[i] = rs.getString("ip");
            i++;
            System.out.println(String.join(", ", datos));
        }

        System.out.println(String.join(", ", datos));

        return datos;
    }

    public String[] obtenerMaquinas() throws SQLException {
        String query = "SELECT nombre FROM Maquina";
        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery((query));

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new  String[0];
        }

        rs.beforeFirst();

        String[] datos = new String[rowCount];

        int i = 0;
        while (rs.next()) {
            datos[i] = rs.getString("nombre");
            i++;
        }

        System.out.println(String.join(", ", datos));

        return datos;
    }

    public int obtenerIdMaquina(String nameMaquina) throws SQLException {
        String query = "SELECT id FROM Maquina WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nameMaquina);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("No se encontró ninguna máquina con el nombre: " + nameMaquina);
        }
    }

    public String[] obtenerEstados() throws SQLException {
        String query = "SELECT nombre FROM Estado";
        Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery((query));

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }

        if (rowCount == 0) {
            return new  String[0];
        }

        rs.beforeFirst();

        String[] datos = new String[rowCount];

        int i = 0;
        while (rs.next()) {
            datos[i] = rs.getString("nombre");
            i++;
        }

        System.out.println(String.join(", ", datos));

        return datos;
    }

    public int obtenerIdEstado(String nameEstado) throws SQLException {
        String query = "SELECT id FROM Estado WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nameEstado);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("No se encontró ningún estado con el nombre: " + nameEstado);
        }
    }

    public void registrarAspiradora(int id, String ip, int maquina, int estado) throws SQLException {
        String query = "INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES (?, ?, ?, FALSE, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.setString(2, ip);
        ps.setInt(3, maquina);
        ps.setInt(4, estado);
        ps.executeUpdate();
    }

    public void registrarUsuario(String name, String password) throws SQLException {
        String query = "INSERT INTO Usuario (nombre, contrasena, tipo) VALUE (?, ?, 1);";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ps.setString(2, password);
        ps.executeUpdate();
    }

    public void registrarMaquina(String nameMachine) throws SQLException {
        String query = "INSERT INTO Maquina (nombre) VALUE (?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nameMachine);
        ps.executeUpdate();
    }

    public void actualizarUsuario(String name, String newPassword, String newName) throws SQLException {
        String query = "UPDATE Usuario SET nombre = ?, contrasena = ?, tipo = 1 WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, newName);
        ps.setString(2, newPassword);
        ps.setString(3, name);
        ps.executeUpdate();
    }

    public void actualizarMaquina(String nameMachine, String newNameMachine) throws SQLException {
        String query = "UPDATE Maquina SET nombre = ? WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, newNameMachine);
        ps.setString(2, nameMachine);
        ps.executeUpdate();
    }

    public void actualizarAspiradora(int id, String ip, int maquina, int estado) throws SQLException {
        String query = "UPDATE Aspiradora SET ip = ?, maquina = ?, estado = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, ip);
        ps.setInt(2, maquina);
        ps.setInt(3, estado);
        ps.setInt(4, id);
        ps.executeUpdate();
    }

    public void actualizarAspiradora(String ip, int estado) throws SQLException {
        String query = "UPDATE Aspiradora SET estado = ? WHERE ip = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, estado);
        ps.setString(2, ip);
        ps.executeUpdate();
    }

    public void eliminarAspiradora(int id) throws SQLException {
        String query = "DELETE FROM Aspiradora WHERE id = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeQuery();
    }

    public void eliminarUsuario(String name) throws SQLException {
        String query = "DELETE FROM Usuario WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ps.executeQuery();
    }

    public void eliminarMaquina(String nameMachine) throws SQLException {
        String query = "DELETE FROM Maquina WHERE nombre = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nameMachine);
        ps.executeQuery();
    }

}
