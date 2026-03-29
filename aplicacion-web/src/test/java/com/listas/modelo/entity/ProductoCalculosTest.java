package com.listas.modelo.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductoCalculosTest {

    private static final int COSTO = 300;

    @ParameterizedTest
    @CsvSource({
            "duravit,600",
            "respontech,600",
            "mafersa,460",
            "lumilagro,500",
            "rigolleau,650",
            "lema,660",
            "rodeca,660",
            "difplast,550"
    })
    void calculaPrecioSegunCasa(String tipoCasa, int esperado) {
        assertPrecio(tipoCasa, esperado);
    }

    @Test
    void tipoCasaNuloUsaCostoBase() {
        assertPrecio(null, 600);
    }

    private void assertPrecio(String tipoCasa, int esperado) {
        Producto p = new Producto(123, "PRODUCTO TEST", COSTO, 100, 1);
        p.calcularPrecio(tipoCasa);
        assertEquals(esperado, p.getPrecio());
    }
}

