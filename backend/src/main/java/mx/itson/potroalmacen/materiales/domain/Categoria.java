package mx.itson.potroalmacen.materiales.domain;

/** Categoría de un material. Cada categoría pertenece a un departamento. */
public enum Categoria {
    PINTURA("Pintura", Departamento.MANTENIMIENTO),
    MATERIALES_CONSTRUCCION("Materiales de construcción", Departamento.MANTENIMIENTO),
    MATERIAL_ELECTRICO("Material eléctrico", Departamento.MANTENIMIENTO),
    CERRAJERIA("Cerrajería", Departamento.MANTENIMIENTO),
    PLOMERIA("Plomería", Departamento.MANTENIMIENTO),
    REFRIGERACION("Refrigeración", Departamento.MANTENIMIENTO),
    FERRETERIA("Ferretería", Departamento.MANTENIMIENTO),
    HERRAMIENTA_MENOR("Herramienta menor", Departamento.MANTENIMIENTO),
    SERVICIOS_GENERALES("Servicios generales", Departamento.SERVICIOS_GENERALES);

    private final String etiqueta;
    private final Departamento departamento;

    Categoria(String etiqueta, Departamento departamento) {
        this.etiqueta = etiqueta;
        this.departamento = departamento;
    }

    /** Texto que se muestra en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }

    public Departamento getDepartamento() {
        return departamento;
    }
}
