package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.UnificadorString;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsertadorMafersa extends Insertador{

    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        ArrayList<String> nombresListas = ConstantesStrings.getNombresMafersa();

        for (String nombre : nombresListas) {
            nombre = UnificadorString.unirString(nombre);
            int codigoLista = ConstantesNumericas.codigoLista(nombre);

            HashMap<Integer, Producto> productos = datos.get(codigoLista);
            HashMap<Integer, HashMap<Integer, Producto>> resultado = separarProductos(productos);

            HashMap<Integer, Producto> productosNuevos = resultado.get(CODIGO_NUEVOS);
            HashMap<Integer, Producto> productosActualizar = resultado.get(CODIGO_ACTUALIZAR);

            List<Producto> paraActualizar = new ArrayList<>(productosActualizar.values());

            ConectorDB.guardarCambios(paraActualizar);
            ConectorDB.insertarProcuctos(productosNuevos, codigoLista);
        }
    }
}
