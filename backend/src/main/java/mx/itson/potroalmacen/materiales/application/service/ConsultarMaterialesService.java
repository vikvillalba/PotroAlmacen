/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.Material;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author erika
 */
@Service
public class ConsultarMaterialesService implements ConsultarMaterialesUseCase{
     private final MaterialRepositoryPort repositorio;
 
    ConsultarMaterialesService(MaterialRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<Material> listar(FiltroMateriales filtro) {
        FiltroMateriales efectivo = filtro == null ? FiltroMateriales.sinFiltros() : filtro;
        return repositorio.buscar(efectivo);
    }
}
