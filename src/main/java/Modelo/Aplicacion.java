package Modelo;

import Modelo.Estado.Estado;
import Modelo.Estado.Inexistente;
import Modelo.Estado.NoVacia;
import Modelo.Lectores.LectorArchivos;
import Modelo.Lectores.LectorDuravit;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Aplicacion {

    private static Aplicacion instance;

    private Estado estado;

    private HashMap<String, HashMap<Integer, Producto>> datos;

    private HashMap<String, ArrayList<Producto>> modificaciones;

    private Aplicacion(){
        datos = new HashMap<>();
        modificaciones = new HashMap<>();
        estado = null;
    }

    public static Aplicacion getInstance() {
        if (instance == null)
            instance = new Aplicacion();
        return instance;
    }

    /**
     * Lee el archivo e inserta la informacion a la base de datos
     * */
    public void leerArchivo(File archivo) throws IOException, SQLException {
        String nombre = archivo.getName();
        nombre = nombre.substring(0, nombre.lastIndexOf("."));
        nombre = nombre.toLowerCase();

        LectorArchivos lectorArchivos = determinarLector(nombre);
        nombre = lectorArchivos.nombreTabla();
        HashMap<Integer, Producto> mapaProductos = datos.get(nombre);

        mapaProductos = lectorArchivos.leerArchivo(archivo, mapaProductos);
        determinarEstadoTabla(nombre);
        estado.insertarABaseDeDatos(mapaProductos, nombre);
    }

    /**
     * Se fija si en la DB si la tabla con el nombre pasado por parametro esta vacia o tiene info
     * */
    private void determinarEstadoTabla(String nombreTabla) throws SQLException {
        Connection connection = ConectorDB.getConnection();

        if(connection!= null) {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, nombreTabla, null);
            if (!rs.next())
                estado = new Inexistente();
            else
                estado = new NoVacia();
        }
    }


    /**
     * Determina si la lista esta vacia
     * */
    public boolean estaVacia(String nombreLista) {
        nombreLista = nombreLista.toLowerCase();
        HashMap<Integer, Producto> productos = datos.get(nombreLista);

        if (productos == null)
            return true;

        return productos.isEmpty();
    }

    /**
     * Agrega una lista al mapa con todas las listas
     * */
    public void agregarListaDeProductos(String nombreLista, HashMap<Integer, Producto> mapaProductos) {
        datos.put(nombreLista, mapaProductos);
    }

    /**
     * Al producto que se le cambio el porcentaje, se lo agrega al map de modificaciones en la respectiva lista. El producto solo puede estar
     * una vez en dicha lista. Solo se guarda la ultima modificacion
     * */
    public void agregarModificacion(String nombreLista, Producto producto) {
        ArrayList<Producto> lista = modificaciones.get(nombreLista);

        if (lista == null) {
            lista = new ArrayList<>();
            lista.add(producto);
            modificaciones.put(nombreLista, lista);
        }
        else if (lista.contains(producto)) {
            int indice = lista.indexOf(producto);
            lista.set(indice, producto);
        }
        else {
            lista.add(producto);
        }
    }
    /**
     * Guarda todas las modificaciones a la base de datos
     * */
    public int guardarModificaciones() throws SQLException {
        Connection connection = ConectorDB.getConnection();
        int cambiosTotales = 0;

        if (connection != null) {
            for (String nombreLista : modificaciones.keySet()) {
                ArrayList<Producto> productos = modificaciones.get(nombreLista);
                int parcial = ConectorDB.guardarCambios(productos, nombreLista);
                cambiosTotales += parcial;
            }
        }
        return cambiosTotales;
    }

    /**
     * Busca el producto buscado en la lista elegida
     * */
    public ArrayList<Producto> buscarProducto(String clave, String buscado) {
        clave = clave.toLowerCase();
        buscado = buscado.toUpperCase();

        HashMap<Integer, Producto> productos = datos.get(clave);
        ArrayList<Producto> filtrados = new ArrayList<>();

        String finalBuscado = buscado;

        productos.forEach((key, value) -> {
            if (value.getNombre().contains(finalBuscado)) {
                filtrados.add(value);
            }
        });
        return filtrados;
    }

    public HashMap<Integer, Producto> obtenerLista(String nombreTabla) {
        return datos.get(nombreTabla);
    }


    /**
     * Determina el lector a instanciar segun el nombre del archivo
     * */
    private LectorArchivos determinarLector(String nombre) {
        LectorArchivos lector = null;

        if (nombre.contains("duravit"))
            lector = new LectorDuravit();

        return lector;
    }
}
