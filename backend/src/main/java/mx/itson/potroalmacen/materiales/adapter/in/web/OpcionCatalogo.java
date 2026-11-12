/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.adapter.in.web;

/**
 *
 * @author erika
 * Opción de un desplegable: id es el valor que se manda de vuelta a la API.
 */
public record OpcionCatalogo(String id, String nombre) {
 
    public static OpcionCatalogo de(Enum<?> valor) {
        return new OpcionCatalogo(valor.name(), valor.toString());
    }
}
 