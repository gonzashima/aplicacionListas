package Modelo.Parsers;

import Modelo.Productos.Producto;
import Modelo.Productos.ProductoLumilagro;
import Modelo.Productos.ProductoMafersa;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.Constantes;
import Modelo.Utils.UnificadorString;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class ParserMafersa implements Parser{
    private final ArrayList<String> nombres;

    public ParserMafersa() {
        nombres = Constantes.getNombresMafersa();
    }

    @Override
    public void parsearAProducto(ArrayList<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        HashMap<String, ArrayList<String>> rubrosSeparados = separarRubros(texto);
        ConectorDB.getConnection();
        cargarTablas(datos);
        ArrayList<String> distintos = Constantes.getDistintosLumilagro();

        for (String nombreLista : nombres) {
            HashMap<Integer, Producto> mapaActual = datos.get(nombreLista);
            ArrayList<String> lista = rubrosSeparados.get(nombreLista);

            for (String linea : lista) {
                String[] lineaSeparada = linea.trim().split(" ");
                ArrayList<String> partes = new ArrayList<>(Arrays.asList(lineaSeparada));
                partes.remove(1);                   //saco el codigo del fabricante

                int codigo = Integer.parseInt(partes.remove(0));
                int costo = (int) Math.round(Double.parseDouble(partes.remove(partes.size() - 1)));
                String nombre = String.join(" ", partes);
                Producto producto;

                if (distintos.stream().anyMatch(nombre::contains))
                    producto = new ProductoLumilagro(nombre, codigo, costo);
                else
                    producto = new ProductoMafersa(nombre, codigo, costo);

                producto.calcularPrecio();
                if (mapaActual.isEmpty() || !mapaActual.containsKey(producto.getCodigo()))
                    mapaActual.put(producto.getCodigo(), producto);
                else {
                    int porcentajeAnterior = mapaActual.get(producto.getCodigo()).getPorcentaje();
                    producto.setPorcentaje(porcentajeAnterior);
                    producto.calcularPrecio();
                    mapaActual.put(producto.getCodigo(), producto);
                }
            }
        }
    }

    /**
     * Carga en memoria las tablas con las que se trabajan. Si no existe, crea una nueva
     * */
    private void cargarTablas (HashMap<String, HashMap<Integer, Producto>> datos) throws SQLException {
        for (String nombre : nombres) {
            boolean existeTabla = ConectorDB.existeTabla(nombre);
            String nombreOriginal = nombre;
            if (existeTabla && datos.get(nombre) == null) {
                nombre = UnificadorString.unirString(nombre);

                String query = "SELECT * from " + nombre + " WHERE precio != 0";
                datos.put(nombreOriginal, ConectorDB.ejecutarQuery(query, nombre));
            }
            else if (!existeTabla) {
                HashMap<Integer, Producto> nuevoMapa = new HashMap<>();
                datos.put(nombre, nuevoMapa);
            }
        }
    }


    /**
     * Separa el texto y coloca a los diferentes rubros en diferentes listas
     * */
    private HashMap<String, ArrayList<String>> separarRubros(ArrayList<String> texto) {
        HashMap<String, ArrayList<String>> rubrosSeparados = new HashMap<>();
        ArrayList<String> nuevaLista = null;

        for (String linea : texto) {
            linea = linea.toLowerCase();
            if (linea.contains("RUBRO :") || linea.contains("rubro :")) {
                nuevaLista = new ArrayList<>();
                Optional<String> nombreLista = nombres.stream().filter(linea :: contains).findFirst();
                if (nombreLista.isPresent())                //siempre va a estar porque se filtro con esa intencion
                    rubrosSeparados.put(nombreLista.get(), nuevaLista);
            }
            else if (!linea.contains("LINEA :") && !linea.contains("linea :")) {
                assert nuevaLista != null;
                nuevaLista.add(linea.toUpperCase());
            }
        }
        return rubrosSeparados;
    }
}
