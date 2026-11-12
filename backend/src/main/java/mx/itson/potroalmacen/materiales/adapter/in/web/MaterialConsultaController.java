/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.adapter.in.web;

import java.util.Arrays;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author erika
 * 
 * GET /api/materiales?busqueda=interruptor&categoria=Electrico
 * Ambos parámetros son opcionales.
 */

@RestController
@RequestMapping("/api/materiales")
class MaterialConsultaController {
 
    private final ConsultarMaterialesUseCase consultarMateriales;
 
    MaterialConsultaController(ConsultarMaterialesUseCase consultarMateriales) {
        this.consultarMateriales = consultarMateriales;
    }
 
    @GetMapping
    List<MaterialResumenResponse> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) Campus campus) {
        return consultarMateriales.listar(new FiltroMateriales(busqueda, categoria, campus))
                .stream()
                .map(MaterialResumenResponse::de)
                .toList();
    }
 
    @GetMapping("/catalogos")
    CatalogosResponse catalogos() {
        return new CatalogosResponse(
                Arrays.stream(Categoria.values()).map(OpcionCatalogo::de).toList(),
                Arrays.stream(Campus.values()).map(OpcionCatalogo::de).toList());
    }
}
 