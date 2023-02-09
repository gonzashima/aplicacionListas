package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

public class ProductoMafersa extends Producto{
    public ProductoMafersa(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
    }

    public ProductoMafersa(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
    }

    @Override
    public void calcularPrecio() {
        ManejadorPrecios manejadorPrecios = new ManejadorPrecios();
        int costoDescontado = costo - (costo * 30) / 100;
        int ivaSumado = (int) (costoDescontado + (costoDescontado * 10.5) / 100);

        int parcial = ivaSumado + (ivaSumado * porcentaje) / 100;
        this.precio = manejadorPrecios.redondearPrecio(parcial);
    }
}
