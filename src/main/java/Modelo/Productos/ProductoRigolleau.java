package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRigolleau extends Producto{

    public ProductoRigolleau(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        actualizarCostoParcial();
    }

    public ProductoRigolleau(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        actualizarCostoParcial();
    }

    @Override
    public int codigoCasa() {
        return ConstantesNumericas.CODIGO_RIGOLLEAU;
    }

    @Override
    public void actualizarCostoParcial() {
        int menosDiez = costo - (costo * 10) / 100;
        costoParcial = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
    }
}
