package modelo;

import modelo.Estado.Estado;
import modelo.Estado.NoVacia;
import modelo.Estado.Vacia;
import modelo.Productos.Producto;
import modelo.Utils.LectorArchivos;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Aplicacion {

    private final ArrayList<Producto> productos;
    private Estado estado;

    public Aplicacion(){
        productos = new ArrayList<>();
    }

    public void leerArchivo(File archivo) throws IOException, SQLException {
        LectorArchivos lectorArchivos = new LectorArchivos();
        lectorArchivos.leerArchivo(archivo, productos);
        determinarEstadoBaseDeDatos();
        estado.insertarABaseDeDatos(productos);
    }

    private void determinarEstadoBaseDeDatos() throws SQLException {
        Connection connection = ConectorDB.getConnection();

        if(connection != null) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(*) from productos");
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
}
