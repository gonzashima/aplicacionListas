package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.UnificadorString;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertadorMafersa extends Insertador{

    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        ArrayList<String> nombresListas = ConstantesStrings.getNombresMafersa();

        for (String nombre : nombresListas) {
            nombre = UnificadorString.unirString(nombre);
            int codigoLista = ConstantesNumericas.codigoLista(nombre);
            HashMap<Integer, Producto> productos = datos.get(codigoLista);
            ConectorDB.insertarProcuctos(productos, codigoLista);
        }
    }
}
