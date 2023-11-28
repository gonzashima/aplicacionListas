package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRodeca extends Producto{

    public ProductoRodeca(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoParcial = (int) (costo + ConstantesNumericas.MEDIO_IVA * costo / 100);
    }

    public ProductoRodeca(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoParcial = (int) (costo + ConstantesNumericas.MEDIO_IVA * costo / 100);
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_RODECA;
    }
}
