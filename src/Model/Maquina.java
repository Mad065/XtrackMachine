package Model;

public record Maquina(int id, String nombre) {
    public Maquina(String nombre) {
        this(0, nombre);
    }
}
