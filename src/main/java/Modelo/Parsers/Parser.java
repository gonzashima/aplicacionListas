package Modelo.Parsers;

import Modelo.Productos.Producto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Parser {

    /**
     * Se encarga de pasar texto a productos y de cargarlos al mapa de datos
     * */
    void parsearAProducto(List<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException;
}
