package Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String CONFIG_FILE = "src/Main/config.properties";
    public int interval;
    public int delay;
    public String ssid;
    public String password;
    public String ip;
    public String broadcast;

    private Properties props = new Properties();

    public Config() {
        // Leer el archivo de configuración
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obtener el valor de
        interval = Integer.parseInt(props.getProperty("interval"));
        delay = Integer.parseInt(props.getProperty("delay"));
        ssid = props.getProperty("SSID");
        password = props.getProperty("password");
        ip = props.getProperty("ipRed");
        broadcast = props.getProperty("broadcast");
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
        props.setProperty("SSID", ssid);
    }

    public void setPassword(String password) {
        this.password = password;
        props.setProperty("password", password);
    }

    public void setIp(String ip) {
        this.ip = ip;
        props.setProperty("ipRed", ip);
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
        props.setProperty("broadcast", broadcast);
    }

    public int getInterval() {
        return interval;
    }

    public int getDelay() {
        return delay;
    }

    public String getSsid() {
        return ssid;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public String getBroadcast() {
        return broadcast;
    }
}
