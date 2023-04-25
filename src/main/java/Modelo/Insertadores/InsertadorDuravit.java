package Modelo.Insertadores;

import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public class InsertadorDuravit extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        estado = determinarEstadoTabla(ConstantesStrings.DURAVIT);
        estado.insertarABaseDeDatos(datos.get(ConstantesStrings.DURAVIT), ConstantesStrings.DURAVIT);
    }
}
