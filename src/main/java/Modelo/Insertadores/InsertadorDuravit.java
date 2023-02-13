package Modelo.Insertadores;

import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public class InsertadorDuravit extends Insertador{
    private final String DURAVIT = "duravit";

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        estado = determinarEstadoTabla(DURAVIT);
        estado.insertarABaseDeDatos(datos.get(DURAVIT), DURAVIT);
    }
}
