import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.*;
import Modelo.Utils.ManejadorPrecios;
import org.junit.Test;
import org.junit.Assert;

public class ProductoTest {
    private static final int COSTO = 300;

    @Test
    public void productoDuravitPrecio() {
        Producto producto = new ProductoDuravit("producto", 123, COSTO);
        producto.calcularPrecio();
        Assert.assertEquals(600, producto.getPrecio());
    }

    @Test
    public void productoMafersaPrecio() {
        Producto producto = new ProductoMafersa("p", 213, COSTO);
        producto.calcularPrecio();

        int costoRestado = COSTO - (COSTO*30)/100;
        int costoFinal = (int) (costoRestado + ConstantesNumericas.MEDIO_IVA * costoRestado /100);
        costoFinal = costoFinal + costoFinal * 20/100;
        int precioFinal = costoFinal + costoFinal * producto.getPorcentaje() /100;

        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        costoFinal = manejadorPrecios.redondearPrecio(precioFinal);

        Assert.assertEquals(costoFinal, producto.getPrecio());
    }

    @Test
    public void productoLumilagroPrecio() {
        Producto producto = new ProductoLumilagro("p", 213, COSTO);
        producto.calcularPrecio();

        int costoRestado = COSTO - (COSTO*30)/100;
        int costoFinal = costoRestado + ConstantesNumericas.IVA * costoRestado /100;
        int precioFinal = costoFinal + costoFinal * producto.getPorcentaje() /100;

        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        costoFinal = manejadorPrecios.redondearPrecio(precioFinal);

        Assert.assertEquals(costoFinal, producto.getPrecio());
    }

    @Test
    public void productoRespontechPrecio() {
        Producto producto = new ProductoRespontech("p", 213, COSTO);
        producto.calcularPrecio();

        Assert.assertEquals(600, producto.getPrecio());
    }

    @Test
    public void productoRIgolleauPrecio() {
        Producto producto = new ProductoRigolleau("p", 213, COSTO);
        producto.calcularPrecio();

        int menosDiez = COSTO - (COSTO * 10) / 100;
        int costoDescontado = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
        int precioFinal = costoDescontado + costoDescontado * producto.getPorcentaje() / 100;

        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        costoDescontado = manejadorPrecios.redondearPrecio(precioFinal);

        Assert.assertEquals(costoDescontado, producto.getPrecio());
    }

    @Test
    public void productoLemaPrecio() {
        Producto producto = new ProductoLema("p", 213, COSTO);
        producto.calcularPrecio();

        Assert.assertEquals(600, producto.getPrecio());
    }
}
