package Modelo.Insertadores;

import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public abstract class Insertador {
    /**
     * Toma los productos y los inserta o los actualiza en la DB, segun corresponda
     * */
    public abstract void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException;
}
