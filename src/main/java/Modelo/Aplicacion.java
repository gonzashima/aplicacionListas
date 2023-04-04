package Modelo;

import Modelo.Insertadores.Insertador;
import Modelo.Insertadores.InsertadorDuravit;
import Modelo.Insertadores.InsertadorMafersa;
import Modelo.Lectores.LectorArchivos;
import Modelo.Lectores.LectorDuravit;
import Modelo.Lectores.LectorMafersa;
import Modelo.Parsers.Parser;
import Modelo.Parsers.ParserDuravit;
import Modelo.Parsers.ParserMafersa;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.Resultado;
import Modelo.Utils.UnificadorString;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Aplicacion {

    private static Aplicacion instance;

    private final HashMap<String, HashMap<Integer, Producto>> datos;

    private final HashMap<String, List<Producto>> modificaciones;

    private Aplicacion(){
        datos = new HashMap<>();
        modificaciones = new HashMap<>();
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

        Resultado utilidades = determinarUtilidades(nombre);
        LectorArchivos lector = utilidades.lector();
        Parser parser = utilidades.parser();
        Insertador insertador = utilidades.insertador();

        List<String> lineas = lector.leerArchivo(archivo);
        parser.parsearAProducto(lineas, datos);
        insertador.insertarABaseDeDatos(datos);
    }
    /**
     * Determina si la lista esta vacia
     * */
    public boolean estaVacia(String nombreLista) {
        nombreLista = nombreLista.toLowerCase();
        nombreLista = UnificadorString.unirString(nombreLista);

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
        List<Producto> lista = modificaciones.get(nombreLista);

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
                List<Producto> productos = modificaciones.get(nombreLista);
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
        clave = UnificadorString.unirString(clave);
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
        nombreTabla = nombreTabla.toLowerCase();
        nombreTabla = UnificadorString.unirString(nombreTabla);
        return datos.get(nombreTabla);
    }


    /**
     * Determina el lector a instanciar segun el nombre del archivo
     * */
    private Resultado determinarUtilidades(String nombre) {
        Resultado resultado = null;

        if (nombre.contains("duravit") || nombre.contains("DURAVIT"))
            resultado = new Resultado(new LectorDuravit(), new ParserDuravit(), new InsertadorDuravit());
        else if (nombre.contains("mafersa") || nombre.contains("MAFERSA"))
            resultado = new Resultado(new LectorMafersa(), new ParserMafersa(), new InsertadorMafersa());

        return resultado;
    }
}
