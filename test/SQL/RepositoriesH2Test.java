package SQL;

import Model.Aspiradora;
import Model.Estado;
import Model.Maquina;
import Model.Usuario;
import Security.SecurityUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Integración de Repositorios con Base de Datos H2 en Memoria")
class RepositoriesH2Test {

    private static Connection connection;
    private AspiradoraRepository aspiradoraRepo;
    private UsuarioRepository usuarioRepo;
    private MaquinaRepository maquinaRepo;
    private EstadoRepository estadoRepo;

    @BeforeAll
    static void initDatabase() throws SQLException {
        // H2 en memoria configurado en modo de compatibilidad MySQL
        String jdbcUrl = "jdbc:h2:mem:xtrack_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        connection = DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        aspiradoraRepo = new AspiradoraRepository(connection);
        usuarioRepo = new UsuarioRepository(connection);
        maquinaRepo = new MaquinaRepository(connection);
        estadoRepo = new EstadoRepository(connection);

        // Reiniciar tablas antes de cada prueba
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Aspiradora");
            stmt.execute("DROP TABLE IF EXISTS Usuario");
            stmt.execute("DROP TABLE IF EXISTS Maquina");
            stmt.execute("DROP TABLE IF EXISTS Estado");
            stmt.execute("DROP TABLE IF EXISTS Tipo_usuario");

            stmt.execute("""
                    CREATE TABLE Tipo_usuario (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL
                    )""");
            stmt.execute("INSERT INTO Tipo_usuario (id, nombre) VALUES (1, 'Dios')");

            stmt.execute("""
                    CREATE TABLE Estado (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL
                    )""");
            stmt.execute("""
                    INSERT INTO Estado (id, nombre) VALUES
                    (1, 'Sucio'),
                    (2, 'Limpio'),
                    (3, 'Limpiando'),
                    (4, 'En uso'),
                    (5, 'Error'),
                    (6, 'Error de conexion'),
                    (7, 'Desconocido')
                    """);

            stmt.execute("""
                    CREATE TABLE Maquina (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL
                    )""");
            stmt.execute("""
                    INSERT INTO Maquina (id, nombre) VALUES
                    (1, 'Fresadora tipo A'),
                    (2, 'Fresadora tipo B'),
                    (3, 'Fresadora tipo C')
                    """);

            stmt.execute("""
                    CREATE TABLE Usuario (
                        nombre VARCHAR(100) NOT NULL PRIMARY KEY,
                        contrasena VARCHAR(255) NOT NULL,
                        tipo INT NOT NULL
                    )""");
            stmt.execute("INSERT INTO Usuario (nombre, contrasena, tipo) VALUES ('Mad0', '1234', 1)");

            stmt.execute("""
                    CREATE TABLE Aspiradora (
                        id INT NOT NULL PRIMARY KEY,
                        ip VARCHAR(50) NOT NULL,
                        maquina INT NOT NULL,
                        encendido BOOLEAN NOT NULL,
                        estado INT NOT NULL
                    )""");
            stmt.execute("INSERT INTO Aspiradora (id, ip, maquina, encendido, estado) VALUES (1, '192.168.3.31', 1, false, 1)");
        }
    }

    @Test
    @DisplayName("EstadoRepository consulta todos los estados y busca por nombre")
    void testEstadoRepository() throws SQLException {
        List<Estado> estados = estadoRepo.findAll();
        assertEquals(7, estados.size(), "Debe haber 7 estados registrados");

        int idLimpio = estadoRepo.findIdByNombre("Limpio");
        assertEquals(2, idLimpio);

        assertThrows(SQLException.class, () -> estadoRepo.findIdByNombre("NoExiste"));
    }

    @Test
    @DisplayName("MaquinaRepository realiza operaciones CRUD completas")
    void testMaquinaRepositoryCRUD() throws SQLException {
        List<Maquina> maquinas = maquinaRepo.findAll();
        assertEquals(3, maquinas.size());

        int idA = maquinaRepo.findIdByNombre("Fresadora tipo A");
        assertEquals(1, idA);

        // Crear nueva máquina
        maquinaRepo.save("Fresadora tipo X");
        int idX = maquinaRepo.findIdByNombre("Fresadora tipo X");
        assertTrue(idX > 0);

        // Actualizar nombre
        maquinaRepo.update("Fresadora tipo X", "Fresadora tipo Renovada");
        assertDoesNotThrow(() -> maquinaRepo.findIdByNombre("Fresadora tipo Renovada"));

        // Eliminar
        maquinaRepo.deleteByNombre("Fresadora tipo Renovada");
        assertThrows(SQLException.class, () -> maquinaRepo.findIdByNombre("Fresadora tipo Renovada"));
    }

    @Test
    @DisplayName("UsuarioRepository valida contraseñas y migra texto plano a hash PBKDF2 automáticamente")
    void testUsuarioRepositoryCredencialesYMigracion() throws SQLException {
        // Verificar que 'Mad0' tiene inicialmente contraseña plana '1234'
        Optional<Usuario> userOpt = usuarioRepo.findByNombre("Mad0");
        assertTrue(userOpt.isPresent());
        assertEquals("1234", userOpt.get().contrasena());
        assertFalse(SecurityUtil.isHashed(userOpt.get().contrasena()));

        // Verificar credenciales correctas: debe autenticar y migrar en BD a hash
        boolean loginOk = usuarioRepo.verificarCredenciales("Mad0", "1234");
        assertTrue(loginOk, "Credenciales correctas deben ser aceptadas");

        // Comprobar que en la base de datos se migró a PBKDF2
        Optional<Usuario> updatedUser = usuarioRepo.findByNombre("Mad0");
        assertTrue(updatedUser.isPresent());
        assertTrue(SecurityUtil.isHashed(updatedUser.get().contrasena()), "La contraseña debe haber sido migrada a hash PBKDF2");

        // Verificar con contraseña incorrecta
        assertFalse(usuarioRepo.verificarCredenciales("Mad0", "password_invalida"));

        // Registrar un nuevo usuario con hash
        Usuario nuevo = new Usuario("operario1", "Apertek2024", 1);
        usuarioRepo.save(nuevo);

        assertTrue(usuarioRepo.verificarCredenciales("operario1", "Apertek2024"));
        assertFalse(usuarioRepo.verificarCredenciales("operario1", "otra"));

        // Eliminar usuario
        usuarioRepo.deleteByNombre("operario1");
        assertTrue(usuarioRepo.findByNombre("operario1").isEmpty());
    }

    @Test
    @DisplayName("AspiradoraRepository realiza operaciones de consulta, inserción, actualización y borrado")
    void testAspiradoraRepositoryCRUD() throws SQLException {
        List<Aspiradora> lista = aspiradoraRepo.findAll();
        assertEquals(1, lista.size());
        Aspiradora primera = lista.get(0);
        assertEquals("192.168.3.31", primera.ip());
        assertEquals("Fresadora tipo A", primera.nombreMaquina());
        assertEquals("Sucio", primera.nombreEstado());

        // Buscar por ID
        Optional<Aspiradora> porId = aspiradoraRepo.findById(1);
        assertTrue(porId.isPresent());
        assertEquals(1, porId.get().id());

        // Obtener lista de IPs
        List<String> ips = aspiradoraRepo.findAllIps();
        assertTrue(ips.contains("192.168.3.31"));

        // Guardar nueva aspiradora
        Aspiradora nueva = new Aspiradora(2, "192.168.3.50", 2, "Fresadora tipo B", true, 2, "Limpio");
        aspiradoraRepo.save(nueva);

        Optional<Aspiradora> aspiradora2 = aspiradoraRepo.findById(2);
        assertTrue(aspiradora2.isPresent());
        assertEquals("192.168.3.50", aspiradora2.get().ip());

        // Actualizar estado de red
        aspiradoraRepo.updateEstado("192.168.3.50", 4); // 4 = En uso
        Optional<Aspiradora> aspiradoraActualizada = aspiradoraRepo.findById(2);
        assertTrue(aspiradoraActualizada.isPresent());
        assertEquals(4, aspiradoraActualizada.get().estadoId());
        assertEquals("En uso", aspiradoraActualizada.get().nombreEstado());

        // Eliminar aspiradora
        aspiradoraRepo.deleteById(2);
        assertTrue(aspiradoraRepo.findById(2).isEmpty());
    }
}
