package mx.itson.potroalmacen;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifica las fronteras entre módulos con Spring Modulith.
 *
 * <p>Falla si un módulo usa clases internas de otro (cualquier subpaquete que no sea su paquete base ni un puerto de
 * entrada expuesto) o si aparece un ciclo de dependencias. No levanta Spring ni la base de datos.
 */
class ModularidadTest {

    private static final ApplicationModules MODULOS = ApplicationModules.of(PotroAlmacenApplication.class);

    @Test
    void losModulosRespetanSusFronteras() {
        MODULOS.verify();
    }
}
