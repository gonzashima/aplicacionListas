package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

public class ProductoDuravit extends Producto{
    public ProductoDuravit(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
    }

    public ProductoDuravit(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
    }

    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costo + (costo * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }
}
