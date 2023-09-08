package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoMafersa extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_MAFERSA;

    public ProductoMafersa(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = (int) (menosTreinta + (menosTreinta * ConstantesNumericas.MEDIO_IVA) / 100);
    }

    public ProductoMafersa(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = (int) (menosTreinta + (menosTreinta * ConstantesNumericas.MEDIO_IVA) / 100);
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
