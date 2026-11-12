package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    public MaterialEntity toEntity(Material domain) {
        if (domain == null) return null;

        MaterialEntity entity = new MaterialEntity();
        entity.setId(domain.getId() != null ? domain.getId().longValue() : null);

        if (domain.getCodigo() != null) {
            entity.setCodigo(domain.getCodigo().toString());
        }

        entity.setNombre(domain.getNombre());
        entity.setCategoria(domain.getCategoria());
        entity.setUnidadMedida(domain.getUnidadMedida());
        entity.setPresentacion(domain.getPresentacion());
        entity.setCampus(domain.getCampus());
        entity.setPrecioUnitario(domain.getPrecioUnitario());
        entity.setCantidad(domain.getCantidad());
        entity.setStockMinimo(domain.getStockMinimo());
        entity.setStockMaximo(domain.getStockMaximo());
        entity.setUsoPrecauciones(domain.getUsoPrecauciones());

        return entity;
    }

    public Material toDomain(MaterialEntity entity) {
        if (entity == null) return null;

        CodigoMaterial codigoVO = new CodigoMaterial(entity.getCodigo());

        return Material.reconstruir(
                entity.getId() != null ? entity.getId().intValue() : null,
                codigoVO,
                entity.getNombre(),
                entity.getCategoria(),
                entity.getUnidadMedida(),
                entity.getPresentacion(),
                entity.getCampus(),
                entity.getPrecioUnitario(),
                entity.getCantidad(),
                entity.getStockMinimo(),
                entity.getStockMaximo(),
                entity.getUsoPrecauciones());
    }
}
