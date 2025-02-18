package Communication;

import SQL.Conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class Communication {
    public int puerto = 7777;

    public String enviarMensaje(String ip, String mensaje) throws IOException {
        Socket socket = new Socket(ip, puerto);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(mensaje);
        System.out.println("Mensaje enviado: " + mensaje);

        String response = in.readLine();
        System.out.println("Respuesta de ESP32: " + response);
        
        return response;
    }

    public boolean verificarConexion(String ip) throws IOException {
        // Verification (V)
        String response = enviarMensaje(ip, "V");

        if (response.equals("T")) {
            return true;
        }

        return true;
    }

    public Integer actualizarEstado(String ip) throws IOException, SQLException {
        // Update (U)
        String response = enviarMensaje(ip, "U");
        int estado;

        switch (response) {
            case "D": estado = 1; break; // (Sucio)
            case "F": estado = 2; break; // (Limpio)
            case "C": estado = 3; break; // (Limpiando)
            case "U": estado = 4; break; // (En uso)
            case "X": estado = 5; break; // (Error)
            default: estado = 6; break; // (Desconocido)
        }

        // TODO Actualizar la base de datos para usar los estados correctos y hacer codigo para actualizar estado en la base de datos
        Conexion conexion = new Conexion();
        conexion.actualizarAspiradora(ip, estado);

        return estado;
    }

    public boolean detenerAspiradora(String ip) throws IOException {
        // Stop (S)
        String response = enviarMensaje(ip, "S");

        if (response.equals("T")) {
            return true;
        }

        return false;
    }

    public boolean reiniciarAspiradora(String ip) throws IOException {
        // Reboot (R)
        String response = enviarMensaje(ip, "R");

        if (response.equals("T")) {
            return true;
        }

        return false;
    }

    public boolean iniciarAspiradora(String ip) throws IOException {
        // Init (I)
        String response = enviarMensaje(ip, "I");

        if (response.equals("T")) {
            return true;
        }

        return false;
    }
}
