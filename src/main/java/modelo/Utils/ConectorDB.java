package modelo.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

}
