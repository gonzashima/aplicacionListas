package Modelo.Insertadores;

import Modelo.Constantes.StringsConstantes;
import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;

public class InsertadorDuravit extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        estado = determinarEstadoTabla(StringsConstantes.DURAVIT);
        estado.insertarABaseDeDatos(datos.get(StringsConstantes.DURAVIT), StringsConstantes.DURAVIT);
    }
}
