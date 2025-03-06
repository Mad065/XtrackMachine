package Network;

import Main.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetworkInfo {
    private String ssid;
    private String password;
    private String gatewayIp;
    private String broadcastIp;

    public NetworkInfo(Config config) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                ssid = getSSIDConfig(config);
                password = getPasswordConfig(config);
                gatewayIp = getGatewayWindows();
                broadcastIp = getBroadcastWindows();
            } else if (os.contains("mac")) {
                ssid = getSSIDConfig(config);
                password = getPasswordConfig(config);
                gatewayIp = getGatewayMac();
                broadcastIp = getBroadcastMac();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {
                ssid = getSSIDConfig(config);
                password = getPasswordConfig(config);
                gatewayIp = getGatewayLinux();
                broadcastIp = getBroadcastLinux();
            } else {
                ssid = password = gatewayIp = broadcastIp = "No disponible";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO revisar comandos
    // TODO implementar con Settings

    // Usar ssid y contraseña proporcionados por el usuario (por el momento)
    private void fetchNetworkInfo() {

    }

    // --- OBTENER GATEWAY ---
    private String getGatewayWindows() throws Exception {
        return executeCommand("cmd /c ipconfig | findstr \"Puerta de enlace\"");
    }

    private String getGatewayMac() throws Exception {
        return executeCommand("ipconfig getoption en0 router");
    }

    private String getGatewayLinux() throws Exception {
        return executeCommand("sh -c \"ip route | grep default | awk '{print $3}'\"");
    }

    // --- OBTENER BROADCAST ---
    private String getBroadcastWindows() throws Exception {
        return executeCommand("cmd /c ipconfig | findstr \"Dirección de transmisión\"");
    }

    private String getBroadcastMac() throws Exception {
        return executeCommand("ifconfig en0 | awk '/inet / {print $6}'");
    }

    private String getBroadcastLinux() throws Exception {
        return executeCommand("sh -c \"ip -o -f inet addr show | awk '{print $6}'\"");
    }

    // --- OBTENER SSID ---
    private String getSSIDConfig(Config config) throws Exception {
        return config.getSsid();
    }

    // --- OBTENER CONTRASEÑA DEL WIFI ---
    private String getPasswordConfig(Config config) throws Exception {
        return config.getPassword();
    }

    // Funcion para ejecutar comandos
    private String executeCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        return (line != null) ? line.trim() : "No encontrado";
    }

    // Getters
    public String getSsid() { return ssid; }
    public String getPassword() { return password; }
    public String getGatewayIp() { return gatewayIp; }
    public String getBroadcastIp() { return broadcastIp; }

    public static void main(String[] args) {
        NetworkInfo networkInfo = new NetworkInfo(new Config());
        System.out.println("SSID: " + networkInfo.getSsid());
        System.out.println("Contraseña: " + networkInfo.getPassword());
        System.out.println("IP del Router (Gateway): " + networkInfo.getGatewayIp());
        System.out.println("IP de Broadcast: " + networkInfo.getBroadcastIp());
    }
}
