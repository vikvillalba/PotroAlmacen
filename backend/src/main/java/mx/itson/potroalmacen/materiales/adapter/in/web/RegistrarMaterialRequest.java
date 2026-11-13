package mx.itson.potroalmacen.materiales.adapter.in.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;

/**
 * Cuerpo de {@code POST /api/materiales}. Los enums se envían con su nombre, por ejemplo {@code "PINTURA"}.
 *
 * <p>Estas validaciones solo dan mensajes por campo al frontend; las reglas de negocio las vuelve a revisar el
 * dominio.
 */
record RegistrarMaterialRequest(
        @NotBlank @Size(max = CodigoMaterial.LONGITUD_MAXIMA)
        String codigo,

        @NotBlank @Size(max = Material.NOMBRE_MAXIMO) String nombre,
        @NotNull Categoria categoria,
        @NotNull UnidadMedida unidadMedida,
        @NotNull Presentacion presentacion,
        @Size(max = Material.CONTENIDO_PRESENTACION_MAXIMO) String contenidoPresentacion,
        @NotNull Campus campus,

        @DecimalMin("0.00") @Digits(integer = 8, fraction = 2)
        BigDecimal precioUnitario,

        @NotNull @PositiveOrZero Integer existenciaInicial,
        @NotNull @PositiveOrZero Integer stockMinimo,
        @PositiveOrZero Integer stockMaximo,
        @Size(max = Material.USO_PRECAUCIONES_MAXIMO) String usoPrecauciones) {

    RegistrarMaterialCommand aComando() {
        return new RegistrarMaterialCommand(
                codigo,
                nombre,
                categoria,
                unidadMedida,
                presentacion,
                contenidoPresentacion,
                campus,
                precioUnitario,
                existenciaInicial,
                stockMinimo,
                stockMaximo,
                usoPrecauciones);
    }
}
