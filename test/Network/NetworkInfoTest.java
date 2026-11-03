package Network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para NetworkInfo")
class NetworkInfoTest {

    @Test
    @DisplayName("NetworkInfo inicializa y captura información de interfaces sin lanzar excepciones")
    void testNetworkInfoInitialization() {
        NetworkInfo net = new NetworkInfo();
        assertNotNull(net.getGateway(), "El gateway no debe ser nulo");
        assertNotNull(net.getBroadcast(), "El broadcast no debe ser nulo");
        assertNotNull(net.getSubnetMask(), "La máscara de subred no debe ser nula");
        assertNotNull(net.getInterfaceName(), "El nombre de interfaz no debe ser nulo");
        assertNotNull(net.getLocalIp(), "La IP local no debe ser nula");
    }

    @Test
    @DisplayName("updateGateway, updateBroadcast y updateSubnetMask retornan valores no nulos")
    void testUpdateMethods() {
        NetworkInfo net = new NetworkInfo();
        assertNotNull(net.updateGateway());
        assertNotNull(net.updateBroadcast());
        assertNotNull(net.updateSubnetMask());
    }

    @Test
    @DisplayName("Si la interfaz local está activa, la IP y máscara deben tener formato IPv4")
    void testIpFormatIfConnected() {
        NetworkInfo net = new NetworkInfo();
        String localIp = net.getLocalIp();
        if (!"No disponible".equals(localIp)) {
            assertTrue(localIp.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"),
                    "La IP local detectada debe tener formato IPv4: " + localIp);
        }

        String mask = net.getSubnetMask();
        if (!"No disponible".equals(mask)) {
            assertTrue(mask.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"),
                    "La máscara detectada debe tener formato IPv4: " + mask);
        }
    }
}
