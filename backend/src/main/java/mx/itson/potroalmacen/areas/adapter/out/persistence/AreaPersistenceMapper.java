package mx.itson.potroalmacen.areas.adapter.out.persistence;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.areas.domain.Area;
import mx.itson.potroalmacen.areas.domain.Espacio;
import mx.itson.potroalmacen.areas.domain.MaterialRef;
import org.springframework.stereotype.Component;

/**
 * Convierte entre el agregado de dominio {@link Area} y la entidad JPA {@link AreaEntity}.
 */
@Component
class AreaPersistenceMapper {

    /**
     * Transforma una entidad {@link AreaEntity} en un agregado de dominio {@link Area}.
     *
     * @param entity entidad persistida de área.
     * @return agregado {@link Area} reconstruido.
     */
    Area toDomain(AreaEntity entity) {
        Objects.requireNonNull(entity, "entity es obligatoria");

        List<Espacio> espacios =
                entity.getEspacios().stream().map(this::toEspacioDomain).toList();

        List<MaterialRef> materiales =
                entity.getMateriales().stream().map(this::toMaterialRefDomain).toList();

        Integer id = entity.getId() != null ? entity.getId().intValue() : null;

        Area area = Area.crear(
                entity.getNombreArea(), entity.getTipoArea(), entity.getMetrosCuadrados(), espacios, materiales);

        return id != null ? area.conId(id) : area;
    }

    /**
     * Transforma un agregado de dominio {@link Area} en una entidad JPA {@link AreaEntity}.
     *
     * @param area agregado de dominio a mapear.
     * @return entidad {@link AreaEntity} lista para persistir.
     */
    AreaEntity toEntity(Area area) {
        Objects.requireNonNull(area, "area es obligatoria");

        AreaEntity entity = new AreaEntity();
        if (area.getId() != null) {
            entity.setId(area.getId().longValue());
        }
        entity.setNombreArea(area.getNombreArea());
        entity.setTipoArea(area.getTipoArea());
        entity.setMetrosCuadrados(area.getMetrosCuadrados());

        area.getEspacios().forEach(espacio -> entity.agregarEspacio(toEspacioEntity(espacio)));
        area.getMateriales().forEach(material -> entity.agregarMaterial(toMaterialEntity(material)));

        return entity;
    }

    private Espacio toEspacioDomain(EspacioEntity espacioEntity) {
        return new Espacio(
                espacioEntity.getNombreEspacio(),
                espacioEntity.getDescripcion(),
                espacioEntity.getFrecuenciaLimpieza());
    }

    private EspacioEntity toEspacioEntity(Espacio espacio) {
        EspacioEntity entity = new EspacioEntity();
        entity.setNombreEspacio(espacio.nombreEspacio());
        entity.setDescripcion(espacio.descripcion());
        entity.setFrecuenciaLimpieza(espacio.frecuenciaLimpieza());
        return entity;
    }

    private MaterialRef toMaterialRefDomain(AreaMaterialEntity materialEntity) {
        return new MaterialRef(
                materialEntity.getCodigoMaterial(), materialEntity.getNombreMaterial(), materialEntity.getCantidad());
    }

    private AreaMaterialEntity toMaterialEntity(MaterialRef materialRef) {
        AreaMaterialEntity entity = new AreaMaterialEntity();
        entity.setCodigoMaterial(materialRef.codigoMaterial());
        entity.setNombreMaterial(materialRef.nombreMaterial());
        entity.setCantidad(materialRef.cantidad());
        return entity;
    }
}
