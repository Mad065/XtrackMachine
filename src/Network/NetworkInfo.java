package Network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkInfo {
    private String gateway = "No disponible";
    private String broadcast = "No disponible";
    private String subnetMask = "No disponible";
    private String interfaceName = "No disponible";
    private String localIp = "No disponible";

    public NetworkInfo() {
        getInfo();
    }

    public synchronized void getInfo() {
        try {
            boolean found = false;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface ni = interfaces.nextElement();
                    // Ignorar loopback, interfaces caídas o virtuales
                    if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) {
                        continue;
                    }

                    for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                        InetAddress addr = ia.getAddress();
                        // Filtrar IPv4 real (no loopback, no link-local 169.254.x.x)
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                            InetAddress bcast = ia.getBroadcast();
                            if (bcast != null) {
                                interfaceName = ni.getName();
                                localIp = addr.getHostAddress();
                                broadcast = bcast.getHostAddress();

                                short prefix = ia.getNetworkPrefixLength();
                                int maskInt = 0xFFFFFFFF << (32 - prefix);
                                subnetMask = String.format("%d.%d.%d.%d",
                                        (maskInt >> 24) & 0xFF,
                                        (maskInt >> 16) & 0xFF,
                                        (maskInt >> 8) & 0xFF,
                                        maskInt & 0xFF);

                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }

            // Detección de Gateway según SO
            gateway = detectGateway(interfaceName, localIp);

        } catch (Exception e) {
            System.err.println("Error al detectar información de red: " + e.getMessage());
        }
    }

    private String detectGateway(String ifName, String ip) {
        String os = System.getProperty("os.name").toLowerCase();
        String detected = null;

        try {
            if (os.contains("win")) {
                String cmd = (ifName != null && !ifName.equals("No disponible"))
                        ? "netsh interface ip show config name=\"" + ifName + "\""
                        : "netsh interface ip show config";
                String output = executeCommand(new String[]{"cmd.exe", "/c", cmd + " | findstr /i \"Gateway Puerta\""});
                if (output != null && !output.isEmpty()) {
                    for (String part : output.split("\\s+")) {
                        if (part.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                            detected = part;
                            break;
                        }
                    }
                }
            } else if (os.contains("mac")) {
                if (ifName != null && !ifName.equals("No disponible")) {
                    detected = executeCommand(new String[]{"/bin/sh", "-c", "ipconfig getoption " + ifName + " router"});
                }
                if (detected == null || detected.isEmpty() || detected.contains("No encontrado")) {
                    detected = executeCommand(new String[]{"/bin/sh", "-c", "route -n get default | awk '/gateway:/ {print $2}'"});
                }
            } else { // Linux / Unix
                detected = executeCommand(new String[]{"/bin/sh", "-c", "ip route | grep default | awk '{print $3}'"});
            }
        } catch (Exception e) {
            // Fallback
        }

        // Si la detección falló pero tenemos una IP local tipo 192.168.X.Y, inferir 192.168.X.1 como fallback
        if ((detected == null || detected.isEmpty() || detected.contains("No encontrado"))
                && ip != null && ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            int lastDot = ip.lastIndexOf('.');
            if (lastDot > 0) {
                detected = ip.substring(0, lastDot) + ".1";
            }
        }

        return (detected != null && !detected.isEmpty()) ? detected.trim() : "No disponible";
    }

    private String executeCommand(String[] cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                return (line != null) ? line.trim() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Getters
    public String getGateway() { return gateway; }
    public String getBroadcast() { return broadcast; }
    public String getSubnetMask() { return subnetMask; }
    public String getInterfaceName() { return interfaceName; }
    public String getLocalIp() { return localIp; }

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
