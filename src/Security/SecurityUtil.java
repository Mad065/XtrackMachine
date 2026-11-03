package Security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utilidad de seguridad para hashing y verificación de contraseñas mediante PBKDF2 con HMAC-SHA256.
 * Incluye compatibilidad hacia atrás para verificar contraseñas legadas en texto plano y migrarlas.
 */
public class SecurityUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;
    private static final String PREFIX = "PBKDF2$";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Genera un hash seguro para la contraseña dada utilizando PBKDF2WithHmacSHA256 y un salt aleatorio.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return PREFIX + saltBase64 + "$" + hashBase64;
    }

    /**
     * Verifica si una contraseña en texto claro coincide con el hash almacenado.
     * Si el hash almacenado es texto plano antiguo, lo compara de forma segura para permitir migración.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }

        if (isHashed(storedHash)) {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 3) {
                return false;
            }

            try {
                byte[] salt = Base64.getDecoder().decode(parts[1]);
                byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

                byte[] computedHash = pbkdf2(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

                return MessageDigest.isEqual(expectedHash, computedHash);
            } catch (Exception e) {
                return false;
            }
        }

        // Compatibilidad hacia atrás: comparación en tiempo constante para texto plano legado
        return MessageDigest.isEqual(
                plainPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                storedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    /**
     * Determina si una cadena ya tiene formato de hash seguro PBKDF2.
     */
    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al calcular el hash de contraseña: " + e.getMessage(), e);
        }
    }
}
