package Network;

import SQL.Conexion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Communication {
    public int puerto = 7777;
    public int timeout = 5000;

    public String enviarMensaje(String ip, String mensaje) {
        try (Socket socket = new Socket(ip, puerto)) {
            socket.setSoTimeout(timeout); // Tiempo máximo de espera

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(mensaje);
                System.out.println("Mensaje enviado: " + mensaje);

                // Leer la respuesta
                String response = in.readLine();
                System.out.println("Respuesta de ESP32: " + (response != null ? response : "Sin respuesta"));

                return response != null ? response : "SIN RESPUESTA";
            }

        } catch (SocketTimeoutException e) {
            System.out.println("Timeout: La ESP32 no respondió en " + timeout + " ms");
            return "TIMEOUT";
        } catch (IOException e) {
            System.out.println("Error de comunicación con " + ip + ": " + e.getMessage());
            return "ERROR";
        }
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

    public Integer actualizarEstado(String ip, Conexion conexion) throws IOException, SQLException {
        // Update (U)
        String response = enviarMensaje(ip, "U");
        int estado;

        switch (response) {
            case "U": estado = 1; break; // (En uso)
            case "F": estado = 2; break; // (Limpio)
            case "X": estado = 3; break; // (Error)
            case "TIMEOUT": estado = 4; break; // (Error de conexion)
            default: estado = 5; break; // (Desconocido)
        }

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

    public List<String> obtenerIPs(String broadcastIP) throws IOException {
        List<String> respuestas = new ArrayList<>();
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        socket.setSoTimeout(10000); // 10 segundos

        byte[] mensaje = "G".getBytes(); // Mensaje de búsqueda
        DatagramPacket paqueteEnvio = new DatagramPacket(mensaje, mensaje.length, InetAddress.getByName(broadcastIP), puerto);
        // Enviar mensaje
        socket.send(paqueteEnvio);

        byte[] buffer = new byte[1024];

        while (true) {
            try {
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(paqueteRecibido); // Esperar respuestas

                String ipRemota = paqueteRecibido.getAddress().getHostAddress();
                System.out.println("Respuesta recibida desde " + ipRemota);

                respuestas.add(ipRemota);
            } catch (SocketTimeoutException e) {
                System.out.println("Finalizando recepción de respuestas...");
                break; // Terminar cuando se acabe el tiempo de espera
            }
        }

        socket.close();
        return respuestas;
    }
}
