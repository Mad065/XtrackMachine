package Communication;

import SQL.Conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

public class Communication {
    public int puerto = 7777;
    public int timeout = 5000;

    public String enviarMensaje(String ip, String mensaje) throws IOException {
        Socket socket = new Socket(ip, puerto);
        //Tiempo maximo de espera de respuesta
        socket.setSoTimeout(timeout);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(mensaje);
        System.out.println("Mensaje enviado: " + mensaje);

        String response;
        try {
            response = in.readLine(); // Leer la respuesta
            System.out.println("Respuesta de ESP32: " + response);
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout: La ESP32 no respondió en " + timeout + " ms");
            response = "TIMEOUT"; // Respuesta predeterminada
        } finally {
            socket.close();
        }
        
        return response;
    }

    public boolean verificarConexion(String ip) throws IOException {
        // Verification (V)
        String response = enviarMensaje(ip, "V");

        if (response.equals("T")) {
            return true;
        } else if (response.equals("TIMEOUT")) {
            System.out.println("No se pudo verificar la conexión de la aspiradora " + ip + " : Timeout");
            return false;
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
            case "TIMEOUT": estado = 6; break; // (Error de conexion)
            default: estado = 7; break; // (Desconocido)
        }

        Conexion conexion = new Conexion();
        conexion.actualizarAspiradora(ip, estado);

        return estado;
    }

    public boolean detenerAspiradora(String ip) throws IOException {
        // Stop (S)
        String response = enviarMensaje(ip, "S");

        if (response.equals("T")) {
            return true;
        } else if (response.equals("TIMEOUT")) {
            System.out.println("No se pudo detener la aspiradora " + ip + " : Timeout");
            return false;
        }

        return false;
    }

    public boolean reiniciarAspiradora(String ip) throws IOException {
        // Reboot (R)
        String response = enviarMensaje(ip, "R");

        if (response.equals("T")) {
            return true;
        } else if (response.equals("TIMEOUT")) {
            System.out.println("No se pudo reiniciar la apiradora " + ip + " : Timeout");
            return false;
        }

        return false;
    }

    public boolean iniciarAspiradora(String ip) throws IOException {
        // Init (I)
        String response = enviarMensaje(ip, "I");

        if (response.equals("T")) {
            return true;
        } else if (response.equals("TIMEOUT")) {
            System.out.println("No se pudo iniciar la aspiradora " + ip + " : Timeout");
            return false;
        }

        return false;
    }

    public boolean actualizarRed(String ip, String nuevoSSID, String nuevaPassword) throws IOException {
        String mensaje = "W:" + nuevoSSID + ":" + nuevaPassword;
        String response = enviarMensaje(ip, mensaje);

        if (response.equals("T")) {
            return true;
        } else if (response.equals("TIMEOUT")) {
            System.out.println("No se pudo actualizar la red " + ip + " : Timeout");
            return false;
        }

        return false;
    }
}
