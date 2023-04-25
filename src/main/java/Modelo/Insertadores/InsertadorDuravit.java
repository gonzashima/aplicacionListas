package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.HashMap;

public class InsertadorDuravit extends Insertador{
    @Override
    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        int codigo = ConstantesNumericas.codigoLista(ConstantesStrings.DURAVIT);
        ConectorDB.insertarProcuctos(datos.get(codigo), codigo);
    }
}
