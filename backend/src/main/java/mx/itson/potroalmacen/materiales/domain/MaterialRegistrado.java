/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.domain;

import java.time.Instant;
import java.util.Objects;

/**
 *
 * @author erika
 */
public record MaterialRegistrado(
        Integer materialId,
        String codigo,
        String nombre,
        Integer almacenId,
        Instant ocurridoEn) {
 
    public MaterialRegistrado {
        Objects.requireNonNull(materialId, "materialId es obligatorio");
        Objects.requireNonNull(codigo, "codigo es obligatorio");
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(almacenId, "almacenId es obligatorio");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn es obligatorio");
    }
 
    public static MaterialRegistrado de(Integer materialId, String codigo,
                                        String nombre, Integer almacenId) {
        return new MaterialRegistrado(materialId, codigo, nombre, almacenId, Instant.now());
    }
}
