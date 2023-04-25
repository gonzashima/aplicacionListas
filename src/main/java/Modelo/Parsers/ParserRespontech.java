package Modelo.Parsers;

import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoRespontech;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.Triple;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserRespontech implements Parser{
    @Override
    public void parsearAProducto(List<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        boolean existeTabla = ConectorDB.existeTabla(ConstantesStrings.RESPONTECH);
        HashMap<Integer, Producto> mapaRespontech;
        List<Triple<String, String, Integer>> sinCodigo = new ArrayList<>();

        if (existeTabla && datos.get(ConstantesStrings.RESPONTECH) == null) { //si la tabla existe, pero no está en memoria, la cargo en memoria
            mapaRespontech = cargarProductos();
            datos.put(ConstantesStrings.RESPONTECH, mapaRespontech);
        } else if (!existeTabla) {                   //  si no existe creo una nueva
            mapaRespontech = new HashMap<>();
            datos.put(ConstantesStrings.RESPONTECH, mapaRespontech);
        }
        //y si existe y está en memoria uso esa

        mapaRespontech = datos.get(ConstantesStrings.RESPONTECH);

        for (String linea : texto) {
            StringBuilder nombre = new StringBuilder();
            int codigo, costo;
            String codigoString, costoString;
            Producto producto;

            String[] lineaSeparada = linea.trim().split(" ");
            List<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));
            boolean quedan = palabras.remove("");

            while (quedan)
                quedan = palabras.remove("");

            codigoString = palabras.remove(0);

            costoString = palabras.remove(palabras.size() - 1);
            costoString = costoString.replace(".", "");
            costoString = costoString.split(",")[0];

            costo = Integer.parseInt(costoString.replace("$", ""));
            palabras.remove(palabras.size() - 1);

            for (String palabra : palabras) {
                if (nombre.length() > 0)
                    nombre.append(" ");
                nombre.append(palabra);
            }
            try {
                codigo = Integer.parseInt(codigoString);
                producto = new ProductoRespontech(nombre.toString().toUpperCase(), codigo, costo);
                producto.calcularPrecio();
                insertarAlMapa(producto, mapaRespontech);
            } catch (NumberFormatException e) {
                Triple<String, String, Integer> triple = new Triple<>(nombre.toString().toUpperCase(), codigoString, costo);
                sinCodigo.add(triple);
            }
        }
        asignarCodigos(sinCodigo, mapaRespontech);
    }

    /**
     * Carga los productos de la base de datos al mapa
     * */
    private HashMap<Integer, Producto> cargarProductos() throws SQLException {
        String query = "SELECT * from " + ConstantesStrings.RESPONTECH + " WHERE precio != 0";
        return ConectorDB.ejecutarQuery(query, ConstantesStrings.RESPONTECH);
    }

    private void asignarCodigos(List<Triple<String, String, Integer>> sinCodigos, HashMap<Integer, Producto> mapaRespontech) {
        Producto producto;
        for (Triple<String, String, Integer> triple : sinCodigos) {
            String codigo = triple.getSecond();
            String codigoHash = DigestUtils.sha256Hex(codigo);

            int codigoNumerico = Math.abs(codigoHash.hashCode());
            int codigoFinal = codigoNumerico % 1_000_000;

            producto = new ProductoRespontech(triple.getFirst(), codigoFinal, triple.getThird());
            producto.calcularPrecio();

            insertarAlMapa(producto, mapaRespontech);
        }
    }

    private void insertarAlMapa(Producto producto, HashMap<Integer, Producto> mapa) {
        if (mapa.isEmpty() || !mapa.containsKey(producto.getCodigo()))
            mapa.put(producto.getCodigo(), producto);
        else {
            int porcentajeAnterior = mapa.get(producto.getCodigo()).getPorcentaje();
            producto.setPorcentaje(porcentajeAnterior);
            producto.calcularPrecio();
            mapa.put(producto.getCodigo(), producto);           //lo reemplazo porque puede pasar que el producto no se haga mas y hayan reasignado el codigo
        }
    }
}
