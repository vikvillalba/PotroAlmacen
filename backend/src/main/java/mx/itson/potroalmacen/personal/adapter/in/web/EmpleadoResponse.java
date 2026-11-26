package mx.itson.potroalmacen.personal.adapter.in.web;

import mx.itson.potroalmacen.personal.domain.Empleado;

public record EmpleadoResponse(
        Integer id, String nombre, String puesto, String area, String tipo, String estado, boolean activo) {

    public static EmpleadoResponse de(Empleado empleado) {
        String areaPrincipal = empleado.getAreasAsignadas().isEmpty()
                ? "Sin área"
                : empleado.getAreasAsignadas().get(0).nombreArea();

        String estado = !empleado.isActivo() ? "Inactivo" : (empleado.isEnPrestamo() ? "En préstamo" : "Activo");

        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombreCompleto(),
                empleado.getPuesto(),
                areaPrincipal,
                empleado.getTipoEmpleado().getEtiqueta(),
                estado,
                empleado.isActivo());
    }
}
