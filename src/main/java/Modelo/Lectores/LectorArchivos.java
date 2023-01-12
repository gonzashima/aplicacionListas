package Modelo.Lectores;

import Modelo.Productos.Producto;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public interface LectorArchivos {

    /**
     * Lee el archivo pdf y parsea a productos, con sus respectivos nombres, codigos y costos
     */
    HashMap<Integer, Producto> leerArchivo(File archivo, HashMap<Integer, Producto> mapaProductos) throws IOException;

    /**
     * Devuelve el nombre de la tabla a la que se refiere este lector
     * */
    String nombreTabla();

}
