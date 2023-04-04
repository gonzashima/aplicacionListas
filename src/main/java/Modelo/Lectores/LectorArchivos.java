package Modelo.Lectores;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface LectorArchivos {

    /**
     * Lee el archivo pdf y parsea a productos, con sus respectivos nombres, codigos y costos
     */
    List<String> leerArchivo(File archivo) throws IOException;

}
