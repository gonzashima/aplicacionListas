package modelo;

import modelo.Utils.ManejadorPrecios;

public class Producto {
    private final String nombre;
    private final int codigo;
    private int precio;
    private final int costo;

    public Producto(String nombre, int codigo, int costo){
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
    }

    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costo * 2;
        this.precio = manejador.redondearPrecio(parcial);
    }

    public int precio(){
        return this.precio;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public int getCosto(){
        return costo;
    }


}
