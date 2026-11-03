package SQL;

import Model.Maquina;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MaquinaRepository {

    private final Connection connection;

    public MaquinaRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Maquina> findAll() throws SQLException {
        String query = "SELECT id, nombre FROM Maquina ORDER BY id";
        List<Maquina> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Maquina(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return list;
    }

    public int findIdByNombre(String nombre) throws SQLException {
        String query = "SELECT id FROM Maquina WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("No se encontró ninguna máquina con el nombre: " + nombre);
    }

    public void save(String nombre) throws SQLException {
        String query = "INSERT INTO Maquina (nombre) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    public void update(String nombreOriginal, String nuevoNombre) throws SQLException {
        String query = "UPDATE Maquina SET nombre = ? WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nuevoNombre);
            ps.setString(2, nombreOriginal);
            ps.executeUpdate();
        }
    }

    public void deleteByNombre(String nombre) throws SQLException {
        String query = "DELETE FROM Maquina WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }
}
