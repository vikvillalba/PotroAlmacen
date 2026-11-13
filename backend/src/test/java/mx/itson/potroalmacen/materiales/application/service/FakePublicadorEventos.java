package mx.itson.potroalmacen.materiales.application.service;

import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.out.PublicadorEventosPort;

/** Publicador falso que guarda los eventos en memoria para revisarlos en las pruebas. */
class FakePublicadorEventos implements PublicadorEventosPort {

    private final List<Object> eventos = new ArrayList<>();

    @Override
    public void publicar(Object evento) {
        eventos.add(evento);
    }

    List<Object> eventos() {
        return List.copyOf(eventos);
    }
}
