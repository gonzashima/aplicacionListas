import Modelo.Productos.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductoTest {

    @Test
    public void productoSeInstanciaCorrectamente(){
        Producto producto = new Producto("leche", 5, 5);
        assertNotNull(producto);
    }

    @Test
    public void calculaPrecioCorrectamente(){
        Producto producto = new Producto("x", 10, 6);
        producto.calcularPrecio();
        assertEquals(10, producto.getPrecio(), 0);

        Producto p2 = new Producto(" ", 1, 110);
        p2.calcularPrecio();
        assertEquals(200, p2.getPrecio());
    }
}
