/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;

/**
 * Tabla {@code sal_boleta}: encabezado de la boleta de entrega.
 *
 * <p>El solicitante y el entregador pertenecen al módulo Personal, así que aquí solo se guarda su id (sin FK ni
 * relación JPA entre módulos) junto con una copia del nombre y el puesto al momento de la entrega. Los materiales
 * van en {@link BoletaDetalleEntity}, que sí es del mismo módulo y se relaciona directamente.
 */
@Entity
@Table(name = "sal_boleta")
public class BoletaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String folio;

    @Column(name = "solicitante_id", nullable = false)
    private Integer solicitanteId;

    @Column(name = "solicitante_nombre", nullable = false, length = 200)
    private String solicitanteNombre;

    @Column(name = "solicitante_puesto", length = 120)
    private String solicitantePuesto;

    @Column(name = "entregador_id", nullable = false)
    private Integer entregadorId;

    @Column(name = "entregador_nombre", nullable = false, length = 200)
    private String entregadorNombre;

    @Column(name = "entregador_puesto", length = 120)
    private String entregadorPuesto;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoBoleta estado;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoletaDetalleEntity> detalle = new ArrayList<>();

    @Version
    private Long version;

    protected BoletaEntity() {
        // Requerido por JPA.
    }

    /** Agrega una línea y mantiene los dos lados de la relación sincronizados. */
    public void agregarDetalle(BoletaDetalleEntity linea) {
        linea.setBoleta(this);
        detalle.add(linea);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public Integer getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Integer solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getSolicitanteNombre() { return solicitanteNombre; }
    public void setSolicitanteNombre(String solicitanteNombre) { this.solicitanteNombre = solicitanteNombre; }

    public String getSolicitantePuesto() { return solicitantePuesto; }
    public void setSolicitantePuesto(String solicitantePuesto) { this.solicitantePuesto = solicitantePuesto; }

    public Integer getEntregadorId() { return entregadorId; }
    public void setEntregadorId(Integer entregadorId) { this.entregadorId = entregadorId; }

    public String getEntregadorNombre() { return entregadorNombre; }
    public void setEntregadorNombre(String entregadorNombre) { this.entregadorNombre = entregadorNombre; }

    public String getEntregadorPuesto() { return entregadorPuesto; }
    public void setEntregadorPuesto(String entregadorPuesto) { this.entregadorPuesto = entregadorPuesto; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoBoleta getEstado() { return estado; }
    public void setEstado(EstadoBoleta estado) { this.estado = estado; }

    public List<BoletaDetalleEntity> getDetalle() { return detalle; }

    public Long getVersion() { return version; }
}
