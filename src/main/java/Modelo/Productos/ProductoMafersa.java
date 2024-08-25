package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoMafersa extends Producto{

    public ProductoMafersa(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoMafersa(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_MAFERSA;
    }

    @Override
    public void actualizarCostoParcial() {
        int menosTreinta = costo - (costo * 30) / 100;
        costoParcial = (int) (menosTreinta + (menosTreinta * ConstantesNumericas.MEDIO_IVA) / 100);
    }
}
