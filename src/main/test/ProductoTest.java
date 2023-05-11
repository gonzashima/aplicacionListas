import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.*;
import Modelo.Utils.ManejadorPrecios;
import org.junit.Test;
import org.junit.Assert;

public class ProductoTest {

    @Test
    public void productoDuravitPrecio() {
        Producto producto = new ProductoDuravit("producto", 123, 300);
        producto.calcularPrecio();
        Assert.assertEquals(600, producto.getPrecio());
    }

    @Test
    public void productoMafersaPrecio() {
        int costo = 300;
        Producto producto = new ProductoMafersa("p", 213, costo);
        producto.calcularPrecio();

        int costoRestado = costo - (costo*30)/100;
        int costoFinal = (int) (costoRestado + ConstantesNumericas.MEDIO_IVA * costoRestado /100);
        int precioFinal = costoFinal + costoFinal * producto.getPorcentaje() /100;
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        costoFinal = manejadorPrecios.redondearPrecio(precioFinal);

        Assert.assertEquals(costoFinal, producto.getPrecio());
    }

    @Test
    public void productoLumilagroPrecio(){
        int costo = 300;
        Producto producto = new ProductoLumilagro("p", 213, costo);
        producto.calcularPrecio();

        int costoRestado = costo - (costo*30)/100;
        int costoFinal = costoRestado + ConstantesNumericas.IVA * costoRestado /100;
        int precioFinal = costoFinal + costoFinal * producto.getPorcentaje() /100;
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        costoFinal = manejadorPrecios.redondearPrecio(precioFinal);

        Assert.assertEquals(costoFinal, producto.getPrecio());
    }
}
