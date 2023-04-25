package Modelo.Insertadores;

import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public class InsertadorRespontech extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        estado = determinarEstadoTabla(ConstantesStrings.RESPONTECH);
        estado.insertarABaseDeDatos(datos.get(ConstantesStrings.RESPONTECH), ConstantesStrings.RESPONTECH);
    }
}
