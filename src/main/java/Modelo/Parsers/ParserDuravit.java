package Modelo.Parsers;

import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDuravit;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.Constantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ParserDuravit implements Parser {

    @Override
    public void parsearAProducto(ArrayList<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        boolean existeTabla = ConectorDB.existeTabla(Constantes.DURAVIT);
        HashMap<Integer, Producto> mapaDuravit;

        if (existeTabla && datos.get(Constantes.DURAVIT) == null) { //si la tabla existe pero no esta en memoria, la cargo en memoria
            mapaDuravit = cargarProductos();
            datos.put(Constantes.DURAVIT, mapaDuravit);
        } else if (!existeTabla) {                   //  si no existe creo una nueva
            mapaDuravit = new HashMap<>();
            datos.put(Constantes.DURAVIT, mapaDuravit);
        }
        //y si existe y esta en memoria uso esa

        mapaDuravit = datos.get(Constantes.DURAVIT);

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

            Producto producto = new ProductoDuravit(nombre.toString(), codigo, costo);
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

    /**
     * Carga los productos de la base de datos al mapa
     * */
    private HashMap<Integer, Producto> cargarProductos() throws SQLException {
        String query = "SELECT * from " + Constantes.DURAVIT + " WHERE precio != 0";
        return ConectorDB.ejecutarQuery(query, Constantes.DURAVIT);
    }
}
