package mx.itson.potroalmacen.materiales.application.port.in;

import mx.itson.potroalmacen.materiales.domain.Material;

/**
 * Puerto de entrada para el caso de uso de registro de material.
 * Define la operación para dar de alta un material con su existencia inicial.
 */
public interface RegistrarMaterialUseCase {

    /**
     * Registra un nuevo material en el catálogo validando que no exista duplicidad de código
     * y estableciendo su existencia inicial para el campus seleccionado.
     *
     * @param comando datos del material y su existencia inicial.
     * @return el material creado y persistido.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException si el código ya existe.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException si algún campo obligatorio falta o es inválido.
     */
    Material registrar(RegistrarMaterialCommand comando);
}
