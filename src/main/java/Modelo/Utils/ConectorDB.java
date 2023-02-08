package Modelo.Utils;

import Controladores.Alertas.AlertaCambios;
import Controladores.Alertas.AlertaDB;
import Modelo.Productos.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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

    public static ResultSet ejecutarQuery(String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        return statement.executeQuery();
    }

    public static void crearTabla(String nombre) throws SQLException {
        String query = "CREATE TABLE " + nombre + " (codigo int, " +
                "nombre varchar(50), costo int, precio int, porcentaje int, PRIMARY KEY (codigo))";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
    }

    public static void insertarProductos(HashMap<Integer, Producto> mapaProductos, String nombreTabla) throws SQLException {
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
        String query = "UPDATE " + nombreTabla + " SET costo=?, precio=?, porcentaje=? WHERE codigo=?";
        PreparedStatement statement = connection.prepareStatement(query);

        productos.forEach((key, value) -> {
            try {
                statement.setInt(1, value.getCosto());
                statement.setInt(2, value.getPrecio());
                statement.setInt(3, value.getPorcentaje());
                statement.setInt(4, value.getCodigo());
                statement.addBatch();
            } catch (SQLException e) {
                AlertaDB alertaDB = new AlertaCambios();
                alertaDB.display();
            }
        });
        statement.executeBatch();
    }

    public static int guardarCambios(ArrayList<Producto> productos, String nombreTabla) throws SQLException {
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
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, nombre, null);

        return rs.next();
    }
}
