import modelo.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductoTest {

    @Test
    public void productoSeInstanciaCorrectamente(){
        Producto producto = new Producto("leche", 5, 5.0);
        assertNotNull(producto);
    }

    @Test
    public void calculaPrecioCorrectamente(){
        Producto producto = new Producto("x", 10, 6.5);
        producto.calcularPrecio();
        assertEquals(20, producto.precio(), 0.0);
    }
}
