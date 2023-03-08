package Modelo.Productos;

import Modelo.Utils.ConstantesNumericas;
import Modelo.Utils.ManejadorPrecios;

public class ProductoDuravit extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_DURAVIT;

    public ProductoDuravit(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoDescontado = costo;
    }

    public ProductoDuravit(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
        costoDescontado = costo;
    }

    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costo + (costo * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
