package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoDuravit extends Producto{

    public ProductoDuravit(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoDuravit(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_TREBOL;
    }

    @Override
    public void actualizarCostoParcial() {
        costoParcial = costo;
    }
}
