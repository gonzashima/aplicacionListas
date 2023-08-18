package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoLema extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_LEMA;

    public ProductoLema(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoDescontado = costo;
    }

    public ProductoLema(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoDescontado = costo;
    }

    @Override
    public int codigoCasa() {
        return CODIGO;
    }
}
