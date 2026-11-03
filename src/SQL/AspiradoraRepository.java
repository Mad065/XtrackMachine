package SQL;

import Model.Aspiradora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AspiradoraRepository {

    private final Connection connection;

    public AspiradoraRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Aspiradora> findAll() throws SQLException {
        String query = """
                SELECT a.id, a.ip, a.maquina, m.nombre AS maquina_nombre,
                       a.encendido, a.estado, e.nombre AS estado_nombre
                FROM Aspiradora a
                INNER JOIN Maquina m ON a.maquina = m.id
                INNER JOIN Estado e ON a.estado = e.id
                ORDER BY a.id
                """;

        List<Aspiradora> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Aspiradora(
                        rs.getInt("id"),
                        rs.getString("ip"),
                        rs.getInt("maquina"),
                        rs.getString("maquina_nombre"),
                        rs.getBoolean("encendido"),
                        rs.getInt("estado"),
                        rs.getString("estado_nombre")
                ));
            }
        }
        return list;
    }

    public Optional<Aspiradora> findById(int id) throws SQLException {
        String query = """
                SELECT a.id, a.ip, a.maquina, m.nombre AS maquina_nombre,
                       a.encendido, a.estado, e.nombre AS estado_nombre
                FROM Aspiradora a
                INNER JOIN Maquina m ON a.maquina = m.id
                INNER JOIN Estado e ON a.estado = e.id
                WHERE a.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Aspiradora(
                            rs.getInt("id"),
                            rs.getString("ip"),
                            rs.getInt("maquina"),
                            rs.getString("maquina_nombre"),
                            rs.getBoolean("encendido"),
                            rs.getInt("estado"),
                            rs.getString("estado_nombre")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public List<String> findAllIps() throws SQLException {
        String query = "SELECT ip FROM Aspiradora ORDER BY id";
        List<String> ips = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ips.add(rs.getString("ip"));
            }
        }
        return ips;
    }

    public void save(Aspiradora aspiradora) throws SQLException {
        String query = "INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, aspiradora.id());
            ps.setString(2, aspiradora.ip());
            ps.setInt(3, aspiradora.maquinaId());
            ps.setBoolean(4, aspiradora.encendido());
            ps.setInt(5, aspiradora.estadoId());
            ps.executeUpdate();
        }
    }

    public void update(Aspiradora aspiradora) throws SQLException {
        String query = "UPDATE Aspiradora SET ip = ?, maquina = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, aspiradora.ip());
            ps.setInt(2, aspiradora.maquinaId());
            ps.setInt(3, aspiradora.estadoId());
            ps.setInt(4, aspiradora.id());
            ps.executeUpdate();
        }
    }

    public void updateEstado(String ip, int estadoId) throws SQLException {
        String query = "UPDATE Aspiradora SET estado = ? WHERE ip = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, estadoId);
            ps.setString(2, ip);
            ps.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String query = "DELETE FROM Aspiradora WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
