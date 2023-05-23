package Modelo.Productos;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Utils.ManejadorPrecios;

public class ProductoRigolleau extends Producto{
    public static final int CODIGO = ConstantesNumericas.CODIGO_RIGOLLEAU;

    public ProductoRigolleau(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosDiez = costo - (costo * 10) / 100;
        costoDescontado = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
    }

    public ProductoRigolleau(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
        int menosDiez = costo - (costo * 10) / 100;
        costoDescontado = menosDiez + (menosDiez * ConstantesNumericas.IVA) / 100;
    }

    @Override
    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costoDescontado + (costoDescontado * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    @Override
    public int codigoCasa() {
        return CODIGO;
    }
}
