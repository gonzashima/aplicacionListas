package Modelo;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Insertadores.*;
import Modelo.Lectores.*;
import Modelo.Parsers.*;
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

    private final HashMap<Integer, HashMap<Integer, Producto>> datos;

    private final HashMap<Integer, List<Producto>> modificaciones;

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

        HashMap<Integer, Producto> productos = datos.get(ConstantesNumericas.codigoLista(nombreLista));

        if (productos == null)
            return true;

        return productos.isEmpty();
    }

    /**
     * Agrega una lista al mapa con todas las listas
     * */
    public void agregarListaDeProductos(String nombreLista, HashMap<Integer, Producto> mapaProductos) {
        datos.put(ConstantesNumericas.codigoLista(nombreLista), mapaProductos);
    }

    /**
     * Al producto que se le cambió el porcentaje, se lo agrega al map de modificaciones en la respectiva lista. El producto solo puede estar
     * una vez en dicha lista. Solo se guarda la última modificacion
     * */
    public void agregarModificacion(String nombreLista, Producto producto) {
        List<Producto> lista = modificaciones.get(ConstantesNumericas.codigoLista(nombreLista));

        if (lista == null) {
            lista = new ArrayList<>();
            lista.add(producto);
            modificaciones.put(ConstantesNumericas.codigoLista(nombreLista), lista);
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
            for (int codigoLista : modificaciones.keySet()) {
                List<Producto> productos = modificaciones.get(codigoLista);
                int parcial = ConectorDB.guardarCambios(productos);
                cambiosTotales += parcial;
                productos.clear();
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

        HashMap<Integer, Producto> productos = datos.get(ConstantesNumericas.codigoLista(clave));
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
        return datos.get(ConstantesNumericas.codigoLista(nombreTabla));
    }


    /**
     * Determina las utilidades a instanciar segun el nombre del archivo
     * */
    private Resultado determinarUtilidades(String nombre) {
        Resultado resultado = null;

        if (nombre.contains(ConstantesStrings.DURAVIT))
            resultado = new Resultado(new LectorDuravit(), new ParserDuravit(), new Insertador(ConstantesStrings.DURAVIT));

        else if (nombre.contains(ConstantesStrings.MAFERSA))
            resultado = new Resultado(new LectorMafersa(), new ParserMafersa(), new InsertadorMafersa(ConstantesStrings.MAFERSA));

        else if (nombre.contains(ConstantesStrings.RESPONTECH))
            resultado = new Resultado(new LectorRespontech(), new ParserRespontech(), new Insertador(ConstantesStrings.RESPONTECH));

        else if (nombre.contains(ConstantesStrings.RIGOLLEAU))
            resultado = new Resultado(new LectorRigolleau(), new ParserRigolleau(), new Insertador(ConstantesStrings.RIGOLLEAU));

        else if (nombre.contains("casa lema") || nombre.contains("casalema"))
            resultado = new Resultado(new LectorLema(), new ParserLema(), new Insertador(ConstantesStrings.LEMA));

        else if (nombre.contains(ConstantesStrings.RODECA))
            resultado = new Resultado(new LectorRodeca(), new ParserRodeca(), new Insertador(ConstantesStrings.RODECA));

        else if (nombre.contains(ConstantesStrings.DIF_PLAST))
            resultado = new Resultado(new LectorDifPlast(), new ParserDifPlast(), new Insertador(ConstantesStrings.DIF_PLAST));

        return resultado;
    }
}
