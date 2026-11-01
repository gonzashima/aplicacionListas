package com.listas.servicio;

import com.listas.modelo.entity.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MafersaDaysalImportTest {

    private final CapturadorProductoService productos = new CapturadorProductoService();
    private final LectorArchivoService lector = new LectorArchivoService(productos);

    @ParameterizedTest
    @ValueSource(strings = {"12D - DAYS", "12D - DAYSAL"})
    void importaDaysalConAmbosEncabezadosSinCambiarFiltrosNiRubrosVecinos(String rubro) throws Exception {
        String texto;
        try (var fixture = Objects.requireNonNull(getClass().getResourceAsStream("/mafersa/daysal.txt"))) {
            texto = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        String mensaje = lector.procesarArchivo(pdf(texto.replace("12D - DAYS", rubro)));

        List<Producto> importados = productosImportados();
        Map<Integer, List<Producto>> porLista = importados.stream().collect(Collectors.groupingBy(Producto::getListaId));
        assertEquals(Map.of(11, 2, 12, 27, 13, 1), porLista.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
        assertEquals("mafersa-daysal se leyó correctamente. 30 productos procesados.", mensaje);
        assertEquals(27, porLista.get(12).stream().map(Producto::getCodigo).distinct().count());
        assertTrue(importados.stream().noneMatch(p -> p.getCodigo() == 10269 || p.getCodigo() == 9998),
                "Vajilla decorada sigue excluida por el filtro existente de líneas");

        Producto aceitera = porLista.get(12).stream().filter(p -> p.getCodigo() == 10484).findFirst().orElseThrow();
        assertEquals("ACEITERA/VINAGRERA C/B DAYS.", aceitera.getNombre());
        assertEquals(5417, aceitera.getCosto());
        assertEquals(8380, aceitera.getPrecio());
        assertEquals(100, aceitera.getPorcentaje());
        Producto azucarera = porLista.get(12).stream().filter(p -> p.getCodigo() == 12450).findFirst().orElseThrow();
        assertEquals(2847, azucarera.getCosto());
        assertEquals(4400, azucarera.getPrecio());
        assertTrue(porLista.get(12).stream().anyMatch(p -> p.getCodigo() == 9035),
                "También se importa la última fila con una sola columna");
    }

    @ParameterizedTest
    @ValueSource(strings = {"RUBRO : 12D - DAYS\nLINEA : 121AL - DAYSAL ALLEGRA",
            "RUBRO : 12D - DAYS\nLINEA : 121AL - DAYSAL ALLEGRA\n10484 47-V2 ACEITERA 5417.28\n12450 45-1 AZUCARERA SIN_PRECIO"})
    void daysalVacioOMalformadoNoReemplazaNingunaLista(String texto) throws Exception {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> lector.procesarArchivo(pdf(texto)));

        assertTrue(error.getMessage().contains("Daysal"));
        assertNull(productos.importados);
    }

    @Test
    void archivoSinProductosNoInformaExitoNiEscribeEnLaBase() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> lector.procesarArchivo(pdf("LISTA DE PRECIOS")));
        assertNull(productos.importados);
    }

    @Test
    void noConfundeDaysalMencionadoEnOtraLineaConElRubroDaysal() throws Exception {
        lector.procesarArchivo(pdf("RUBRO : 11K - KUFO\nLINEA : 11K - KUFO\n12007 ASA017 ARTICULO DAYSAL 12613.00"));
        assertEquals(11, productosImportados().get(0).getListaId());
    }

    private List<Producto> productosImportados() {
        return Objects.requireNonNull(productos.importados);
    }

    private static class CapturadorProductoService extends ProductoService {
        private List<Producto> importados;

        CapturadorProductoService() {
            super(null, null);
        }

        @Override
        public int insertarProductosMafersa(List<Producto> productos) {
            this.importados = List.copyOf(productos);
            return productos.size();
        }
    }

    static MockMultipartFile pdf(String texto) throws Exception {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            PDPage pagina = new PDPage();
            doc.addPage(pagina);
            try (PDPageContentStream contenido = new PDPageContentStream(doc, pagina)) {
                contenido.beginText();
                contenido.setFont(PDType1Font.COURIER, 6);
                contenido.setLeading(12);
                contenido.newLineAtOffset(20, 760);
                for (String linea : texto.split("\n")) {
                    contenido.showText(linea);
                    contenido.newLine();
                }
                contenido.endText();
            }
            doc.save(bytes);
            return new MockMultipartFile("archivo", "mafersa-daysal.pdf", "application/pdf", bytes.toByteArray());
        }
    }
}
