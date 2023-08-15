package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ParserLema implements Parser{

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        HashMap<Integer, Producto> mapaLema;
        mapaLema = datos.computeIfAbsent(ConstantesNumericas.codigoLista(ConstantesStrings.LEMA), k -> new HashMap<>());


    }
}
