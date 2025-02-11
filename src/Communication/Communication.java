package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communication {

    public String enviarMensaje(String ip, int puerto, String mensaje) throws IOException {
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

    }

    public String actualizarEstado() {

    }
}
