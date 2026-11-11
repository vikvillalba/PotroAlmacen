package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.FichaTecnica;
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
        entity.setDepartamento(domain.getDepartamento());
        entity.setPrecioUnitario(domain.getPrecioUnitario());

        // Mapeo adaptado exactamente a los métodos del record FichaTecnica de Erika
        FichaTecnica ficha = domain.getFicha();
        if (ficha != null) {
            entity.setCaracteristicasTecnicas(ficha.caracteristicasTecnicas());
            entity.setMarcaRequerida(ficha.marcaRequerida());
            entity.setNormas(ficha.normas());
            // Guardamos la hoja de seguridad como texto (o validamos si aplica booleano en la entidad JPA)
            entity.setUsoYPrecauciones(ficha.hojaSeguridad());
        }

        return entity;
    }

    public Material toDomain(MaterialEntity entity) {
        if (entity == null) return null;

        CodigoMaterial codigoVO = new CodigoMaterial(entity.getCodigo());
        FichaTecnica fichaVO = new FichaTecnica(
                entity.getCaracteristicasTecnicas(),
                entity.getMarcaRequerida(),
                entity.getNormas(),
                entity.getUsoYPrecauciones() // Mapeado desde el campo correspondiente en la entidad
        );

        return Material.reconstruir(
                entity.getId() != null ? entity.getId().intValue() : null,
                codigoVO,
                entity.getNombre(),
                entity.getCategoria(),
                entity.getUnidadMedida(),
                entity.getPresentacion(),
                entity.getDepartamento(),
                entity.getPrecioUnitario(),
                fichaVO
        );
    }
}