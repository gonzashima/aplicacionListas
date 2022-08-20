package Modelo.Lectores;

import Modelo.Productos.Producto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface LectorArchivos {

    /**
     * Lee el archivo pdf y parsea a productos, con sus respectivos nombres, codigos y costos
     * */
    void leerArchivo(File archivo, ArrayList<Producto> listaProductos) throws IOException;

}
