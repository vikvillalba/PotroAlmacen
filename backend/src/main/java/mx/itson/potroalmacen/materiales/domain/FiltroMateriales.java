package mx.itson.potroalmacen.materiales.domain;

/**
 * Criterios para consultar materiales. Todos son opcionales: un criterio nulo (o texto en blanco) no filtra, y los
 * que se den se combinan con Y.
 *
 * @param nombre busca dentro del nombre, sin distinguir mayúsculas
 * @param codigo busca dentro del código, sin distinguir mayúsculas
 * @param categoria solo materiales de esta categoría
 * @param presentacion solo materiales con esta presentación
 */
public record FiltroMateriales(String nombre, String codigo, Categoria categoria, Presentacion presentacion) {

    public FiltroMateriales {
        nombre = normalizar(nombre);
        codigo = normalizar(codigo);
    }

    public static FiltroMateriales sinFiltros() {
        return new FiltroMateriales(null, null, null, null);
    }

    public boolean estaVacio() {
        return nombre == null && codigo == null && categoria == null && presentacion == null;
    }

    /** Indica si el material cumple todos los criterios. Es la definición de referencia del filtro. */
    public boolean aplicaA(Material material) {
        return contiene(material.getNombre(), nombre)
                && contiene(material.getCodigo().valor(), codigo)
                && (categoria == null || material.getCategoria() == categoria)
                && (presentacion == null || material.getPresentacion() == presentacion);
    }

    private static boolean contiene(String valor, String buscado) {
        return buscado == null || valor.toLowerCase().contains(buscado.toLowerCase());
    }

    private static String normalizar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
