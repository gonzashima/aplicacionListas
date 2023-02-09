package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

public class ProductoLumilagro extends Producto{

    public ProductoLumilagro(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
    }

    public ProductoLumilagro(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
    }

    @Override
    public void calcularPrecio() {
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        int costoDescontado = costo - (costo * 30) / 100;
        int ivaSumado = costoDescontado + (costoDescontado * 21) / 100;
        int parcial = ivaSumado + ivaSumado * porcentaje;

        this.precio = manejadorPrecios.redondearPrecio(parcial);
    }
}
