package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoDifPlast extends Producto {

    public ProductoDifPlast(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoDifPlast(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_DIFPLAST;
    }

    @Override
    public void actualizarCostoParcial() {
        int menosVeinte = costo - costo * 20 / 100;
        costoParcial = (int) (menosVeinte + menosVeinte * ConstantesNumericas.MEDIO_IVA / 100);
    }
}
