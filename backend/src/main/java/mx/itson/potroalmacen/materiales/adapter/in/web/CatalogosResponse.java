/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.adapter.in.web;

import java.util.List;

/**
 *
 * @author erika
 */
/** Opciones para los desplegables de filtros del inventario. */
public record CatalogosResponse(List<OpcionCatalogo> categorias, List<OpcionCatalogo> campus) {}
