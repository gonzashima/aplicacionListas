package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRespontech extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_RESPONTECH;

    public ProductoRespontech(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoParcial = costo;
    }

    public ProductoRespontech(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoParcial = costo;
    }

    @Override
    public int codigoCasa() {
        return CODIGO;
    }
}
