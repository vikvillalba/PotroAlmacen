package mx.itson.potroalmacen.auditoria.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;

/**
 * Tabla {@code aud_registro_auditoria}: bitácora de lo que ocurre en el sistema.
 *
 * <p>Es solo de alta: un registro no se edita ni se borra, por eso no lleva {@code @Version}. No se relaciona con las
 * tablas de otros módulos: el usuario se guarda por su id y el registro afectado por su módulo, entidad y referencia
 * de negocio (por ejemplo el folio de una boleta), sin FK ni relación JPA.
 */
@Entity
@Table(name = "aud_registro_auditoria")
public class RegistroAuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(length = 80)
    private String campus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccionAuditoria accion;

    @Column(length = 60)
    private String modulo;

    @Column(length = 80)
    private String entidad;

    @Column(length = 100)
    private String referencia;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    protected RegistroAuditoriaEntity() {
        // Requerido por JPA.
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    public AccionAuditoria getAccion() { return accion; }
    public void setAccion(AccionAuditoria accion) { this.accion = accion; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public Instant getOcurridoEn() { return ocurridoEn; }
    public void setOcurridoEn(Instant ocurridoEn) { this.ocurridoEn = ocurridoEn; }
}