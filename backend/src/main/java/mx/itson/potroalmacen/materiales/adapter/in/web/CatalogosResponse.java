package mx.itson.potroalmacen.materiales.adapter.in.web;

import java.util.List;

/** Opciones de los selects del inventario (filtros) y del formulario de registro. */
public record CatalogosResponse(
        List<OpcionCatalogo> categorias,
        List<OpcionCatalogo> campus,
        List<OpcionCatalogo> unidadesMedida,
        List<OpcionCatalogo> presentaciones) {}
