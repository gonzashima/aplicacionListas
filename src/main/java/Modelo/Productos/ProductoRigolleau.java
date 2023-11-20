package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoRigolleau extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_RIGOLLEAU;

    public ProductoRigolleau(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosDiez = costo - (costo * 10) / 100;
        costoParcial = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
    }

    public ProductoRigolleau(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        int menosDiez = costo - (costo * 10) / 100;
        costoParcial = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
    }

    @Override
    public int codigoCasa() {
        return CODIGO;
    }
}
