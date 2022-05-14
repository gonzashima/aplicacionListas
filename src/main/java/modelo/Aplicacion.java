package modelo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Aplicacion {

    private ArrayList<Producto> productos;

    public Aplicacion(){
        productos = new ArrayList<>();
    }

    public void leerArchivo(File archivo) throws IOException {
        LectorArchivos lectorArchivos = new LectorArchivos();
        lectorArchivos.leerArchivo(archivo, productos);

        System.out.println(productos.size());
    }
}
