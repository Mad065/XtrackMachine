package Network;

import Main.Config;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetworkInfo {
    private String gateway;
    private String broadcast;
    private String subnetMask;

    // TODO revisar como obtener ssid y password de la red del equipo

    public NetworkInfo() {
        getInfo();
    }

    private void getInfo() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {

            } else if (os.contains("mac")) {

            } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {

            } else {
                gateway = broadcast = subnetMask = "No disponible";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- OBTENER GATEWAY ---
    private String getGatewayWindows() throws Exception {
        return executeCommand("cmd /c netsh interface ip show config | findstr \"Default Gateway\"");
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
        return executeCommand("ifconfig en0 | awk '/broadcast/ {print $6}'");
    }

    private String getBroadcastLinux() throws Exception {
        return executeCommand("sh -c \"ip -o -f inet addr show | awk '{print $4}' | cut -d'/' -f1\"");
    }

    // --- OBTENER MÁSCARA DE SUBRED ---
    private String getSubnetMaskWindows() throws Exception {
        return executeCommand("cmd /c ipconfig | findstr \"Máscara de subred\"");
    }

    private String getSubnetMaskMac() throws Exception {
        return executeCommand("ifconfig en0 | awk '/netmask/ {print $4}'");
    }

    private String getSubnetMaskLinux() throws Exception {
        return executeCommand("sh -c \"ip -o -f inet addr show | awk '{print $4}' | cut -d'/' -f2\"");
    }

    // --- CONVERSIÓN DE MÁSCARA DE SUBRED ---
    private String convertToDecimal(String subnetMask) {
        if (subnetMask == null || subnetMask.isEmpty()) return "No encontrado";
        String[] parts = subnetMask.split("\\s+");
        for (String part : parts) {
            if (part.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                return part;
            }
        }
        return "No encontrado";
    }

    private String convertHexToDecimal(String hexMask) {
        if (hexMask == null || hexMask.isEmpty()) return "No encontrado";
        try {
            int maskInt = Integer.decode(hexMask);
            return String.format("%d.%d.%d.%d",
                    (maskInt >> 24) & 0xFF,
                    (maskInt >> 16) & 0xFF,
                    (maskInt >> 8) & 0xFF,
                    maskInt & 0xFF);
        } catch (NumberFormatException e) {
            return "No encontrado";
        }
    }

    private String convertCidrToDecimal(String cidr) {
        if (cidr == null || cidr.isEmpty()) return "No encontrado";
        try {
            int prefix = Integer.parseInt(cidr);
            int mask = 0xFFFFFFFF << (32 - prefix);
            return String.format("%d.%d.%d.%d",
                    (mask >> 24) & 0xFF,
                    (mask >> 16) & 0xFF,
                    (mask >> 8) & 0xFF,
                    mask & 0xFF);
        } catch (NumberFormatException e) {
            return "No encontrado";
        }
    }

    // Función para ejecutar comandos en terminal
    private String executeCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        return (line != null) ? line.trim() : "No encontrado";
    }

    // Getters
    public String getGateway() { return gateway; }
    public String getBroadcast() { return broadcast; }
    public String getSubnetMask() { return subnetMask; }

    // Update
    public String updateGateway() {
        getInfo();
        return gateway;
    }
    public String updateBroadcast() {
        getInfo();
        return broadcast;
    }
    public String updateSubnetMask() {
        getInfo();
        return subnetMask;
    }
}
