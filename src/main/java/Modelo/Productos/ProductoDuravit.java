package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoDuravit extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_TREBOL;

    public ProductoDuravit(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoParcial = costo;
    }

    public ProductoDuravit(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        costoParcial = costo;
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
