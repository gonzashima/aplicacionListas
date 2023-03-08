package Modelo.Insertadores;

import Modelo.Productos.Producto;
import Modelo.Utils.StringsConstantes;
import Modelo.Utils.UnificadorString;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertadorMafersa extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ArrayList<String> nombresListas = StringsConstantes.getNombresMafersa();

        for (String nombre : nombresListas) {
            nombre = UnificadorString.unirString(nombre);
            estado = determinarEstadoTabla(nombre);
            HashMap<Integer, Producto> productos = datos.get(nombre);
            this.estado.insertarABaseDeDatos(productos, nombre);
        }
    }
}
