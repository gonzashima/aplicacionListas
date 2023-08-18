package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoLema;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserLema implements Parser{

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaLema;

        if (datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.LEMA)) == null) {
            mapaLema = ConectorDB.seleccionarProductos(ConstantesStrings.LEMA);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.LEMA), mapaLema);
        }

        mapaLema = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.LEMA));

        for (String linea : texto) {
            StringBuilder nombre = new StringBuilder();
            int codigo, costo;
            String codigoString, costoString;
            Producto producto;

            String[] costoSeparado = linea.trim().split("$");
            costoString = costoSeparado[1].replace(".", "");
            costoString = costoString.split(",")[0];

            costo = Integer.parseInt(costoString.replace("$", ""));

            String[] lineaSeparada = costoSeparado[0].trim().split(" ");
            List<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));
            codigoString = palabras.remove(0);
            codigo = Integer.parseInt(codigoString);

            for (String palabra : palabras){
                if (!nombre.isEmpty())
                    nombre.append(" ");
                nombre.append(palabra);
            }
            producto = new ProductoLema(nombre.toString().toUpperCase(), codigo, costo);

        }
    }
}
