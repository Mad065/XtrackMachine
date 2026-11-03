package SQL;

import Model.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstadoRepository {

    private final Connection connection;

    public EstadoRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Estado> findAll() throws SQLException {
        String query = "SELECT id, nombre FROM Estado ORDER BY id";
        List<Estado> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Estado(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return list;
    }

    public int findIdByNombre(String nombre) throws SQLException {
        String query = "SELECT id FROM Estado WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("No se encontró ningún estado con el nombre: " + nombre);
    }
}
