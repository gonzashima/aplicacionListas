package Modelo.Utils;

import Controladores.Alertas.AlertaCambios;
import Controladores.Alertas.Alerta;
import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
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

    public static HashMap<Integer, Producto> seleccionarProductos(String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        String query = "SELECT * from productos WHERE lista_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, ConstantesNumericas.codigoLista(nombreTabla));

        ResultSet rs = statement.executeQuery();
        HashMap<Integer, Producto> productos = new HashMap<>();
        List<String> nombresMafersa = ConstantesStrings.getNombresMafersa();
        nombresMafersa = nombresMafersa.stream().map(UnificadorString::unirString).collect(Collectors.toList());

        while (rs.next()) {
            Producto producto = null;
            int id = rs.getInt("id");
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            int costo = rs.getInt("costo");
            int precio = rs.getInt("precio");
            int porcentaje = rs.getInt("porcentaje");

            if (nombreTabla.equals(ConstantesStrings.DURAVIT))
                producto = new ProductoDuravit(id, codigo, nombre, costo, precio, porcentaje);

            else if (nombresMafersa.stream().anyMatch(nombreTabla :: contains)) {
                if (ConstantesStrings.getDistintosLumilagro().stream().anyMatch(nombre::contains))
                    producto = new ProductoLumilagro(id, codigo, nombre, costo, precio, porcentaje);
                else
                    producto = new ProductoMafersa(id, codigo, nombre, costo, precio, porcentaje);
            }
            else if (nombreTabla.equals(ConstantesStrings.RESPONTECH))
                producto = new ProductoRespontech(id, codigo, nombre, costo, precio, porcentaje);
            else if (nombreTabla.equals(ConstantesStrings.RIGOLLEAU))
                producto = new ProductoRigolleau(id, codigo, nombre, costo, precio, porcentaje);

            productos.put(codigo, producto);
        }
        return productos;
    }


    public static void insertarProcuctos(HashMap<Integer, Producto> productos, int codigoLista) throws SQLException {
        String query = "INSERT INTO productos (id, codigo, nombre, costo, precio, porcentaje, lista_id) " +
                "VALUES (NULL,?,?,?,?,?,?)";

        PreparedStatement insertarTemp = connection.prepareStatement(query);

        productos.forEach((key, value) -> {
            try {;
                insertarTemp.setInt(1, value.getCodigo());
                insertarTemp.setString(2, value.getNombre());
                insertarTemp.setInt(3, value.getCosto());
                insertarTemp.setInt(4, value.getPrecio());
                insertarTemp.setInt(5, value.getPorcentaje());
                insertarTemp.setInt(6, codigoLista);
                insertarTemp.addBatch();
            } catch (SQLException e) {
                Alerta alertaDB = new AlertaCambios();
                alertaDB.display();
            }
        });
        insertarTemp.executeBatch();
    }

    public static int guardarCambios(List<Producto> productos) throws SQLException {
        String query = "UPDATE productos SET nombre=?, precio=?, costo=?, porcentaje=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(query);

        for (Producto producto: productos) {
            statement.setString(1, producto.getNombre());
            statement.setInt(2, producto.getPrecio());
            statement.setInt(3, producto.getCosto());
            statement.setInt(4, producto.getPorcentaje());
            statement.setInt(5, producto.getId());
            statement.addBatch();
        }

        return statement.executeBatch().length;
    }

    public static void insertarLista(String nombre) throws SQLException {
        int codigo = ConstantesNumericas.codigoLista(UnificadorString.unirString(nombre));
        nombre = nombre.toUpperCase();

        String query = "INSERT IGNORE INTO listas (id, nombre, proveedor) VALUES(?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, codigo);
        statement.setString(2, nombre);
        statement.setString(3, nombre);
        statement.executeUpdate();
    }

    public static void modificarID() throws SQLException {
        String query = "ALTER TABLE productos MODIFY COLUMN id INT AUTO_INCREMENT";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
    }
}
