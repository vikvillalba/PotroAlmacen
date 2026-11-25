package mx.itson.potroalmacen.areas.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AsignarMaterialAreaRequest(
        @NotBlank String codigoMaterial,
        @NotBlank String nombreMaterial,
        @NotNull @Positive Integer cantidad) {}
