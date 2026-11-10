package mx.itson.potroalmacen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del monolito modular.
 *
 * <p>Cada subpaquete directo de {@code mx.itson.potroalmacen} es un módulo de Spring Modulith. Los módulos solo se
 * comunican por sus puertos públicos o por eventos de dominio; la prueba {@code ModularidadTest} lo verifica en el CI.
 */
@SpringBootApplication
public class PotroAlmacenApplication {

    public static void main(String[] args) {
        SpringApplication.run(PotroAlmacenApplication.class, args);
    }
}
