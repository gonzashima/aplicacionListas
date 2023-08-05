package Modelo.Productos;

/**
 * Representacion de un producto generico
 * */
public abstract class Producto {
    //Id unico del programa
    protected int id;

    //Codigo unico de la LISTA
    protected final int codigo;

    protected final String nombre;
    protected int precio;
    protected int costo;
    protected int porcentaje;
    protected int costoDescontado;

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

    public abstract void calcularPrecio();

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
        return id + " " + codigo + " " + nombre + " " + costo + " " + precio;
    }

    public String stringCartel() {
        return codigo + " " + nombre + "\n"
                + codigoCasa() + "-" + costo + "\n";
    }
}
