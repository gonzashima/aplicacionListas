package modelo;

public class Producto {
    private String nombre;
    private float precio;
    private float costo;

    public Producto(String nombre, float costo){
        this.nombre = nombre;
        this.costo = costo;
    }

    public void calcularPrecio(){
        this.precio = costo * 2;
    }

    public float precio(){
        return this.precio;
    }

}
