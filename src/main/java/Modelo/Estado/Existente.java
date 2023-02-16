package Modelo.Estado;

import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class Existente implements Estado {

    @Override
    public void insertarABaseDeDatos(HashMap<Integer, Producto> productos, String nombreTabla) throws SQLException {
        Connection connection = ConectorDB.getConnection();
        if (connection != null)
            ConectorDB.actualizarProcuctos(productos, nombreTabla);
    }
}
