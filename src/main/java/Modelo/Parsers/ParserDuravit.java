package Modelo.Parsers;

import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDuravit;
import Modelo.Utils.ConectorDB;
import Modelo.Constantes.ConstantesStrings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserDuravit implements Parser {

    @Override
    public void parsearAProducto(List<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        boolean existeTabla = ConectorDB.existeTabla(ConstantesStrings.DURAVIT);
        HashMap<Integer, Producto> mapaDuravit;

        if (existeTabla && datos.get(ConstantesStrings.DURAVIT) == null) { //si la tabla existe pero no esta en memoria, la cargo en memoria
            mapaDuravit = cargarProductos();
            datos.put(ConstantesStrings.DURAVIT, mapaDuravit);
        } else if (!existeTabla) {                   //  si no existe creo una nueva
            mapaDuravit = new HashMap<>();
            datos.put(ConstantesStrings.DURAVIT, mapaDuravit);
        }
        //y si existe y esta en memoria uso esa

        mapaDuravit = datos.get(ConstantesStrings.DURAVIT);

        for (String linea : texto) {
            StringBuilder nombre = new StringBuilder();
            int codigo;
            int costo;

            String[] lineaSeparada = linea.trim().split(" ");
            List<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));
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
                int porcentajeAnterior = mapaDuravit.get(producto.getCodigo()).getPorcentaje();
                producto.setPorcentaje(porcentajeAnterior);
                producto.calcularPrecio();
                mapaDuravit.put(producto.getCodigo(), producto);                    //lo reemplazo porque puede pasar que el producto no se haga mas y hayan reasignado el codigo
            }
        }
    }

    /**
     * Carga los productos de la base de datos al mapa
     * */
    private HashMap<Integer, Producto> cargarProductos() throws SQLException {
        String query = "SELECT * from " + ConstantesStrings.DURAVIT + " WHERE precio != 0";
        return ConectorDB.ejecutarQuery(query, ConstantesStrings.DURAVIT);
    }
}
