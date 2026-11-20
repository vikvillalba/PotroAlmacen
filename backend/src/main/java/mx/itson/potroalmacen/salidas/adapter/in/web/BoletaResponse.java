package mx.itson.potroalmacen.salidas.adapter.in.web;

import java.time.LocalDateTime;
import java.util.List;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;

public record BoletaResponse(
        Integer id,
        String folio,
        Integer idSolicitante,
        String nombreSolicitante,
        Integer idEntregador,
        String nombreEntregador,
        LocalDateTime fechaHora,
        EstadoBoleta estado,
        List<MaterialEntregadoResponse> materiales) {

    public record MaterialEntregadoResponse(String codigoMaterial, String nombreMaterial, int cantidad) {

        public static MaterialEntregadoResponse fromDomain(MaterialRef ref) {
            return new MaterialEntregadoResponse(ref.codigoMaterial(), ref.nombreMaterial(), ref.cantidad());
        }
    }

    public static BoletaResponse fromDomain(Boleta boleta) {
        List<MaterialEntregadoResponse> items = boleta.getMateriales().stream()
                .map(MaterialEntregadoResponse::fromDomain)
                .toList();

        return new BoletaResponse(
                boleta.getId(),
                boleta.getFolio(),
                boleta.getSolicitante().idEmpleado(),
                boleta.getSolicitante().nombreCompleto(),
                boleta.getEntregador().idEmpleado(),
                boleta.getEntregador().nombreCompleto(),
                boleta.getFechaHora(),
                boleta.getEstado(),
                items);
    }
}
