package Modelo.Parsers;

import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ParserDuravit implements Parser {

    private static final String NOMBRE = "duravit";

    @Override
    public void parsearAProducto(ArrayList<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        boolean existeTabla = ConectorDB.existeTabla(NOMBRE);
        HashMap<Integer, Producto> mapaDuravit;

        if (existeTabla && datos.get(NOMBRE) == null) { //si la tabla existe pero no esta en memoria, la cargo en memoria
            mapaDuravit = new HashMap<>();
            cargarProductos(mapaDuravit);
            datos.put(NOMBRE, mapaDuravit);
        } else if (!existeTabla) {                   //  si no existe creo una nueva
            mapaDuravit = new HashMap<>();
            datos.put(NOMBRE, mapaDuravit);
        }
        //y si existe y esta en memoria uso esa

        mapaDuravit = datos.get(NOMBRE);

        for (String linea : texto) {
            StringBuilder nombre = new StringBuilder();
            int codigo;
            int costo;

            String[] lineaSeparada = linea.trim().split(" ");
            ArrayList<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));

            boolean quedan = palabras.remove("");

            while (quedan)
                quedan = palabras.remove("");

            int tamanio = palabras.size();

            for (int i = 0; i < tamanio - 2; i++) {
                nombre.append(palabras.get(i));
                if (i != tamanio - 3)
                    nombre.append(" ");
            }

            codigo = Integer.parseInt(palabras.get(tamanio - 2));
            costo = (int) Math.round(Double.parseDouble(palabras.get(tamanio - 1)));

            Producto producto = new Producto(nombre.toString(), codigo, costo);
            producto.calcularPrecio();

            if (mapaDuravit.isEmpty() || !mapaDuravit.containsKey(producto.getCodigo()))
                mapaDuravit.put(producto.getCodigo(), producto);
            else {
                Producto anterior = mapaDuravit.get(producto.getCodigo());
                anterior.setCosto(producto.getCosto());
                anterior.calcularPrecio();
            }
        }
    }

    private void cargarProductos(HashMap<Integer, Producto> mapaDuravit) throws SQLException {
        String query = "SELECT * from " + NOMBRE + " WHERE precio != 0";
        ResultSet rs = ConectorDB.ejecutarQuery(query);

        while (rs.next()) {
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            int costo = rs.getInt("costo");
            int precio = rs.getInt("precio");
            int porcentaje = rs.getInt("porcentaje");

            Producto producto = new Producto(codigo, nombre, costo, precio, porcentaje);
            mapaDuravit.put(producto.getCodigo(), producto);
        }
    }
}
