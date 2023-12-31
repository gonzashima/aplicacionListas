package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ParserDifPlast implements Parser{

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaDifPlast;

        if (datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST)) == null) {
            mapaDifPlast = ConectorDB.seleccionarProductos(ConstantesStrings.DIF_PLAST);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST), mapaDifPlast);
        }
        mapaDifPlast = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST));

        for (String linea : texto) {

        }
    }
}
