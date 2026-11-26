package mx.itson.potroalmacen.personal.adapter.in.web;

import mx.itson.potroalmacen.personal.domain.Empleado;

public record EmpleadoResponse(
        Integer id,
        String nombre,
        String puesto,
        String area,
        Integer idArea,
        String tipo,
        String estado,
        boolean activo) {

    public static EmpleadoResponse de(Empleado empleado) {
        var areas = empleado.getAreasAsignadas();
        String areaPrincipal = areas.isEmpty() ? "Sin área" : areas.get(0).nombreArea();
        Integer idArea = areas.isEmpty() ? null : areas.get(0).idArea();

        String estado = !empleado.isActivo() ? "Inactivo" : (empleado.isEnPrestamo() ? "En préstamo" : "Activo");

        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombreCompleto(),
                empleado.getPuesto(),
                areaPrincipal,
                idArea,
                empleado.getTipoEmpleado().getEtiqueta(),
                estado,
                empleado.isActivo());
    }
}
