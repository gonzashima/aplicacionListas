package controladores;

/**
 * Este record es necesario para la biblioteca de JavaFX
 * */

public record ModeloTabla(String codigo, String nombre, int costo, int precio) {

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
