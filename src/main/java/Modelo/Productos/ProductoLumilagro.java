package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;

public class ProductoLumilagro extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_MAFERSA;

    public ProductoLumilagro(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = menosTreinta + (menosTreinta * ConstantesNumericas.IVA) / 100;
    }

    public ProductoLumilagro(int id, int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(id, codigo, nombre, costo, precio, porcentaje);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = menosTreinta + (menosTreinta * ConstantesNumericas.IVA) / 100;
    }

    public int codigoCasa() {
        return CODIGO;
    }
}
