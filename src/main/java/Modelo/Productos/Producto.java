package Modelo.Productos;

import Modelo.Utils.ManejadorPrecios;

/**
 * Representacion de un producto generico
 * */
public abstract class Producto {
    //Id unico del programa
    protected int id;

    //Codigo unico del producto en su LISTA
    protected final int codigo;

    protected final String nombre;
    protected int precio;
    protected int costo;
    protected int porcentaje;
    protected int costoParcial;

    public Producto(String nombre, int codigo, int costo){
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.porcentaje = 100;
        this.id = -1;
    }

    public Producto (int id, int codigo, String nombre, int costo, int precio, int porcentaje){
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.precio = precio;
        this.porcentaje = porcentaje;
    }

    public void calcularPrecio() {
        ManejadorPrecios manejador = new ManejadorPrecios();
        int parcial = costoParcial + (costoParcial * porcentaje) / 100;
        this.precio = manejador.redondearPrecio(parcial);
    }

    public abstract int codigoCasa();

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
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

    public int getCostoParcial() {
        return costoParcial;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public abstract void actualizarCostoParcial();

    @Override
    public String toString() {
        return id + " " + codigo + " " + nombre + " " + costo + " " + precio;
    }

    public String stringCartel() {
        return codigo + " " + nombre + "\n"
                + codigoCasa() + "-" + costo + "\n";
    }
}
