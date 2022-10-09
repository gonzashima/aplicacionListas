package Modelo;

import Modelo.Estado.Estado;
import Modelo.Estado.NoVacia;
import Modelo.Estado.Vacia;
import Modelo.Lectores.LectorArchivos;
import Modelo.Lectores.LectorDuravit;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Aplicacion {

    private static Aplicacion instance;

    private Estado estado;

    private HashMap<String, HashMap<Integer, Producto>> datos;

    private HashMap<String, HashMap<Producto>> modificaciones;

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
        ArrayList<Producto> listaProductos = datos.get(nombre);

        listaProductos = lectorArchivos.leerArchivo(archivo, listaProductos);
        determinarEstadoTabla(nombre);
        estado.insertarABaseDeDatos(listaProductos);
    }

    /**
     * Se fija si en la DB si la tabla con el nombre pasado por parametro esta vacia o tiene info
     * */
    private void determinarEstadoTabla(String nombreTabla) throws SQLException {
        Connection connection = ConectorDB.getConnection();

        if(connection != null) {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("select count(*) from " + nombreTabla);
            int cantidad = 0;
            while (resultSet.next())
                cantidad = resultSet.getInt(1);

            if (cantidad == 0)
                estado = new Vacia();
            else
                estado = new NoVacia();
        }
        else
            System.out.println("No se pudo conectar con la base de datos");
    }

    /**
     * Agrega una lista al mapa con todas las listas
     * */
    public void agregarListaDeProductos(String nombreLista, ArrayList<Producto> listaProductos) {
        datos.put(nombreLista, listaProductos);
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
                String query = "UPDATE " + nombreLista + " SET precio=?, porcentaje=? WHERE codigo=?";
                PreparedStatement statement = connection.prepareStatement(query);

                ArrayList<Producto> lista = modificaciones.get(nombreLista);

                for (Producto producto : lista) {
                    statement.setInt(1, producto.getPrecio());
                    statement.setInt(2, producto.getPorcentaje());
                    statement.setInt(3, producto.getCodigo());
                    statement.addBatch();
                }
                int totalParcial = statement.executeUpdate();
                cambiosTotales += totalParcial;
            }
        }
        return cambiosTotales;
    }

    /**
     * Busca el producto buscado en la lista elegida
     * */
    public ArrayList<Producto> buscarProducto(String key, String buscado) {
        key = key.toLowerCase();
        buscado = buscado.toUpperCase();
        ArrayList<Producto> productos = datos.get(key);
        ArrayList<Producto> filtrados = new ArrayList<>();

        for (Producto p : productos) {
            if (p.getNombre().contains(buscado)){
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    /**
     * Determina si la lista esta vacia
     * */
    public boolean estaVacia(String nombreLista) {
        nombreLista = nombreLista.toLowerCase();
        ArrayList<Producto> productos = datos.get(nombreLista);

        if (productos == null)
            return true;

        return productos.isEmpty();
    }

    public ArrayList<Producto> obtenerLista(String nombreTabla) {
        return datos.get(nombreTabla);
    }


    /**
     * Determina el lector a instanciar segun el nombre del archivo
     * */
    private LectorArchivos determinarLector(String nombre) {
        LectorArchivos lector = null;

        switch (nombre) {
            case "duravit":
                lector = new LectorDuravit();
                break;
        }

        return lector;
    }
}
