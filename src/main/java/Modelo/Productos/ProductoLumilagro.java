package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

public class ProductoLumilagro extends Producto{

    public ProductoLumilagro(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = menosTreinta + (menosTreinta * 21) / 100;
    }

    public ProductoLumilagro(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
        int menosTreinta = costo - (costo * 30) / 100;
        costoDescontado = menosTreinta + (menosTreinta * 21) / 100;
    }

    @Override
    public void calcularPrecio() {
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        int parcial = costoDescontado + costoDescontado * porcentaje;

        this.precio = manejadorPrecios.redondearPrecio(parcial);
    }
}
