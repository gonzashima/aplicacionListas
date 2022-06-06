import modelo.Utils.ParserTextoAProducto;
import modelo.Productos.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    public void separaLineaCorrectamente(){
        ParserTextoAProducto parser = new ParserTextoAProducto();
        String linea = "Producto   32891  50.2";

        Producto producto = parser.aProducto(linea);

        assertEquals("Producto", producto.getNombre());
        assertEquals(32891, producto.getCodigo());
        assertEquals(50, producto.getCosto());
    }
}
