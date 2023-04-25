package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoLumilagro;
import Modelo.Productos.ProductoMafersa;
import Modelo.Utils.ConectorDB;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Utils.UnificadorString;

import java.sql.SQLException;
import java.util.*;

public class ParserMafersa implements Parser{
    private final ArrayList<String> nombres;

    public ParserMafersa() {
        nombres = ConstantesStrings.getNombresMafersa();
    }

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        HashMap<String, List<String>> rubrosSeparados = separarRubros(texto);
        ConectorDB.getConnection();
        cargarTablas(datos);
        ArrayList<String> distintos = ConstantesStrings.getDistintosLumilagro();

        for (String nombreLista : nombres) {
            nombreLista = UnificadorString.unirString(nombreLista);
            HashMap<Integer, Producto> mapaActual = datos.get(ConstantesNumericas.codigoLista(nombreLista));
            List<String> lista = rubrosSeparados.get(nombreLista);

            if (lista == null) //porque puede pasar que la lista venga por rubro, entonces la cantidad de rubros separados no va a ser la cantidad de nombres
                continue;
            for (String linea : lista) {
                String[] lineaSeparada = linea.trim().split(" ");
                List<String> partes = new ArrayList<>(Arrays.asList(lineaSeparada));
                partes.remove(1);                   //saco el codigo del fabricante

                int codigo = Integer.parseInt(partes.remove(0));

                String costoString = partes.get(partes.size() - 1);
                if (costoString.contains(","))
                    costoString = costoString.replace(",", ".");

                int costo = (int) Math.round(Double.parseDouble(costoString));
                partes.remove(partes.size() - 1);
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
    private void cargarTablas (HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        for (String nombre : nombres) {
//            boolean existeTabla = ConectorDB.existeTabla(nombre);
            String nombreUnido = UnificadorString.unirString(nombre);
            if (datos.get(ConstantesNumericas.codigoLista(nombreUnido)) == null)
                datos.put(ConstantesNumericas.codigoLista(nombreUnido), ConectorDB.seleccionarProductos(nombreUnido));
//            else {
//                HashMap<Integer, Producto> nuevoMapa = new HashMap<>();
//                datos.put(ConstantesNumericas.codigoLista(nombreUnido), nuevoMapa);
//            }
        }
    }


    /**
     * Separa el texto y coloca a los diferentes rubros en diferentes listas
     * */
    private HashMap<String, List<String>> separarRubros(List<String> texto) {
        HashMap<String, List<String>> rubrosSeparados = new HashMap<>();
        ArrayList<String> nuevaLista = null;

        for (String linea : texto) {
            linea = linea.toLowerCase();
            if (linea.contains("RUBRO :") || linea.contains("rubro :")) {
                nuevaLista = new ArrayList<>();
                Optional<String> nombreLista = nombres.stream().filter(linea :: contains).findFirst();
                if (nombreLista.isPresent())                //siempre va a estar porque se filtro con esa intencion
                    rubrosSeparados.put(UnificadorString.unirString(nombreLista.get()), nuevaLista);
            }
            else if (!linea.contains("LINEA :") && !linea.contains("linea :")) {
                assert nuevaLista != null;
                nuevaLista.add(linea.toUpperCase());
            }
        }
        return rubrosSeparados;
    }
}
