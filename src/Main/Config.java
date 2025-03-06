package Main;

import Network.NetworkInfo;

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
    public String gateway;
    public String broadcast;
    public String mask;

    private Properties props = new Properties();

    public Config() {
        // Leer el archivo de configuración
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO actualizar datos de red en config.porperties con NetworkInfo
        NetworkInfo networkInfo = new NetworkInfo();

        setGateway(networkInfo.updateGateway());
        setBroadcast(networkInfo.updateBroadcast());
        setMask(networkInfo.updateSubnetMask());

        // Obtener el valor de
        interval = Integer.parseInt(props.getProperty("interval"));
        delay = Integer.parseInt(props.getProperty("delay"));
        ssid = props.getProperty("SSID");
        password = props.getProperty("password");
        gateway = props.getProperty("gateway");
        broadcast = props.getProperty("broadcast");
        mask = props.getProperty("mask");
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
        props.setProperty("SSID", ssid);
    }

    public void setPassword(String password) {
        this.password = password;
        props.setProperty("password", password);
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
        props.setProperty("gateway", gateway);
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
        props.setProperty("broadcast", broadcast);
    }

    public void setMask(String mask) {
        this.mask = mask;
        props.setProperty("mask", mask);
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

    public String getGateway() {
        return gateway;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public String getMask() {
        return mask;
    }
}
