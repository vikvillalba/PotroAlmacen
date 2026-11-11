/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.usuarios.domain.exceptions;

/**
 * 
 * @author erika
 * Se lanza cuando se viola una regla de negocio del dominio de materiales.
 */
public class MaterialInvalidoException extends RuntimeException{
    public MaterialInvalidoException(String mensaje){
        super(mensaje);
    }
}
