package mx.itson.potroalmacen.areas.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.itson.potroalmacen.areas.domain.exceptions.AreaSinEspaciosException;
import mx.itson.potroalmacen.areas.domain.exceptions.MetrosCuadradosInvalidosException;

public class Area {

    private final Integer id;
    private String nombreArea;
    private TipoArea tipoArea;
    private BigDecimal metrosCuadrados;
    private final List<Espacio> espacios;
    private final List<MaterialRef> materiales; // Usamos MaterialRef en lugar de la entidad Material

    private Area(
            Integer id,
            String nombreArea,
            TipoArea tipoArea,
            BigDecimal metrosCuadrados,
            List<Espacio> espacios,
            List<MaterialRef> materiales) {
        this.id = id;
        this.nombreArea = nombreArea;
        this.tipoArea = tipoArea;
        this.metrosCuadrados = metrosCuadrados;
        this.espacios = espacios != null ? new ArrayList<>(espacios) : new ArrayList<>();
        this.materiales = materiales != null ? new ArrayList<>(materiales) : new ArrayList<>();
    }

    /** Fábrica de dominio para crear una nueva área con validaciones estrictas. */
    public static Area crear(
            String nombreArea,
            TipoArea tipoArea,
            BigDecimal metrosCuadrados,
            List<Espacio> espacios,
            List<MaterialRef> materiales) {
        validarDatos(nombreArea, metrosCuadrados, espacios);
        return new Area(null, nombreArea.trim(), tipoArea, metrosCuadrados, espacios, materiales);
    }

    /** Reconstituye el agregado con ID de base de datos. */
    public Area conId(Integer id) {
        return new Area(id, this.nombreArea, this.tipoArea, this.metrosCuadrados, this.espacios, this.materiales);
    }

    // MÉTODOS DE NEGOCIO / COMPORTAMIENTO

    public void agregarMaterial(MaterialRef material) {
        if (material == null) {
            throw new IllegalArgumentException("El material no puede ser nulo.");
        }
        if (!materiales.contains(material)) {
            materiales.add(material);
        }
    }

    public void removerMaterial(MaterialRef material) {
        materiales.remove(material);
    }

    public void agregarEspacio(Espacio espacio) {
        if (espacio == null) {
            throw new IllegalArgumentException("El espacio no puede ser nulo.");
        }
        espacios.add(espacio);
    }

    public void actualizarInformacion(String nuevoNombre, TipoArea nuevoTipo, BigDecimal nuevosMetros) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del área es obligatorio.");
        }
        if (nuevosMetros == null || nuevosMetros.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MetrosCuadradosInvalidosException();
        }
        this.nombreArea = nuevoNombre.trim();
        this.tipoArea = nuevoTipo != null ? nuevoTipo : this.tipoArea;
        this.metrosCuadrados = nuevosMetros;
    }

    private static void validarDatos(String nombreArea, BigDecimal metrosCuadrados, List<Espacio> espacios) {
        if (nombreArea == null || nombreArea.isBlank()) {
            throw new IllegalArgumentException("El nombre del área es obligatorio.");
        }
        if (metrosCuadrados == null || metrosCuadrados.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MetrosCuadradosInvalidosException();
        }
        if (espacios == null || espacios.isEmpty()) {
            throw new AreaSinEspaciosException();
        }
    }

    // Getters de solo lectura
    public Integer getId() {
        return id;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public TipoArea getTipoArea() {
        return tipoArea;
    }

    public BigDecimal getMetrosCuadrados() {
        return metrosCuadrados;
    }

    public List<Espacio> getEspacios() {
        return Collections.unmodifiableList(espacios);
    }

    public List<MaterialRef> getMateriales() {
        return Collections.unmodifiableList(materiales);
    }
}
