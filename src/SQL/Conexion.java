package SQL;

import Main.Config;
import Security.SecurityUtil;
import java.sql.*;

public class Conexion {

    Connection connection;
    String url;
    String user;
    String password;

    public Conexion () {
        Config config = new Config();
        this.url = config.getDbUrl();
        this.user = config.getDbUser();
        this.password = config.getDbPassword();
    }

    public Conexion (Config config) {
        if (config != null) {
            this.url = config.getDbUrl();
            this.user = config.getDbUser();
            this.password = config.getDbPassword();
        } else {
            Config defaultCfg = new Config();
            this.url = defaultCfg.getDbUrl();
            this.user = defaultCfg.getDbUser();
            this.password = defaultCfg.getDbPassword();
        }
    }

    public synchronized void conectar() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conectado");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public synchronized Connection getConnection() {
        conectar();
        return connection;
    }

    public synchronized AspiradoraRepository getAspiradoraRepository() {
        conectar();
        return new AspiradoraRepository(connection);
    }

    public synchronized UsuarioRepository getUsuarioRepository() {
        conectar();
        return new UsuarioRepository(connection);
    }

    public synchronized MaquinaRepository getMaquinaRepository() {
        conectar();
        return new MaquinaRepository(connection);
    }

    public synchronized EstadoRepository getEstadoRepository() {
        conectar();
        return new EstadoRepository(connection);
    }

    public synchronized boolean verificarUsuario(String usuario, String contrasena) throws SQLException {
        String query = "SELECT contrasena FROM Usuario WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contrasena");
                    boolean valido = SecurityUtil.verifyPassword(contrasena, storedHash);
                    if (valido && !SecurityUtil.isHashed(storedHash)) {
                        // Migración transparente a hash seguro PBKDF2
                        actualizarPasswordHash(usuario, SecurityUtil.hashPassword(contrasena));
                    }
                    return valido;
                }
            }
        }
        return false;
    }

    public synchronized void actualizarPasswordHash(String usuario, String newHash) throws SQLException {
        String query = "UPDATE Usuario SET contrasena = ? WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newHash);
            ps.setString(2, usuario);
            ps.executeUpdate();
        }
    }

    public synchronized String[][] obtenerAspiradoras() throws SQLException {

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

        String[][] datos = new String[rowCount][5];
        int i = 0;

        while (rs.next()) {
            datos[i][0] = String.valueOf(rs.getInt("id"));
            datos[i][1] = rs.getString("ip");
            datos[i][2] = rs.getString("maquina");
            datos[i][3] = rs.getString("estado");
            datos[i][4] = "Iniciar";
            i++;
        }

        for (String[] fila : datos) {
            System.out.println(String.join(", ", fila));
        }

        return datos;
    }

    public synchronized String[][] obtenerAdministrarAspiradoras() throws SQLException {

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

    public synchronized String[] obtenerAspiradora(int idAspiradora) throws SQLException {
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

    public synchronized String[][] obtenerUsuarios() throws SQLException {

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
            datos[i][1] = "••••••••";
            datos[i][2] = "Editar";
            datos[i][3] = "Eliminar";
            i++;
        }

        for (String[] fila : datos) {
            System.out.println(String.join(", ", fila));
        }

        return datos;
    }

    public synchronized String[] obtenerIPs() throws SQLException {
        String query = "SELECT ip FROM Aspiradora";
        try (Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query)) {

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
            while (rs.next()) {
                datos[i] = rs.getString("ip");
                i++;
            }

            return datos;
        }
    }

    public synchronized String[] obtenerMaquinas() throws SQLException {
        String query = "SELECT nombre FROM Maquina";
        try (Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query)) {

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

            return datos;
        }
    }

    public synchronized int obtenerIdMaquina(String nameMaquina) throws SQLException {
        String query = "SELECT id FROM Maquina WHERE nombre = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nameMaquina);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró ninguna máquina con el nombre: " + nameMaquina);
                }
            }
        }
    }

    public synchronized String[] obtenerEstados() throws SQLException {
        String query = "SELECT nombre FROM Estado";
        try (Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs = stmt.executeQuery(query)) {

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

            return datos;
        }
    }

    public synchronized int obtenerIdEstado(String nameEstado) throws SQLException {
        String query = "SELECT id FROM Estado WHERE nombre = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nameEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró ningún estado con el nombre: " + nameEstado);
                }
            }
        }
    }

    public synchronized void registrarAspiradora(int id, String ip, int maquina, int estado) throws SQLException {
        String query = "INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES (?, ?, ?, FALSE, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setString(2, ip);
            ps.setInt(3, maquina);
            ps.setInt(4, estado);
            ps.executeUpdate();
        }
    }

    public synchronized void registrarUsuario(String name, String password) throws SQLException {
        String hashedPassword = SecurityUtil.isHashed(password) ? password : SecurityUtil.hashPassword(password);
        String query = "INSERT INTO Usuario (nombre, contrasena, tipo) VALUES (?, ?, 1);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
        }
    }

    public synchronized void registrarMaquina(String nameMachine) throws SQLException {
        String query = "INSERT INTO Maquina (nombre) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nameMachine);
            ps.executeUpdate();
        }
    }

    public synchronized void actualizarUsuario(String name, String newPassword, String newName) throws SQLException {
        boolean updatePass = newPassword != null && !newPassword.trim().isEmpty() && !"••••••••".equals(newPassword);
        String query = updatePass
                ? "UPDATE Usuario SET nombre = ?, contrasena = ?, tipo = 1 WHERE nombre = ?;"
                : "UPDATE Usuario SET nombre = ?, tipo = 1 WHERE nombre = ?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (updatePass) {
                String hashed = SecurityUtil.isHashed(newPassword) ? newPassword : SecurityUtil.hashPassword(newPassword);
                ps.setString(1, newName);
                ps.setString(2, hashed);
                ps.setString(3, name);
            } else {
                ps.setString(1, newName);
                ps.setString(2, name);
            }
            ps.executeUpdate();
        }
    }

    public synchronized void actualizarMaquina(String nameMachine, String newNameMachine) throws SQLException {
        String query = "UPDATE Maquina SET nombre = ? WHERE nombre = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newNameMachine);
            ps.setString(2, nameMachine);
            ps.executeUpdate();
        }
    }

    public synchronized void actualizarAspiradora(int id, String ip, int maquina, int estado) throws SQLException {
        String query = "UPDATE Aspiradora SET ip = ?, maquina = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, ip);
            ps.setInt(2, maquina);
            ps.setInt(3, estado);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public synchronized void actualizarAspiradora(String ip, int estado) throws SQLException {
        String query = "UPDATE Aspiradora SET estado = ? WHERE ip = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, estado);
            ps.setString(2, ip);
            ps.executeUpdate();
        }
    }

    public synchronized void eliminarAspiradora(int id) throws SQLException {
        String query = "DELETE FROM Aspiradora WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public synchronized void eliminarUsuario(String name) throws SQLException {
        String query = "DELETE FROM Usuario WHERE nombre = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public synchronized void eliminarMaquina(String nameMachine) throws SQLException {
        String query = "DELETE FROM Maquina WHERE nombre = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nameMachine);
            ps.executeUpdate();
        }
    }
}
