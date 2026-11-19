package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import java.util.List;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import org.springframework.stereotype.Component;

@Component
public class BoletaMapper {

    public BoletaEntity toBoletaEntity(Boleta domain) {
        if (domain == null) return null;

        BoletaEntity entity = new BoletaEntity();

        entity.setId(domain.getId() != null ? domain.getId().longValue() : null);
        entity.setFolio(domain.getFolio());

        // Solicitante
        entity.setSolicitanteId(domain.getSolicitante().idEmpleado());
        entity.setSolicitanteNombre(domain.getSolicitante().nombreCompleto());
        entity.setSolicitantePuesto(domain.getSolicitante().puesto());

        // Entregador
        entity.setEntregadorId(domain.getEntregador().idEmpleado());
        entity.setEntregadorNombre(domain.getEntregador().nombreCompleto());
        entity.setEntregadorPuesto(domain.getEntregador().puesto());

        entity.setFechaHora(domain.getFechaHora());
        entity.setEstado(domain.getEstado());

        // Agrega el detalle de cada material a la entidad
        for (MaterialRef materialRef : domain.getMateriales()) {
            BoletaDetalleEntity detalleEntity = new BoletaDetalleEntity();

            detalleEntity.setCantidad(materialRef.cantidad());
            detalleEntity.setCodigoMaterial(materialRef.codigoMaterial());
            detalleEntity.setNombreMaterial(materialRef.nombreMaterial());

            entity.agregarDetalle(detalleEntity);
        }
        return entity;
    }

    public Boleta toDomain(BoletaEntity entity) {
        if (entity == null) return null;
        return Boleta.reconstruir(
                entity.getId() != null ? entity.getId().intValue() : null,
                entity.getFolio(),
                new EmpleadoRef(
                        entity.getSolicitanteId(), entity.getSolicitanteNombre(), entity.getSolicitantePuesto()),
                new EmpleadoRef(entity.getEntregadorId(), entity.getEntregadorNombre(), entity.getEntregadorPuesto()),
                entity.getFechaHora(),
                entity.getEstado(),
                entity.getDetalle() != null
                        ? entity.getDetalle().stream()
                                .map((d) ->
                                        new MaterialRef(d.getCodigoMaterial(), d.getNombreMaterial(), d.getCantidad()))
                                .toList()
                        : List.of());
    }
}
