package modelo;

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
        int parcial = costo * 2;
        this.precio = redondearPrecio(parcial);
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

    private int redondearPrecio(int precio){
        int resultado = precio % 10;
        switch (resultado) {
            case 1, 2, 3, 4 -> precio = precio - resultado;
            case 5, 6, 7, 8, 9 -> precio = precio + (10 - resultado);
            default -> {}
        }
        return precio;
    }

}
