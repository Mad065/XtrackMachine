package Main;

import Network.NetworkInfo;

import java.io.*;
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
    public String dbUrl;
    public String dbUser;
    public String dbPassword;

    private Properties props = new Properties();

    public Config() {
        // Leer el archivo de configuración
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("No se pudo cargar el archivo de configuración: " + e.getMessage());
            }
        } else {
            // Cargar desde classpath si se ejecuta desde JAR
            try (InputStream input = Config.class.getResourceAsStream("/Main/config.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (IOException e) {
                System.err.println("No se pudo cargar config.properties del classpath: " + e.getMessage());
            }
        }

        // Obtener valores con defaults seguros
        interval = parseOrDefault(props.getProperty("interval"), 3);
        delay = parseOrDefault(props.getProperty("delay"), 0);
        ssid = props.getProperty("SSID", "");
        password = props.getProperty("password", "");
        gateway = props.getProperty("gateway", "");
        broadcast = props.getProperty("broadcast", "");
        mask = props.getProperty("mask", "");

        // Propiedades de Base de Datos (con soporte de variables de entorno)
        String envDbUrl = System.getenv("DB_URL");
        String envDbUser = System.getenv("DB_USER");
        String envDbPassword = System.getenv("DB_PASSWORD");

        dbUrl = (envDbUrl != null && !envDbUrl.isEmpty()) ? envDbUrl :
                props.getProperty("db.url", "jdbc:mysql://127.0.0.1:3306/xtrackmachine");
        dbUser = (envDbUser != null && !envDbUser.isEmpty()) ? envDbUser :
                props.getProperty("db.user", "root");
        dbPassword = (envDbPassword != null) ? envDbPassword :
                props.getProperty("db.password", "edsla266");

        NetworkInfo networkInfo = new NetworkInfo();

        if (gateway == null || gateway.trim().isEmpty()) {
            setGateway(networkInfo.updateGateway());
        }

        if (broadcast == null || broadcast.trim().isEmpty()) {
            setBroadcast(networkInfo.updateBroadcast());
        }

        if (mask == null || mask.trim().isEmpty()) {
            setMask(networkInfo.updateSubnetMask());
        }
    }

    private int parseOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Guardar cambios en el archivo
    private void saveConfig() {
        File file = new File(CONFIG_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream output = new FileOutputStream(file)) {
            props.store(output, "Archivo de Configuración Actualizado");
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
    }

    // Setters

    public void setSsid(String ssid) {
        this.ssid = ssid;
        props.setProperty("SSID", ssid != null ? ssid : "");
        saveConfig();
    }

    public void setPassword(String password) {
        this.password = password;
        props.setProperty("password", password != null ? password : "");
        saveConfig();
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
        props.setProperty("gateway", gateway != null ? gateway : "");
        saveConfig();
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
        props.setProperty("broadcast", broadcast != null ? broadcast : "");
        saveConfig();
    }

    public void setMask(String mask) {
        this.mask = mask;
        props.setProperty("mask", mask != null ? mask : "");
        saveConfig();
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        props.setProperty("db.url", dbUrl);
        saveConfig();
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
        props.setProperty("db.user", dbUser);
        saveConfig();
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        props.setProperty("db.password", dbPassword);
        saveConfig();
    }

    // Getters

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

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
