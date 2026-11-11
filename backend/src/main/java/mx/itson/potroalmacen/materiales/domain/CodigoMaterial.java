/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.domain;

import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;

/**
 *
 * @author erika
 */
public record CodigoMaterial(String valor) {
 
    public static final int LONGITUD_MAXIMA = 30;
 
    public CodigoMaterial {
        if (valor == null || valor.isBlank()) {
            throw new MaterialInvalidoException("El código es obligatorio");
        }
        valor = valor.trim().toUpperCase();
        if (valor.length() > LONGITUD_MAXIMA) {
            throw new MaterialInvalidoException(
                    "El código no puede exceder " + LONGITUD_MAXIMA + " caracteres");
        }
    }
 
    @Override
    public String toString() {
        return valor;
    }
}
