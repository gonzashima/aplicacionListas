package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

/**
 * Representacion de un producto generico
 * */
public class Producto {
    private final String nombre;
    private final int codigo;
    private int precio;
    private int costo;
    private int porcentaje;

    public Producto(String nombre, int codigo, int costo){
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.porcentaje = 100;
    }

    public Producto (int codigo, String nombre, int costo, int precio, int porcentaje){
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.precio = precio;
        this.porcentaje = porcentaje;
    }

    public void calcularPrecio(){
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costo + (costo * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    public int getPrecio(){
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

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }


}
