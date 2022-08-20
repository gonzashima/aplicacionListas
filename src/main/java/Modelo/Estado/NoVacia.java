package Modelo.Estado;

import Modelo.Utils.ConectorDB;
import Modelo.Productos.Producto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class NoVacia implements Estado {

    @Override
    public void insertarABaseDeDatos(ArrayList<Producto> productos) throws SQLException {
        Connection connection = ConectorDB.getConnection();
        if(connection != null) {
            Statement statement = connection.createStatement();

            int codigo;
            int costo;
            int precio;
            StringBuilder queryCosto = new StringBuilder("UPDATE duravit SET costo = CASE ");
            StringBuilder queryPrecio = new StringBuilder("precio = CASE ");

            for (Producto producto : productos) {
                codigo = producto.getCodigo();
                costo = producto.getCosto();
                precio = producto.getPrecio();

                queryCosto.append("WHEN codigo = ").append(codigo).append(" THEN ").append(costo).append(" ");
                queryPrecio.append("WHEN codigo = ").append(codigo).append(" THEN ").append(precio).append(" ");

            }
            queryCosto.append("ELSE 0 END, ");
            queryPrecio.append("ELSE 0 END;");
            queryCosto.append(queryPrecio);

            statement.executeUpdate(queryCosto.toString());
        }
    }
}
