package Modelo.Insertadores;

import Modelo.Productos.Producto;
import Modelo.Utils.Constantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertadorMafersa extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ArrayList<String> nombresListas = Constantes.getNombresMafersa();

        for (String nombre : nombresListas) {
            estado = determinarEstadoTabla(nombre);
            HashMap<Integer, Producto> productos = datos.get(nombre);
            this.estado.insertarABaseDeDatos(productos, nombre);
        }
    }
}
