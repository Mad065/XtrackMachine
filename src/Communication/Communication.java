package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public boolean verificarConexion() {
        return true;
    }

    public String actualizarEstado(String ip) throws IOException {
        // Update (U)
        String response = enviarMensaje(ip, "U");
        String estado;

        switch (response) {
            case "D": estado = "Sucio"; break;
            case "F": estado = "Limpio"; break;
            case "C": estado = "Limpiando"; break;
            case "U": estado = "En uso"; break;
            case "X": estado = "Error"; break;
            default: estado = "Desconocido"; break;
        }

        // Actualizar en la base de datos
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
