package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

public class ProductoRigolleau extends Producto{

    public ProductoRigolleau(String nombre, int codigo, int costo) {
        super(nombre, codigo, costo);
        costoDescontado = costo;
    }

    public ProductoRigolleau(int codigo, String nombre, int costo, int precio, int porcentaje) {
        super(codigo, nombre, costo, precio, porcentaje);
        costoDescontado = costo;
    }

    @Override
    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costo + (costo * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    @Override
    public int codigoCasa() {
        return 0;
    }
}
