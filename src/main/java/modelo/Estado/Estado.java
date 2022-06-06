package modelo.Estado;

import modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Representa el estado de la tabla en la base de datos cuando se hace la conexion. La misma puede estar vacia o no.
 * */
public interface Estado {
    void insertarABaseDeDatos(ArrayList<Producto> productos) throws SQLException;
}
