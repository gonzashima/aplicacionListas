package modelo.Estado;

import modelo.Producto;

import java.util.ArrayList;

public interface Estado {
    void insertarABaseDeDatos(ArrayList<Producto> productos);
}
