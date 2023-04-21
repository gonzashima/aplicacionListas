package Modelo.Productos;

/**
 * Representacion de un producto generico
 * */
public abstract class Producto {
    protected final String nombre;
    protected final int codigo;
    protected int precio;
    protected int costo;
    protected int porcentaje;
    protected int costoDescontado;

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

    public abstract void calcularPrecio();

    public abstract int codigoCasa();

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

    public int getCostoDescontado() {
        return costoDescontado;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return codigo + " " + nombre + " " + costo + " " + precio;
    }
}
