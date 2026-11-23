package mx.itson.potroalmacen.auditoria.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.auditoria.domain.exceptions.AuditoriaInvalidaException;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegistrarAuditoriaServiceTest {

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

    private final List<RegistroAuditoria> guardados = new ArrayList<>();

    private final RegistrarAuditoriaService service = new RegistrarAuditoriaService(registro -> {
        RegistroAuditoria conId = registro.conId((long) guardados.size() + 1);
        guardados.add(conId);
        return conId;
    });

    @Test
    @DisplayName("Guarda el registro con los datos del evento y devuelve el id asignado")
    void registrar_eventoValido() {
        EventoPrueba evento = new EventoPrueba(
                7L, "OBREGON_NAINARI", AccionAuditoria.CREAR, "materiales", "Material", "MAT-001", AHORA);

        RegistroAuditoria registro = service.registrar(evento);

        assertEquals(1L, registro.getId());
        assertEquals(7L, registro.getUsuarioId());
        assertEquals("OBREGON_NAINARI", registro.getCampus());
        assertEquals(AccionAuditoria.CREAR, registro.getAccion());
        assertEquals("materiales", registro.getModulo());
        assertEquals("Material", registro.getEntidad());
        assertEquals("MAT-001", registro.getReferencia());
        assertEquals(AHORA, registro.getOcurridoEn());
        assertEquals(List.of(registro), guardados);
    }

    @Test
    @DisplayName("Un evento nulo, sin usuario o sin acción se rechaza y no se guarda nada")
    void registrar_eventoInvalido() {
        assertThrows(AuditoriaInvalidaException.class, () -> service.registrar(null));
        assertThrows(
                AuditoriaInvalidaException.class,
                () -> service.registrar(new EventoPrueba(
                        null, "OBREGON_NAINARI", AccionAuditoria.CREAR, "materiales", "Material", "MAT-001", AHORA)));
        assertThrows(
                AuditoriaInvalidaException.class,
                () -> service.registrar(
                        new EventoPrueba(7L, "OBREGON_NAINARI", null, "materiales", "Material", "MAT-001", AHORA)));
        assertTrue(guardados.isEmpty());
    }
}
