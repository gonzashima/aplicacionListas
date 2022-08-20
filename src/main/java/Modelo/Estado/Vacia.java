package Modelo.Estado;

import Modelo.Utils.ConectorDB;
import Modelo.Productos.Producto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Vacia implements Estado {

    @Override
    public void insertarABaseDeDatos(ArrayList<Producto> productos) throws SQLException {
        Connection connection = ConectorDB.getConnection();
        if(connection != null) {
            Statement statement = connection.createStatement();

            int codigo;
            String nombre;
            int costo;
            int precio;
            int porcentaje;
            StringBuilder query = new StringBuilder("INSERT INTO duravit(codigo, nombre, costo, precio, porcentaje) VALUES ");

            for (int i = 0; i < productos.size(); i++) {
                codigo = productos.get(i).getCodigo();
                nombre = productos.get(i).getNombre();
                costo = productos.get(i).getCosto();
                precio = productos.get(i).getPrecio();
                porcentaje = productos.get(i).getPorcentaje();

                query.append("(").append(codigo).append(", '").append(nombre).append("',").append(costo).append(",").append(precio).append(",").
                        append(porcentaje).append(")");
                if (i < productos.size() - 1)
                    query.append(',');
            }
            statement.executeUpdate(query.toString());
        }
    }
}
