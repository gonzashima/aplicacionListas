package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRodeca extends Producto{

    public ProductoRodeca(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoParcial = costo;
    }

    public ProductoRodeca(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoParcial = costo;
    }

    //TODO ver codigo rodeca real
    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_RODECA;
    }
}
