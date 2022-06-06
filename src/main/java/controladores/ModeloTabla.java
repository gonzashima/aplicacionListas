package controladores;

public class ModeloTabla {

    private final String codigo;
    private final String nombre;
    private final int costo;
    private final int precio;

    public ModeloTabla(String codigo, String nombre, int costo, int precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCosto() {
        return costo;
    }

    public int getPrecio() {
        return precio;
    }
}
