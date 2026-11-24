package mx.itson.potroalmacen.salidas.adapter.out.eventos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.salidas.eventos.BoletaGenerada;
import org.junit.jupiter.api.Test;

class SpringPublicadorEventosAdapterTest {

    @Test
    void publicarEntregaElEventoAlPublisherDeSpring() {
        List<Object> recibidos = new ArrayList<>();
        SpringPublicadorEventosAdapter adapter = new SpringPublicadorEventosAdapter(recibidos::add);
        BoletaGenerada evento = new BoletaGenerada(1, "BOL-1A2B3C4D", 10, 20, Instant.parse("2026-11-23T18:00:00Z"));

        adapter.publicar(evento);

        assertEquals(List.of(evento), recibidos);
    }

    @Test
    void publicarRechazaEventoNulo() {
        SpringPublicadorEventosAdapter adapter = new SpringPublicadorEventosAdapter(evento -> {});

        assertThrows(NullPointerException.class, () -> adapter.publicar(null));
    }
}
