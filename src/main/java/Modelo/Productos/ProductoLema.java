package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoLema extends Producto{

    public ProductoLema(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoLema(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_LEMA;
    }

    @Override
    public void actualizarCostoParcial() {
        costoParcial = (int) (costo + ConstantesNumericas.MEDIO_IVA * costo / 100);
    }
}
