package Modelo.Parsers;

import Modelo.Productos.Producto;

import java.util.HashMap;

public interface Parser {
    void parsearAProducto(String texto, HashMap<String, HashMap<Integer, Producto>> datos);
}
