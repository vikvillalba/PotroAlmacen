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
public record FichaTecnica (
     String caracteristicasTecnicas,
        String marcaRequerida,
        String normas,
        String hojaSeguridad) {
 
    public static final int MARCA_MAXIMA = 150;
    public static final int NORMAS_MAXIMA = 500;
    public static final int HOJA_SEGURIDAD_MAXIMA = 500;
 
    public FichaTecnica {
        caracteristicasTecnicas = normalizar(caracteristicasTecnicas);
        marcaRequerida = limitar(normalizar(marcaRequerida), MARCA_MAXIMA, "marca requerida");
        normas = limitar(normalizar(normas), NORMAS_MAXIMA, "normas");
        hojaSeguridad = limitar(normalizar(hojaSeguridad), HOJA_SEGURIDAD_MAXIMA, "hoja de seguridad");
    }
 
    public static FichaTecnica vacia() {
        return new FichaTecnica(null, null, null, null);
    }
 
    private static String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
 
    private static String limitar(String valor, int max, String campo) {
        if (valor != null && valor.length() > max) {
            throw new MaterialInvalidoException(
                    "El campo " + campo + " no puede exceder " + max + " caracteres");
        }
        return valor;
    }
}
