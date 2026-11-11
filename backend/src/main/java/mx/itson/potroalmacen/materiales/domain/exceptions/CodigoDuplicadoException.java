/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.domain.exceptions;

/**
 *
 * @author erika
 */
public class CodigoDuplicadoException extends RuntimeException{
    
    private final String codigo;
    
    public CodigoDuplicadoException(String codigo) {
        super("Ya existe un material con el código " + codigo);
        this.codigo = codigo;
    }
 
    public String getCodigo() {
        return codigo;
    }  
}
