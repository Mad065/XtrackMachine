package SQL;

import Model.Usuario;
import Security.SecurityUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private final Connection connection;

    public UsuarioRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Usuario> findAll() throws SQLException {
        String query = "SELECT nombre, contrasena, tipo FROM Usuario ORDER BY nombre";
        List<Usuario> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Usuario(
                        rs.getString("nombre"),
                        rs.getString("contrasena"),
                        rs.getInt("tipo")
                ));
            }
        }
        return list;
    }

    public Optional<Usuario> findByNombre(String nombre) throws SQLException {
        String query = "SELECT nombre, contrasena, tipo FROM Usuario WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                            rs.getString("nombre"),
                            rs.getString("contrasena"),
                            rs.getInt("tipo")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public boolean verificarCredenciales(String nombre, String contrasenaPlana) throws SQLException {
        Optional<Usuario> userOpt = findByNombre(nombre);
        if (userOpt.isPresent()) {
            Usuario u = userOpt.get();
            boolean valido = SecurityUtil.verifyPassword(contrasenaPlana, u.contrasena());
            if (valido && !SecurityUtil.isHashed(u.contrasena())) {
                actualizarPasswordHash(nombre, SecurityUtil.hashPassword(contrasenaPlana));
            }
            return valido;
        }
        return false;
    }

    public void actualizarPasswordHash(String nombre, String newHash) throws SQLException {
        String query = "UPDATE Usuario SET contrasena = ? WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newHash);
            ps.setString(2, nombre);
            ps.executeUpdate();
        }
    }

    public void save(Usuario usuario) throws SQLException {
        String hashed = SecurityUtil.isHashed(usuario.contrasena())
                ? usuario.contrasena()
                : SecurityUtil.hashPassword(usuario.contrasena());
        String query = "INSERT INTO Usuario (nombre, contrasena, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, usuario.nombre());
            ps.setString(2, hashed);
            ps.setInt(3, usuario.tipo());
            ps.executeUpdate();
        }
    }

    public void update(String nombreOriginal, String nuevoNombre, String nuevaContrasena) throws SQLException {
        boolean updatePass = nuevaContrasena != null && !nuevaContrasena.trim().isEmpty() && !"••••••••".equals(nuevaContrasena);
        String query = updatePass
                ? "UPDATE Usuario SET nombre = ?, contrasena = ? WHERE nombre = ?"
                : "UPDATE Usuario SET nombre = ? WHERE nombre = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (updatePass) {
                String hashed = SecurityUtil.isHashed(nuevaContrasena)
                        ? nuevaContrasena
                        : SecurityUtil.hashPassword(nuevaContrasena);
                ps.setString(1, nuevoNombre);
                ps.setString(2, hashed);
                ps.setString(3, nombreOriginal);
            } else {
                ps.setString(1, nuevoNombre);
                ps.setString(2, nombreOriginal);
            }
            ps.executeUpdate();
        }
    }

    public void deleteByNombre(String nombre) throws SQLException {
        String query = "DELETE FROM Usuario WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }
}
