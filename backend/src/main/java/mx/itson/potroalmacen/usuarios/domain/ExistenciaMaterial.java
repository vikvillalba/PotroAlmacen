/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.usuarios.domain;

import mx.itson.potroalmacen.usuarios.domain.exceptions.MaterialInvalidoException;

/**
 *
 * @author erika
 */
public class ExistenciaMaterial {
    private final Long almacenId;
    private int cantidad;
    private int stockMinimo;
    
    public ExistenciaMaterial(Long almacenId, int cantidad, int stockMinimo) {
        if (almacenId == null) {
            throw new MaterialInvalidoException("El almacén es obligatorio");
        }
        if (cantidad < 0) {
            throw new MaterialInvalidoException("La cantidad no puede ser negativa");
        }
        if (stockMinimo < 0) {
            throw new MaterialInvalidoException("El stock mínimo no puede ser negativo");
        }
        this.almacenId = almacenId;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
    }
    
    /** 
     * Existencia finical al registrar un material: cantidad en cero.
     * @param almacenId
     * @param stockMinimo
     * @return  */
    public static ExistenciaMaterial inicial(Long almacenId, int stockMinimo) {
        return new ExistenciaMaterial(almacenId, 0, stockMinimo);
    }
    
    /** una entrada incrementa el stock; la cantidad debe ser positiva. */
    public void registrarEntrada(int cantidadRecibida) {
        if (cantidadRecibida <= 0) {
            throw new MaterialInvalidoException("La cantidad de entrada debe ser mayor a cero");
        }
        this.cantidad = Math.addExact(this.cantidad, cantidadRecibida);
    }
    
    public void registrarSalida(int cantidadSolicitada) {
        if (cantidadSolicitada <= 0) {
            throw new MaterialInvalidoException("La cantidad de salida debe ser mayor a cero");
        }
        if (cantidadSolicitada > cantidad) {
            throw new MaterialInvalidoException("Cantidad solicitada excede las existencias disponibles");
        }
        this.cantidad -= cantidadSolicitada;
    }
    
    public void cambiarStockMinimo(int nuevoStockMinimo) {
        if (nuevoStockMinimo < 0) {
            throw new MaterialInvalidoException("El stock mínimo no puede ser negativo");
        }
        this.stockMinimo = nuevoStockMinimo;
    }
    
    public boolean estaBajoMinimo() {
        return cantidad < stockMinimo;
    }
    
    public Long getAlmacenId() { return almacenId; }
    public int getCantidad() { return cantidad; }
    public int getStockMinimo() { return stockMinimo; }
}
