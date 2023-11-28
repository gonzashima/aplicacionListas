package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoLema extends Producto{

    public ProductoLema(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoParcial = (int) (costo + ConstantesNumericas.MEDIO_IVA * costo / 100);
    }

    public ProductoLema(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoParcial = (int) (costo + ConstantesNumericas.MEDIO_IVA * costo / 100);
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_LEMA;
    }
}
