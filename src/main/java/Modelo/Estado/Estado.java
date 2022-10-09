package Modelo.Estado;

import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;


/**
 * Representa el estado de la tabla en la base de datos cuando se hace la conexion. La misma puede estar vacia o no.
 * */
public interface Estado {

    void insertarABaseDeDatos(HashMap<Integer, Producto> productos, String nombreTabla) throws SQLException;
}
