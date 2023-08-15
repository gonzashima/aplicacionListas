package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Utils.ManejadorPrecios;

public class ProductoDuravit extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_TREBOL;

    public ProductoDuravit(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoDescontado = costo + costo * 10 / 100;
    }

    public ProductoDuravit(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoDescontado = costo + costo * 10 / 100;
    }

    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costoDescontado + (costoDescontado * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
