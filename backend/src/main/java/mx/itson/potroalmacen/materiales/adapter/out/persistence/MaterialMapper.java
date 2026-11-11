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

        // Extrae el valor del Objeto de Valor CodigoMaterial
        if (domain.getCodigo() != null) {
            entity.setCodigo(domain.getCodigo().toString()); // O domain.getCodigo().getValor() según su implementación
        }

        entity.setNombre(domain.getNombre());
        entity.setCategoria(domain.getCategoria());
        entity.setUnidadMedida(domain.getUnidadMedida());
        entity.setPresentacion(domain.getPresentacion());
        entity.setDepartamento(domain.getDepartamento());
        entity.setPrecioUnitario(domain.getPrecioUnitario());

        // Mapea los atributos de la FichaTecnica si existen
        if (domain.getFicha() != null) {
            entity.setCaracteristicasTecnicas(domain.getFicha().getCaracteristicasTecnicas());
            entity.setMarcaRequerida(domain.getFicha().getMarcaRequerida());
            entity.setNormas(domain.getFicha().getNormas());
            entity.setRequiereHojaSeguridad(domain.getFicha().isRequiereHojaSeguridad());
            entity.setUsoYPrecauciones(domain.getFicha().getUsoYPrecauciones());
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
                entity.isRequiereHojaSeguridad(),
                entity.getUsoYPrecauciones()
        );

        // Utiliza el método estático 'reconstruir' que diseñó Erika en su dominio
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