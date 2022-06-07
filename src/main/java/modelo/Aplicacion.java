package modelo;

import modelo.Estado.Estado;
import modelo.Estado.NoVacia;
import modelo.Estado.Vacia;
import modelo.Productos.Producto;
import modelo.Utils.ConectorDB;
import modelo.Utils.LectorArchivos;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Aplicacion {

    private static Aplicacion instance;

    private final ArrayList<Producto> productos;

    private Estado estado;

    private HashMap<String, ArrayList<Producto>> datos;

    //TODO tener todos los resultset guardados aca cuando se muestran las tablas por primera vez, para no tener que hacer muchas queries a la DB

    //TODO inicializar la conexion a la base de datos al principio, que es la que mas tiempo tarda

    private Aplicacion(){
        productos = new ArrayList<>();
        datos = new HashMap<>();
        estado = null;
    }

    public static Aplicacion getInstance() {
        if (instance == null)
            instance = new Aplicacion();
        return instance;
    }

    public void leerArchivo(File archivo, String nombre) throws IOException, SQLException {
        LectorArchivos lectorArchivos = new LectorArchivos();
        lectorArchivos.leerArchivo(archivo, productos);
        determinarEstadoTabla(nombre);
        estado.insertarABaseDeDatos(productos);
    }

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

    public void agregarListaDeProductos(String nombreTabla, ArrayList<Producto> lista) {
        datos.put(nombreTabla, lista);
    }

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

    public boolean estaVacia(String nombreTabla) {
        nombreTabla = nombreTabla.toLowerCase();
        ArrayList<Producto> productos = datos.get(nombreTabla);

        if (productos == null)
            return true;

        return productos.isEmpty();
    }

    public ArrayList<Producto> obtenerLista(String nombreTabla) {
        return datos.get(nombreTabla);
    }
}
