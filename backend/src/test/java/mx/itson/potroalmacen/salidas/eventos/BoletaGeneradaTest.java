package mx.itson.potroalmacen.salidas.eventos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoletaGeneradaTest {

    private static final Instant AHORA = Instant.parse("2026-11-23T18:00:00Z");

    @Test
    @DisplayName("Como evento auditable, registra al entregador, la acción ENTREGAR y el folio")
    void datosDeAuditoria() {
        EventoAuditable evento = new BoletaGenerada(1, "BOL-1A2B3C4D", 10, 20, AHORA);

        assertEquals(20L, evento.usuarioId());
        assertNull(evento.campus());
        assertEquals(AccionAuditoria.ENTREGAR, evento.accion());
        assertEquals("salidas", evento.modulo());
        assertEquals("Boleta", evento.entidad());
        assertEquals("BOL-1A2B3C4D", evento.referencia());
        assertEquals(AHORA, evento.ocurridoEn());
    }

    @Test
    @DisplayName("Rechaza datos obligatorios nulos")
    void rechazaNulos() {
        assertThrows(NullPointerException.class, () -> new BoletaGenerada(null, "BOL-1", 10, 20, AHORA));
        assertThrows(NullPointerException.class, () -> new BoletaGenerada(1, null, 10, 20, AHORA));
        assertThrows(NullPointerException.class, () -> new BoletaGenerada(1, "BOL-1", null, 20, AHORA));
        assertThrows(NullPointerException.class, () -> new BoletaGenerada(1, "BOL-1", 10, null, AHORA));
        assertThrows(NullPointerException.class, () -> new BoletaGenerada(1, "BOL-1", 10, 20, null));
    }
}
