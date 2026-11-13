package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest
@Testcontainers
@Transactional
class MaterialPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysqlContainer = new MySQLContainer("mysql:8.4");

    @Autowired
    private MaterialPersistenceAdapter persistenceAdapter;

    @Test
    void debeGuardarYBuscarMaterialPorCodigoExitosamente() {
        CodigoMaterial codigo = new CodigoMaterial("MAT-TC-001");

        Material nuevoMaterial = Material.crear(
                codigo,
                "Cinta de Aislar con Testcontainers",
                Categoria.values()[0],
                UnidadMedida.values()[0],
                Presentacion.values()[0],
                "Uso exclusivo en talleres",
                Campus.values()[0],
                new BigDecimal("50.00"),
                5,
                10,
                "Uso industrial");

        Material guardado = persistenceAdapter.guardar(nuevoMaterial);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();

        Optional<Material> encontrado = persistenceAdapter.buscarPorCodigo(codigo);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Cinta de Aislar con Testcontainers");
        assertThat(encontrado.get().getPrecioUnitario()).isEqualByComparingTo("50.00");
        assertThat(encontrado.get().getCantidad()).isEqualTo(0);

        assertThat(persistenceAdapter.existePorCodigo(codigo)).isTrue();
    }
}
