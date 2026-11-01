package com.listas.servicio;

import com.listas.modelo.entity.Producto;
import com.listas.modelo.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** Ejecutar con DAYSAL_POSTGRES_TEST=true y las variables de conexión a un PostgreSQL de prueba. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIfEnvironmentVariable(named = "DAYSAL_POSTGRES_TEST", matches = "true")
@Transactional
class MafersaDaysalPersistenceTest {

    @Autowired private LectorArchivoService lector;
    @Autowired private ProductoRepository productos;
    @Autowired private EntityManager entityManager;

    @Test
    void recargarDaysalActualizaCostosConservaIdsYPorcentajesYEliminaSoloObsoletos() throws Exception {
        productos.deleteByListaId(12);
        Producto anterior = productos.saveAndFlush(new Producto(10484, "ACEITERA ANTERIOR", 1, 65, 12));
        Integer idAnterior = anterior.getId();
        Integer idObsoleto = productos.saveAndFlush(new Producto(999999, "OBSOLETO", 1, 100, 12)).getId();
        Integer idOtraLista = productos.saveAndFlush(new Producto(999998, "OTRA LISTA", 99, 75, 13)).getId();
        entityManager.clear();

        String texto;
        try (var fixture = Objects.requireNonNull(getClass().getResourceAsStream("/mafersa/daysal.txt"))) {
            texto = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }
        texto = texto.substring(texto.indexOf("RUBRO : 12D"), texto.indexOf("RUBRO : 16B"));

        for (int carga = 0; carga < 2; carga++) {
            lector.procesarArchivo(MafersaDaysalImportTest.pdf(texto));
            productos.flush();
            entityManager.clear();

            assertEquals(27, productos.findByListaId(12).size());
            Producto actualizado = productos.findById(idAnterior).orElseThrow();
            assertEquals(10484, actualizado.getCodigo());
            assertEquals("ACEITERA/VINAGRERA C/B DAYS.", actualizado.getNombre());
            assertEquals(5417, actualizado.getCosto());
            assertEquals(65, actualizado.getPorcentaje());
            assertEquals(6900, actualizado.getPrecio());
            assertTrue(productos.findById(idObsoleto).isEmpty());
            assertEquals(99, productos.findById(idOtraLista).orElseThrow().getCosto());
        }
    }

    @Test
    void daysalMalformadoNoModificaProductosExistentes() throws Exception {
        Producto existente = productos.saveAndFlush(new Producto(999997, "CONSERVAR", 500, 70, 12));
        int cantidad = productos.findByListaId(12).size();
        entityManager.clear();

        assertThrows(IllegalArgumentException.class, () -> lector.procesarArchivo(MafersaDaysalImportTest.pdf(
                "RUBRO : 12D - DAYS\nLINEA : 121AL - DAYSAL ALLEGRA\n10484 47-V2 ACEITERA 5417.28\n12450 45-1 AZUCARERA SIN_PRECIO")));
        productos.flush();
        entityManager.clear();

        assertEquals(cantidad, productos.findByListaId(12).size());
        assertEquals(500, productos.findById(existente.getId()).orElseThrow().getCosto());
    }
}
