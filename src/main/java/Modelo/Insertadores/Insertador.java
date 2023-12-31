package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Insertador {
    /**
     * Toma los productos y los inserta o los actualiza en la DB, segun corresponda
     * */
    protected static int CODIGO_NUEVOS = 0;
    protected static int CODIGO_ACTUALIZAR = 1;

    private final String nombreLista;

    public Insertador(String nombreLista) {
        this.nombreLista = nombreLista;
    }

    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        int codigo = ConstantesNumericas.codigoLista(nombreLista);

        HashMap<Integer, Producto> productos = datos.get(codigo);
        HashMap<Integer, HashMap<Integer, Producto>> resultado = separarProductos(productos);

        HashMap<Integer, Producto> productosNuevos = resultado.get(CODIGO_NUEVOS);
        HashMap<Integer, Producto> productosActualizar = resultado.get(CODIGO_ACTUALIZAR);

        List<Producto> paraActualizar = new ArrayList<>(productosActualizar.values());

        ConectorDB.guardarCambios(paraActualizar);
        ConectorDB.insertarProcuctos(productosNuevos, codigo);
        datos.replace(codigo, ConectorDB.seleccionarProductos(nombreLista));
    }

    public static HashMap<Integer, HashMap<Integer, Producto>> separarProductos(HashMap<Integer, Producto> productos) {
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
