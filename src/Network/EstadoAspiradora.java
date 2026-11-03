package Network;

/**
 * Representa los estados de las aspiradoras sincronizados con la base de datos (tabla Estado).
 */
public enum EstadoAspiradora {
    SUCIO(1, "Sucio"),
    LIMPIO(2, "Limpio"),
    LIMPIANDO(3, "Limpiando"),
    EN_USO(4, "En uso"),
    ERROR(5, "Error"),
    ERROR_CONEXION(6, "Error de conexion"),
    DESCONOCIDO(7, "Desconocido");

    private final int id;
    private final String nombre;

    EstadoAspiradora(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Mapea la respuesta enviada por el ESP32 al ID del estado correspondiente en la base de datos:
     * - "U" -> En uso (4)
     * - "F" -> Limpio (2)
     * - "X" -> Error (5)
     * - "TIMEOUT" -> Error de conexión (6)
     * - Cualquier otro valor o null -> Desconocido (7)
     */
    public static int fromCodigoRespuesta(String respuesta) {
        if (respuesta == null) {
            return DESCONOCIDO.id;
        }
        return switch (respuesta.trim()) {
            case "U" -> EN_USO.id;
            case "F" -> LIMPIO.id;
            case "X" -> ERROR.id;
            case "TIMEOUT" -> ERROR_CONEXION.id;
            default -> DESCONOCIDO.id;
        };
    }
}
