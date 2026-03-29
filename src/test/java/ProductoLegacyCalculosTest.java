import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDifPlast;
import Modelo.Productos.ProductoDuravit;
import Modelo.Productos.ProductoLema;
import Modelo.Productos.ProductoLumilagro;
import Modelo.Productos.ProductoMafersa;
import Modelo.Productos.ProductoRespontech;
import Modelo.Productos.ProductoRigolleau;
import Modelo.Productos.ProductoRodeca;
import org.junit.Assert;
import org.junit.Test;

public class ProductoLegacyCalculosTest {

    private static final int COSTO = 300;

    @Test
    public void duravitYRespontechMantienenCalculoBase() {
        assertPrecio(new ProductoDuravit("x", 1, COSTO), 600);
        assertPrecio(new ProductoRespontech("x", 2, COSTO), 600);
    }

    @Test
    public void mafersaYLumilagroAplicanFormulasDistintas() {
        assertPrecio(new ProductoMafersa("x", 3, COSTO), 460);
        assertPrecio(new ProductoLumilagro("x", 4, COSTO), 500);
    }

    @Test
    public void rigolleauLemaRodecaYDifplastMantienenRedondeoLegacy() {
        assertPrecio(new ProductoRigolleau("x", 5, COSTO), 650);
        assertPrecio(new ProductoLema("x", 6, COSTO), 660);
        assertPrecio(new ProductoRodeca("x", 7, COSTO), 660);
        assertPrecio(new ProductoDifPlast("x", 8, COSTO), 550);
    }

    private void assertPrecio(Producto producto, int esperado) {
        producto.calcularPrecio();
        Assert.assertEquals(esperado, producto.getPrecio());
    }
}

