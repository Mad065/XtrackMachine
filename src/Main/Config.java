package Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String CONFIG_FILE = "config.properties";
    public int interval;
    public int delay;
    public String ssid;
    public String password;


    public Config() {
        // Leer el archivo de configuración
        Properties props = new Properties();
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
    }
}
