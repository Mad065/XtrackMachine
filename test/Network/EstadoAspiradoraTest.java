package Network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para EstadoAspiradora")
class EstadoAspiradoraTest {

    @Test
    @DisplayName("Los valores del enum corresponden exactamente al esquema de base de datos XtrackMachine.sql")
    void testEnumIdsAndNames() {
        assertEquals(1, EstadoAspiradora.SUCIO.getId());
        assertEquals("Sucio", EstadoAspiradora.SUCIO.getNombre());

        assertEquals(2, EstadoAspiradora.LIMPIO.getId());
        assertEquals("Limpio", EstadoAspiradora.LIMPIO.getNombre());

        assertEquals(3, EstadoAspiradora.LIMPIANDO.getId());
        assertEquals("Limpiando", EstadoAspiradora.LIMPIANDO.getNombre());

        assertEquals(4, EstadoAspiradora.EN_USO.getId());
        assertEquals("En uso", EstadoAspiradora.EN_USO.getNombre());

        assertEquals(5, EstadoAspiradora.ERROR.getId());
        assertEquals("Error", EstadoAspiradora.ERROR.getNombre());

        assertEquals(6, EstadoAspiradora.ERROR_CONEXION.getId());
        assertEquals("Error de conexion", EstadoAspiradora.ERROR_CONEXION.getNombre());

        assertEquals(7, EstadoAspiradora.DESCONOCIDO.getId());
        assertEquals("Desconocido", EstadoAspiradora.DESCONOCIDO.getNombre());
    }

    @ParameterizedTest(name = "Trama ''{0}'' debe mapear al estado id {1}")
    @CsvSource({
            "U, 4",
            "F, 2",
            "X, 5",
            "TIMEOUT, 6",
            "'  U  ', 4",
            "'  F  ', 2",
            "'TIMEOUT ', 6"
    })
    @DisplayName("fromCodigoRespuesta mapea correctamente respuestas del microcontrolador ESP32")
    void testFromCodigoRespuestaValidos(String input, int expectedId) {
        assertEquals(expectedId, EstadoAspiradora.fromCodigoRespuesta(input));
    }

    @Test
    @DisplayName("fromCodigoRespuesta ante valores desconocidos o nulos retorna el estado DESCONOCIDO (7)")
    void testFromCodigoRespuestaDesconocidos() {
        assertEquals(EstadoAspiradora.DESCONOCIDO.getId(), EstadoAspiradora.fromCodigoRespuesta(null));
        assertEquals(EstadoAspiradora.DESCONOCIDO.getId(), EstadoAspiradora.fromCodigoRespuesta(""));
        assertEquals(EstadoAspiradora.DESCONOCIDO.getId(), EstadoAspiradora.fromCodigoRespuesta("RESPUESTA_RARA"));
        assertEquals(EstadoAspiradora.DESCONOCIDO.getId(), EstadoAspiradora.fromCodigoRespuesta("999"));
    }
}
