package modelo;

import modelo.Estado.Estado;
import modelo.Estado.NoVacia;
import modelo.Estado.Vacia;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Aplicacion {

    private ArrayList<Producto> productos;
    private Estado estado;

    public Aplicacion(){
        productos = new ArrayList<>();
    }

    public void leerArchivo(File archivo) throws IOException {
        LectorArchivos lectorArchivos = new LectorArchivos();
        lectorArchivos.leerArchivo(archivo, productos);
        determinarEstadoBaseDeDatos();
        estado.insertarABaseDeDatos(productos);
    }

    private void determinarEstadoBaseDeDatos() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/productos", "root", "2520804");
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select count(*) from productos");

            int cantidad = 0;
            while (resultSet.next())
                cantidad = resultSet.getInt(1);

            if (cantidad == 0)
                estado = new Vacia();
            else
                estado = new NoVacia();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
