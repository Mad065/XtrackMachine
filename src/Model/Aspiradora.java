package Model;

public record Aspiradora(
        int id,
        String ip,
        int maquinaId,
        String nombreMaquina,
        boolean encendido,
        int estadoId,
        String nombreEstado
) {
    public Aspiradora(int id, String ip, int maquinaId, int estadoId) {
        this(id, ip, maquinaId, "", false, estadoId, "");
    }
}
