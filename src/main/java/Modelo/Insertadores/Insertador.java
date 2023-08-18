package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public abstract class Insertador {
    /**
     * Toma los productos y los inserta o los actualiza en la DB, segun corresponda
     * */
    protected static int CODIGO_NUEVOS = 0;
    protected static int CODIGO_ACTUALIZAR = 1;

    public abstract void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException;

    public HashMap<Integer, HashMap<Integer, Producto>> separarProductos(HashMap<Integer, Producto> productos) {
        HashMap<Integer, Producto> productosNuevos = new HashMap<>();
        HashMap<Integer, Producto> productosActualizar = new HashMap<>();

        for (int key : productos.keySet()) {
            Producto producto = productos.get(key);
            if (producto.getId() == ConstantesNumericas.ID_NULO)
                productosNuevos.put(producto.getCodigo(), producto);
            else
                productosActualizar.put(producto.getCodigo(), producto);
        }
        HashMap<Integer, HashMap<Integer, Producto>> resultado = new HashMap<>();
        resultado.put(CODIGO_NUEVOS, productosNuevos);
        resultado.put(CODIGO_ACTUALIZAR, productosActualizar);

        return resultado;
    }
}
