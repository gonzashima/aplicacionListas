package Modelo.Insertadores;

import Modelo.Estado.Estado;
import Modelo.Estado.Inexistente;
import Modelo.Estado.NoVacia;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.UnificadorString;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class Insertador {
    protected Estado estado;

    protected Estado determinarEstadoTabla(String nombreTabla) throws SQLException {
        Connection connection = ConectorDB.getConnection();
        nombreTabla = UnificadorString.unirString(nombreTabla);
        if(connection!= null) {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, nombreTabla, null);
            if (!rs.next())
                return new Inexistente();
            return new NoVacia();
        }
        return null;
    }


    public abstract void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException;
}
