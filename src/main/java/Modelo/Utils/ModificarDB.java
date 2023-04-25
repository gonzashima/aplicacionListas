package Modelo.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModificarDB {
    public void modificarDB() throws SQLException {
        ConectorDB.getConnection();
        Casas[] casas = Casas.values();
        List<String> todasLasListas = new ArrayList<>();

        for (Casas casa : casas) {
            Casas casaEnum = Casas.valueOf(casa.toString());
            String[] listas = casaEnum.getListas();
            for (String lista: listas)
                todasLasListas.add(lista + "-" + casa);
        }

//        ConectorDB.crearTablaListas();
//        ConectorDB.insertarListas(todasLasListas);
//        ConectorDB.crearTablaProductos();
        ConectorDB.insertarProductos(todasLasListas);

        System.out.println("listo");
    }
}
