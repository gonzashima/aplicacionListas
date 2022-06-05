package modelo.Estado;

import modelo.Producto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class NoVacia implements Estado {
    @Override
    public void insertarABaseDeDatos(ArrayList<Producto> productos) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/productos", "root", "2520804");
            Statement statement = connection.createStatement();

            int codigo;
            int costo;
            int precio;
            StringBuilder queryCosto = new StringBuilder("UPDATE productos SET costo = CASE ");
            StringBuilder queryPrecio = new StringBuilder("precio = CASE ");

            for (Producto producto : productos) {
                codigo = producto.getCodigo();
                costo = producto.getCosto();
                precio = producto.precio();

                queryCosto.append("WHEN codigo = ").append(codigo).append(" THEN ").append(costo).append(" ");
                queryPrecio.append("WHEN codigo = ").append(codigo).append(" THEN ").append(precio).append(" ");

            }
            queryCosto.append("ELSE 0 END, ");
            queryPrecio.append("ELSE 0 END;");
            queryCosto.append(queryPrecio);

            statement.executeUpdate(queryCosto.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
