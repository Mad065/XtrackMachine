package Security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para SecurityUtil")
class SecurityUtilTest {

    @Test
    @DisplayName("hashPassword genera un formato válido con prefijo PBKDF2$")
    void testHashPasswordFormat() {
        String hash = SecurityUtil.hashPassword("Apertek2024!");
        assertNotNull(hash);
        assertTrue(SecurityUtil.isHashed(hash), "El hash generado debe ser reconocido por isHashed()");
        assertTrue(hash.startsWith("PBKDF2$"), "El hash debe comenzar con el prefijo PBKDF2$");
        assertEquals(3, hash.split("\\$").length, "El hash debe tener exactamente 3 componentes separados por $");
    }

    @Test
    @DisplayName("hashPassword genera hashes diferentes para la misma contraseña gracias al salt aleatorio")
    void testHashPasswordUniqueSalt() {
        String hash1 = SecurityUtil.hashPassword("Password123");
        String hash2 = SecurityUtil.hashPassword("Password123");
        assertNotEquals(hash1, hash2, "Dos llamadas con la misma contraseña deben generar hashes distintos");
    }

    @Test
    @DisplayName("verifyPassword valida correctamente una contraseña frente a su hash")
    void testVerifyPasswordSuccess() {
        String pass = "SuperSecretPassword";
        String hash = SecurityUtil.hashPassword(pass);
        assertTrue(SecurityUtil.verifyPassword(pass, hash), "La contraseña correcta debe ser validada exitosamente");
    }

    @Test
    @DisplayName("verifyPassword rechaza una contraseña incorrecta")
    void testVerifyPasswordFailure() {
        String pass = "SuperSecretPassword";
        String hash = SecurityUtil.hashPassword(pass);
        assertFalse(SecurityUtil.verifyPassword("WrongPassword", hash), "Una contraseña errónea debe ser rechazada");
    }

    @Test
    @DisplayName("verifyPassword soporta contraseñas legadas en texto plano para permitir migración transparente")
    void testVerifyLegacyPlainTextPassword() {
        String legacyPlain = "1234";
        assertTrue(SecurityUtil.verifyPassword("1234", legacyPlain), "Debe validar contraseña plana coincidente");
        assertFalse(SecurityUtil.verifyPassword("4321", legacyPlain), "Debe rechazar contraseña plana no coincidente");
        assertFalse(SecurityUtil.isHashed(legacyPlain), "El texto plano no debe reportarse como isHashed()");
    }

    @Test
    @DisplayName("verifyPassword maneja entradas nulas de forma segura sin lanzar excepción")
    void testVerifyPasswordNullHandling() {
        assertFalse(SecurityUtil.verifyPassword(null, "hash"));
        assertFalse(SecurityUtil.verifyPassword("pass", null));
        assertFalse(SecurityUtil.verifyPassword(null, null));
    }

    @Test
    @DisplayName("hashPassword lanza IllegalArgumentException si la contraseña es nula o vacía")
    void testHashPasswordInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> SecurityUtil.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> SecurityUtil.hashPassword(""));
    }
}
