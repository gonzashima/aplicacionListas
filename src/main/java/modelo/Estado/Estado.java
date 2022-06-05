package modelo.Estado;

import modelo.Producto;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Estado {
    void insertarABaseDeDatos(ArrayList<Producto> productos) throws SQLException;
}
