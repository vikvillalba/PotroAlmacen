package mx.itson.potroalmacen.auditoria.adapter.in.eventos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventoAuditableListenerTest {

    private record EventoPrueba(
            Long usuarioId,
            String campus,
            AccionAuditoria accion,
            String modulo,
            String entidad,
            String referencia,
            Instant ocurridoEn)
            implements EventoAuditable {}

    private static final Instant AHORA = Instant.parse("2026-11-22T10:00:00Z");

    private final List<EventoAuditable> recibidos = new ArrayList<>();

    private final EventoAuditableListener listener = new EventoAuditableListener(evento -> {
        recibidos.add(evento);
        return RegistroAuditoria.desdeEvento(evento).conId(1L);
    });

    @Test
    @DisplayName("Pasa el evento recibido al caso de uso de auditoría")
    void alOcurrirEventoAuditable_registra() {
        EventoPrueba evento = new EventoPrueba(
                7L, "OBREGON_NAINARI", AccionAuditoria.CREAR, "materiales", "Material", "MAT-001", AHORA);

        listener.alOcurrirEventoAuditable(evento);

        assertEquals(List.of(evento), recibidos);
    }

    @Test
    @DisplayName("Un evento inválido no lanza excepción: no se registra y la operación original no falla")
    void alOcurrirEventoAuditable_invalido() {
        EventoPrueba sinUsuario = new EventoPrueba(
                null, "OBREGON_NAINARI", AccionAuditoria.CREAR, "materiales", "Material", "MAT-001", AHORA);

        assertDoesNotThrow(() -> listener.alOcurrirEventoAuditable(sinUsuario));
        assertEquals(List.of(sinUsuario), recibidos);
    }
}
