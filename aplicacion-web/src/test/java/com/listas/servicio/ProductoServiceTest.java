package com.listas.servicio;

import com.listas.modelo.repository.ListaRepository;
import com.listas.modelo.repository.ProductoRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ProductoServiceTest {

    @ParameterizedTest
    @CsvSource({
            "mafersa,'MATE RIVER 1L',mafersa",
            "lumilagro,'repuesto termo universal',lumilagro",
            "lumilagro,'JARRA ACERO INOX',mafersa",
            "duravit,'PRODUCTO X',duravit"
    })
    void detectarTipoCasaRespetaReglas(String base, String nombre, String esperado) throws Exception {
        ProductoService service = new ProductoService(mock(ProductoRepository.class), mock(ListaRepository.class));
        assertEquals(esperado, detectarTipoCasa(service, base, nombre));
    }

    private String detectarTipoCasa(ProductoService service, String tipoCasaBase, String nombreProducto) throws Exception {
        Method method = ProductoService.class.getDeclaredMethod("detectarTipoCasa", String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, tipoCasaBase, nombreProducto);
    }
}





