package mx.itson.potroalmacen.materiales.adapter.out.eventos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.materiales.eventos.MaterialRegistrado;
import org.junit.jupiter.api.Test;

class SpringPublicadorEventosAdapterTest {

    @Test
    void publicarEntregaElEventoAlPublisherDeSpring() {
        List<Object> recibidos = new ArrayList<>();
        SpringPublicadorEventosAdapter adapter = new SpringPublicadorEventosAdapter(recibidos::add);
        MaterialRegistrado evento =
                new MaterialRegistrado(1, "PIN-001", "Pintura", "GUAYMAS", 0, Instant.parse("2026-11-12T18:00:00Z"));

        adapter.publicar(evento);

        assertEquals(List.of(evento), recibidos);
    }

    @Test
    void publicarRechazaEventoNulo() {
        SpringPublicadorEventosAdapter adapter = new SpringPublicadorEventosAdapter(evento -> {});

        assertThrows(NullPointerException.class, () -> adapter.publicar(null));
    }
}
