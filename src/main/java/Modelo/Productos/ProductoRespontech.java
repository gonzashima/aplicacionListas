package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRespontech extends Producto{

    public ProductoRespontech(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoRespontech(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_RESPONTECH;
    }

    @Override
    public void actualizarCostoParcial() {
        costoParcial = costo;
    }
}
