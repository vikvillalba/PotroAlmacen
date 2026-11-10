package mx.itson.potroalmacen.seguridad;

/**
 * Obtiene el {@link Actor} de la petición actual.
 *
 * <p>Es el único punto que conoce el mecanismo de autenticación. En el perfil dev devuelve un actor fijo;
 * cuando se habilite la autenticación (local o Active Directory) solo se reemplaza la implementación de esta
 * interfaz, sin tocar controladores ni casos de uso.
 */
public interface ActorResolver {

    /**
     * Devuelve el actor de la petición actual.
     *
     * @return el actor autenticado
     */
    Actor resolve();
}
