package com.listas.servicio;

import com.listas.modelo.entity.Producto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LectorArchivoServiceTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "RUBRO : lumilagro|100 FABCOD BOTELLA ACERO 300|15|460",
            "RUBRO : lumilagro|101 FABCOD REPUESTO TERMO 300|15|500",
            "RUBRO : almandoz|102 FABCOD MATE RIVER 300|3|460"
    })
    void parsearMafersaRespetaSublistaYDistintos(String rubro, String linea, int listaId, int precio) throws Exception {
        LectorArchivoService service = new LectorArchivoService(mock(ProductoService.class));
        List<Producto> productos = parsearMafersa(service, List.of(rubro, linea));

        assertEquals(1, productos.size());
        assertEquals(listaId, productos.get(0).getListaId());
        assertEquals(precio, productos.get(0).getPrecio());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "$1.475,00|1475",
            "$ 1.475,00|1475",
            "1.475|1475",
            "1475|1475",
            "1475.50|1475"
    })
    void parsearCostoSoportaFormatosEsperados(String raw, int esperado) throws Exception {
        LectorArchivoService service = new LectorArchivoService(mock(ProductoService.class));
        assertEquals(esperado, parsearCosto(service, raw));
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearMafersa(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearMafersa", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    private int parsearCosto(LectorArchivoService service, String costoRaw) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearCosto", String.class);
        method.setAccessible(true);
        return (int) method.invoke(service, costoRaw);
    }
}



