package modelo;

public class Producto {
    private final String nombre;
    private final int codigo;
    private double precio;
    private final double costo;

    public Producto(String nombre, int codigo, double costo){
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
    }

    public void calcularPrecio(){
        this.precio = costo * 2;
    }

    public double precio(){
        return this.precio;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public double getCosto(){
        return costo;
    }

}
