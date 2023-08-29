package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsertadorRigolleau extends Insertador{

    @Override
    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        int codigo = ConstantesNumericas.codigoLista(ConstantesStrings.RIGOLLEAU);

        HashMap<Integer, Producto> productos = datos.get(codigo);
        HashMap<Integer, HashMap<Integer, Producto>> resultado = separarProductos(productos);

        HashMap<Integer, Producto> productosNuevos = resultado.get(CODIGO_NUEVOS);
        HashMap<Integer, Producto> productosActualizar = resultado.get(CODIGO_ACTUALIZAR);

        List<Producto> paraActualizar = new ArrayList<>(productosActualizar.values());

        ConectorDB.guardarCambios(paraActualizar);
        ConectorDB.insertarProcuctos(productosNuevos, codigo);
    }
}
