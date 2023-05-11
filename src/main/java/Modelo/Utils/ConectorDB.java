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


    @Deprecated
    public static HashMap<Integer, Producto> ejecutarQuery(String query, String nombreTabla) throws SQLException {
        nombreTabla = UnificadorString.unirString(nombreTabla);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, Producto> productos = new HashMap<>();
        List<String> nombresMafersa = ConstantesStrings.getNombresMafersa();
        nombresMafersa = nombresMafersa.stream().map(UnificadorString::unirString).collect(Collectors.toList());

        while (rs.next()) {
            Producto producto = null;
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            int costo = rs.getInt("costo");
            int precio = rs.getInt("precio");
            int porcentaje = rs.getInt("porcentaje");

            if (nombreTabla.equals(ConstantesStrings.DURAVIT))
                producto = new ProductoDuravit(codigo, nombre, costo, precio, porcentaje);

            else if (nombresMafersa.stream().anyMatch(nombreTabla :: contains)) {
                if (ConstantesStrings.getDistintosLumilagro().stream().anyMatch(nombre::contains))
                    producto = new ProductoLumilagro(codigo, nombre, costo, precio, porcentaje);
                else
                    producto = new ProductoMafersa(codigo, nombre, costo, precio, porcentaje);
            }
            else if (nombreTabla.equals(ConstantesStrings.RESPONTECH))
                producto = new ProductoRespontech(codigo, nombre, costo, precio, porcentaje);
            productos.put(codigo, producto);
        }
        return productos;
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
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            int costo = rs.getInt("costo");
            int precio = rs.getInt("precio");
            int porcentaje = rs.getInt("porcentaje");

            if (nombreTabla.equals(ConstantesStrings.DURAVIT))
                producto = new ProductoDuravit(codigo, nombre, costo, precio, porcentaje);

            else if (nombresMafersa.stream().anyMatch(nombreTabla :: contains)) {
                if (ConstantesStrings.getDistintosLumilagro().stream().anyMatch(nombre::contains))
                    producto = new ProductoLumilagro(codigo, nombre, costo, precio, porcentaje);
                else
                    producto = new ProductoMafersa(codigo, nombre, costo, precio, porcentaje);
            }
            else if (nombreTabla.equals(ConstantesStrings.RESPONTECH))
                producto = new ProductoRespontech(codigo, nombre, costo, precio, porcentaje);
            productos.put(codigo, producto);
        }
        return productos;
    }


    public static void insertarProcuctos(HashMap<Integer, Producto> productos, int codigoLista) throws SQLException {
        String crearTabla = "CREATE TEMPORARY TABLE IF NOT EXISTS tmp_productos " +
                "SELECT id, codigo, nombre, costo, precio, porcentaje, lista_id " +
                "FROM productos " +
                "WHERE lista_id = ?";

        PreparedStatement statementCrear = connection.prepareStatement(crearTabla);
        statementCrear.setInt(1, codigoLista);
        statementCrear.executeUpdate();

        String setPrimaryKey = "ALTER TABLE tmp_productos ADD PRIMARY KEY(codigo);";
        PreparedStatement ps2 = connection.prepareStatement(setPrimaryKey);
        ps2.executeUpdate();

        String alterTableSql = "ALTER TABLE tmp_productos MODIFY COLUMN id INT NULL;";
        PreparedStatement stmtAlter = connection.prepareStatement(alterTableSql);
        stmtAlter.executeUpdate();

        String query = "INSERT INTO tmp_productos (id, codigo, nombre, costo, precio, porcentaje, lista_id) " +
                "VALUES (NULL,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE costo=VALUES(costo), nombre=VALUES(nombre), precio=VALUES(precio)";

        PreparedStatement insertarTemp = connection.prepareStatement(query);

        productos.forEach((key, value) -> {
            try {
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

        String alterTable = "ALTER TABLE tmp_productos DROP PRIMARY KEY, ADD PRIMARY KEY (id)";
        PreparedStatement psAlter = connection.prepareStatement(alterTable);
        psAlter.executeUpdate();

        String insertarEnOriginal = "INSERT INTO productos (id, codigo, nombre, costo, precio, porcentaje, lista_id) " +
                "SELECT id, codigo, nombre, costo, precio, porcentaje, lista_id " +
                "FROM tmp_productos t " +
                "ON DUPLICATE KEY UPDATE nombre=t.nombre, costo=t.costo, precio=t.precio";

        PreparedStatement reinsercion = connection.prepareStatement(insertarEnOriginal);
        reinsercion.executeUpdate();

        Statement drop = connection.createStatement();
        drop.executeUpdate("DROP TEMPORARY TABLE IF EXISTS tmp_productos");
    }

    public static int guardarCambios(List<Producto> productos, int codigoLista) throws SQLException {
        String query = "UPDATE productos SET precio=?, porcentaje=? WHERE codigo=? AND lista_id=?";
        PreparedStatement statement = connection.prepareStatement(query);

        for (Producto producto: productos) {
            statement.setInt(1, producto.getPrecio());
            statement.setInt(2, producto.getPorcentaje());
            statement.setInt(3, producto.getCodigo());
            statement.setInt(4, codigoLista);
            statement.addBatch();
        }

        return statement.executeBatch().length;
    }

    /**
     * Crea la tabla de productos
    * */
    public static void crearTablaProductos() throws SQLException {
        String query = "CREATE TABLE productos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "codigo VARCHAR(50)," +
                "nombre VARCHAR(255)," +
                "costo INT," +
                "precio INT," +
                "porcentaje INT," +
                "lista_id INT," +
                "FOREIGN KEY (lista_id) REFERENCES listas(id)" +
                ")";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.execute();
    }

    public static void crearTablaListas() throws SQLException {
        String query = "CREATE TABLE listas (" +
                "id INT PRIMARY KEY," +
                "nombre VARCHAR(255), " +
                "proveedor VARCHAR(255)" +
                ")";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.execute();
    }

    public static void insertarListas(List<String> listas) throws SQLException {
        String query = "INSERT INTO listas (id, nombre, proveedor) VALUES (?,?,?) ";
        PreparedStatement statement = connection.prepareStatement(query);

        for (String lista : listas) {
            String[] separado = lista.split("-");
            int codigo = ConstantesNumericas.codigoLista(UnificadorString.unirString(separado[0]).toLowerCase());
            String nombre = separado[0];
            String proveedor = separado[1];

            statement.setInt(1, codigo);
            statement.setString(2, nombre);
            statement.setString(3, proveedor);
            statement.addBatch();
        }
        statement.executeBatch();
    }

    public static void insertarProductos(List<String> listas) throws SQLException {
        HashMap<Integer, Producto> mapaProductos;
        for(String lista : listas) {
            String nombreLista = lista.split("-")[0].toLowerCase();
            nombreLista = UnificadorString.unirString(nombreLista);
            String select = "SELECT * from " + nombreLista;
            mapaProductos = ejecutarQuery(select, nombreLista);

            String query = "INSERT INTO productos (codigo, nombre, costo, precio, porcentaje, lista_id) VALUES(?,?,?,?,?,?) ";
            PreparedStatement statement = connection.prepareStatement(query);
            int numeroLista = ConstantesNumericas.codigoLista(nombreLista);

            mapaProductos.forEach((key, value) -> {
                try {
                    statement.setInt(1, value.getCodigo());
                    statement.setString(2, value.getNombre());
                    statement.setInt(3, value.getCosto());
                    statement.setInt(4, value.getPrecio());
                    statement.setInt(5, value.getPorcentaje());
                    statement.setInt(6, numeroLista);
                    statement.addBatch();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            statement.executeBatch();
        }
    }
}
