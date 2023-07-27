package Modelo.Insertadores;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsertadorDuravit extends Insertador{
    @Override
    public void insertarABaseDeDatos(HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        int codigo = ConstantesNumericas.codigoLista(ConstantesStrings.DURAVIT);
        HashMap<Integer, Producto> productos = datos.get(codigo);

        HashMap<Integer, Producto> productosNuevos = new HashMap<>();
        HashMap<Integer, Producto> productosActualizar = new HashMap<>();

        for (int key : productos.keySet()) {
            Producto producto = productos.get(key);
            if (producto.getId() == ConstantesNumericas.ID_NULO)
                productosNuevos.put(producto.getCodigo(), producto);
            else
                productosActualizar.put(producto.getCodigo(), producto);
        }

        List<Producto> paraActualizar = new ArrayList<>(productosActualizar.values());

        ConectorDB.guardarCambios(paraActualizar);
        ConectorDB.insertarProcuctos(productosNuevos, codigo);
    }
}
