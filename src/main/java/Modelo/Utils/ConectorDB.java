package Modelo.Utils;

import Controladores.Alertas.AlertaCambios;
import Controladores.Alertas.Alerta;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDuravit;
import Modelo.Productos.ProductoLumilagro;
import Modelo.Productos.ProductoMafersa;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Se utiliza para obtener siempre la misma conexion a la base de datos y no perder tiempo intentando crear una nueva conexion cada vez que se necesita
 * */
public class ConectorDB {

    private static Connection connection = null;

    public static Connection getConnection(){
        try{
            if(connection == null) {
                String url = "jdbc:mysql://localhost:3306/productos";
                String user = "root";
                String password = "2520804";
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e){
            System.out.println("No se pudo conectar a la base de datos");
        }
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null)
            connection.close();
    }

    public static HashMap<Integer, Producto> ejecutarQuery(String query, String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, Producto> productos = new HashMap<>();
        ArrayList<String> nombresMafersa = Constantes.getNombresMafersa();
        nombresMafersa = (ArrayList<String>) nombresMafersa.stream().map(UnificadorString::unirString).collect(Collectors.toList());

        while (rs.next()) {
            Producto producto = null;
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            int costo = rs.getInt("costo");
            int precio = rs.getInt("precio");
            int porcentaje = rs.getInt("porcentaje");

            if (nombreTabla.equals(Constantes.DURAVIT))
                producto = new ProductoDuravit(codigo, nombre, costo, precio, porcentaje);
            else if (nombresMafersa.stream().anyMatch(nombreTabla :: contains))
                if (Constantes.getDistintosLumilagro().stream().anyMatch(nombre :: contains))
                    producto = new ProductoLumilagro(codigo, nombre, costo, precio, porcentaje);
                else
                    producto = new ProductoMafersa(codigo, nombre, costo, precio, porcentaje);

            productos.put(codigo, producto);
        }
        return productos;
    }

    public static void crearTabla(String nombre) throws SQLException {
        nombre = UnificadorString.unirString(nombre);
        String query = "CREATE TABLE " + nombre + " (codigo int, " +
                "nombre varchar(50), costo int, precio int, porcentaje int, PRIMARY KEY (codigo))";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public static void insertarProductos(HashMap<Integer, Producto> mapaProductos, String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        String query = "INSERT INTO " + nombreTabla + " (codigo, nombre, costo, precio, porcentaje) VALUES(?,?,?,?,?) ";
        PreparedStatement statement = connection.prepareStatement(query);

        mapaProductos.forEach((key, value) -> {
            try {
                statement.setInt(1, value.getCodigo());
                statement.setString(2, value.getNombre());
                statement.setInt(3, value.getCosto());
                statement.setInt(4, value.getPrecio());
                statement.setInt(5, value.getPorcentaje());
                statement.addBatch();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        statement.executeBatch();
    }

    public static void actualizarProcuctos(HashMap<Integer, Producto> productos, String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        String query = "INSERT INTO " + nombreTabla + " (codigo, nombre, costo, precio, porcentaje) VALUES(?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE nombre=?, costo=?, precio=?, porcentaje=?";
        PreparedStatement statement = connection.prepareStatement(query);

        productos.forEach((key, value) -> {
            try {
                statement.setInt(1, value.getCodigo());
                statement.setString(2, value.getNombre());
                statement.setInt(3, value.getCosto());
                statement.setInt(4, value.getPrecio());
                statement.setInt(5, value.getPorcentaje());
                statement.setString(6, value.getNombre());
                statement.setInt(7, value.getCosto());
                statement.setInt(8, value.getPrecio());
                statement.setInt(9, value.getPorcentaje());
                statement.addBatch();
            } catch (SQLException e) {
                Alerta alertaDB = new AlertaCambios();
                alertaDB.display();
            }
        });
        statement.executeBatch();
    }

    public static int guardarCambios(ArrayList<Producto> productos, String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        String query = "UPDATE " + nombreTabla + " SET precio=?, porcentaje=? WHERE codigo=?";
        PreparedStatement statement = connection.prepareStatement(query);

        for (Producto producto: productos) {
            statement.setInt(1, producto.getPrecio());
            statement.setInt(2, producto.getPorcentaje());
            statement.setInt(3, producto.getCodigo());
            statement.addBatch();
        }

        return statement.executeBatch().length;
    }

    /**
     * Determina si una tabla con cierto nombre existe o no
     * */
    public static boolean existeTabla(String nombre) throws SQLException {
        nombre = UnificadorString.unirString(nombre);
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, nombre, null);

        return rs.next();
    }
}
