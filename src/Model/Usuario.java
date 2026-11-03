package Model;

public record Usuario(String nombre, String contrasena, int tipo) {
    public Usuario(String nombre, String contrasena) {
        this(nombre, contrasena, 1);
    }
}
