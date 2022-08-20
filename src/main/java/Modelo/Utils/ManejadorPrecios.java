package Modelo.Utils;

public class ManejadorPrecios {

    /**
     * Redondea el precio a la decena mas cercana
     * */
    public int redondearPrecio(int precio){
        int resultado = precio % 10;
        switch (resultado) {
            case 1, 2, 3, 4 -> precio = precio - resultado;
            case 5, 6, 7, 8, 9 -> precio = precio + (10 - resultado);
            default -> {}
        }
        return precioDeVenta(precio);
    }

    /**
     * Redondea el precio a un precio conveniente para la venta
     * */
    private int precioDeVenta(int precio){
        if (precio >= 100){
            int resultado = precio % 100;
            switch (resultado) {
                case 10, 20 -> precio = precio - resultado;
                case 30, 40 -> precio = precio + (50 - resultado);
                default -> {}
            }
        }
        return precio;
    }
}
