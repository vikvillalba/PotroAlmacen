package mx.itson.potroalmacen.personal.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.itson.potroalmacen.personal.domain.exceptions.EmpleadoInvalidoException;

public class Empleado {

    private final Integer id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private TipoEmpleado tipoEmpleado;
    private String puesto;
    private String jefeDirecto;
    private String telefono;
    private boolean activo;
    private boolean enPrestamo;
    private final List<AreaRef> areasAsignadas;

    private Empleado(
            Integer id,
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            TipoEmpleado tipoEmpleado,
            String puesto,
            String jefeDirecto,
            String telefono,
            boolean activo,
            boolean enPrestamo,
            List<AreaRef> areasAsignadas) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.tipoEmpleado = tipoEmpleado;
        this.puesto = puesto;
        this.jefeDirecto = jefeDirecto;
        this.telefono = telefono;
        this.activo = activo;
        this.enPrestamo = enPrestamo;
        this.areasAsignadas = areasAsignadas != null ? new ArrayList<>(areasAsignadas) : new ArrayList<>();
    }

    /** Fábrica de dominio para crear un nuevo empleado con validaciones de negocio. */
    public static Empleado crear(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            TipoEmpleado tipoEmpleado,
            String puesto,
            String jefeDirecto,
            String telefono,
            List<AreaRef> areasAsignadas) {
        validarDatos(nombre, apellidoPaterno, tipoEmpleado, puesto);
        return new Empleado(
                null,
                nombre.trim(),
                apellidoPaterno.trim(),
                apellidoMaterno != null ? apellidoMaterno.trim() : null,
                tipoEmpleado,
                puesto.trim(),
                jefeDirecto != null ? jefeDirecto.trim() : null,
                telefono != null ? telefono.trim() : null,
                true, // Por defecto nace activo
                false, // Por defecto no tiene préstamo activo
                areasAsignadas);
    }

    /** Reconstituye el agregado con su ID y estados desde la base de datos. */
    public Empleado reconstitadir(Integer id, boolean activo, boolean enPrestamo) {
        return new Empleado(
                id,
                this.nombre,
                this.apellidoPaterno,
                this.apellidoMaterno,
                this.tipoEmpleado,
                this.puesto,
                this.jefeDirecto,
                this.telefono,
                activo,
                enPrestamo,
                this.areasAsignadas);
    }

    // MÉTODOS DE NEGOCIO / COMPORTAMIENTO

    /** Devuelve el nombre completo uniendo nombre y apellidos. */
    public String getNombreCompleto() {
        if (apellidoMaterno == null || apellidoMaterno.isBlank()) {
            return nombre + " " + apellidoPaterno;
        }
        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }

    public void activar() {
        this.activo = true;
    }

    public void inactivar() {
        this.activo = false;
    }

    public void marcarEnPrestamo() {
        this.enPrestamo = true;
    }

    public void liberarPrestamo() {
        this.enPrestamo = false;
    }

    /** Asigna una nueva área de responsabilidad o trabajo al empleado. */
    public void asignarArea(AreaRef area) {
        if (area == null) {
            throw new IllegalArgumentException("La referencia de área no puede ser nula.");
        }
        if (!areasAsignadas.contains(area)) {
            areasAsignadas.add(area);
        }
    }

    /** Remueve la asignación de un área. */
    public void removerArea(AreaRef area) {
        areasAsignadas.remove(area);
    }

    /** Actualiza la información personal, laboral o de contacto del empleado. */
    public void actualizarInformacion(
            String nuevoNombre,
            String nuevoApellidoPaterno,
            String nuevoApellidoMaterno,
            String nuevoPuesto,
            String nuevoJefe,
            String nuevoTelefono) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new EmpleadoInvalidoException("El nombre del empleado es obligatorio.");
        }
        if (nuevoApellidoPaterno == null || nuevoApellidoPaterno.isBlank()) {
            throw new EmpleadoInvalidoException("El apellido paterno es obligatorio.");
        }
        if (nuevoPuesto == null || nuevoPuesto.isBlank()) {
            throw new EmpleadoInvalidoException("El puesto del empleado es obligatorio.");
        }
        this.nombre = nuevoNombre.trim();
        this.apellidoPaterno = nuevoApellidoPaterno.trim();
        this.apellidoMaterno = nuevoApellidoMaterno != null ? nuevoApellidoMaterno.trim() : null;
        this.puesto = nuevoPuesto.trim();
        this.jefeDirecto = nuevoJefe != null ? nuevoJefe.trim() : this.jefeDirecto;
        this.telefono = nuevoTelefono != null ? nuevoTelefono.trim() : this.telefono;
    }

    private static void validarDatos(String nombre, String apellidoPaterno, TipoEmpleado tipoEmpleado, String puesto) {
        if (nombre == null || nombre.isBlank()) {
            throw new EmpleadoInvalidoException("El nombre del empleado es obligatorio.");
        }
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new EmpleadoInvalidoException("El apellido paterno es obligatorio.");
        }
        if (tipoEmpleado == null) {
            throw new EmpleadoInvalidoException("El tipo de empleado es obligatorio.");
        }
        if (puesto == null || puesto.isBlank()) {
            throw new EmpleadoInvalidoException("El puesto del empleado es obligatorio.");
        }
    }

    // Getters de solo lectura
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }

    public String getPuesto() {
        return puesto;
    }

    public String getJefeDirecto() {
        return jefeDirecto;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isEnPrestamo() {
        return enPrestamo;
    }

    public List<AreaRef> getAreasAsignadas() {
        return Collections.unmodifiableList(areasAsignadas);
    }
}
