package Modelo.Lectores;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface LectorArchivos {

    /**
     * Lee el archivo pdf y parsea a productos, con sus respectivos nombres, codigos y costos
     */
    ArrayList<String> leerArchivo(File archivo) throws IOException;

    /**
     * Devuelve el nombre de la tabla a la que se refiere este lector
     * */
    String nombreTabla();

}
