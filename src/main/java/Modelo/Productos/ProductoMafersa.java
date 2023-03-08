package Modelo.Productos;

import Modelo.Utils.ConstantesNumericas;
import Modelo.Utils.ManejadorPrecios;

public class ProductoMafersa extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_MAFERSA;

    public ProductoMafersa(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = (int) (menosTreinta + (menosTreinta * ConstantesNumericas.MEDIO_IVA) / 100);
    }

    public ProductoMafersa(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = (int) (menosTreinta + (menosTreinta * ConstantesNumericas.MEDIO_IVA) / 100);
    }

    @Override
    public void calcularPrecio() {
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();

        int parcial = costoDescontado + (costoDescontado * porcentaje) / 100;
        this.precio = manejadorPrecios.redondearPrecio(parcial);
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
