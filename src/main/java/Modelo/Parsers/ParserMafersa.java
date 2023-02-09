package Modelo.Parsers;

import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class ParserMafersa implements Parser{
    private static final String CODIGO_ALMANDOZ = "02A -";
    private static final String CODIGO_BELGIOCO = "02BG -";
    private static final String CODIGO_NADIR = "03NA -";
    private static final String CODIGO_TRAMONTINA = "03T -";
    private static final String CODIGO_WHEATON = "03W -";
    private static final String CODIGO_CAMPAGNA = "06C -";
    private static final String CODIGO_CHEF = "10A -";
    private static final String CODIGO_LOZAFER = "11 -";
    private static final String CODIGO_KUFO = "11K -";
    private static final String CODIGO_DAYSAL = "12D -";
    private static final String CODIGO_GUADIX = "16B -";
    private static final String CODIGO_LOEKEMEYER = "18L -";
    private static final String CODIGO_LUMILAGRO = "21 -";
    private static final String CODIGO_MANFER = "22 -";
    private static final String CODIGO_MARINEX = "23M -";
    private static final String CODIGO_COLORES = "24C -";
    private static final String CODIGO_DATOMAX = "24C1 -";
    private static final String CODIGO_DESES = "25D -";
    private static final String CODIGO_PLASTIC_HOUSE = "30P -";
    private static final String CODIGO_YESI = "35Y -";

    private final ArrayList<String> nombres;

    public ParserMafersa() {
        nombres = new ArrayList<>();

        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers()) && !field.getName().contains("CODIGO_") && field.getType().isAssignableFrom(String.class)) {
                try {
                    String valor = (String) field.get(null);
                    nombres.add(valor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void parsearAProducto(ArrayList<String> texto, HashMap<String, HashMap<Integer, Producto>> datos) {
        ArrayList<ArrayList<String>> rubrosSeparados = separarRubros(texto);

        ConectorDB.getConnection();



    }

//    private void cargarTablas (HashMap<String, HashMap<Integer, Producto>> datos) {
//        for
//    }

    private ArrayList<ArrayList<String>> separarRubros(ArrayList<String> texto) {
        ArrayList<ArrayList<String>> rubrosSeparados = new ArrayList<>();
        ArrayList<String> nuevaLista = null;

        for (String linea : texto) {
            if (linea.contains("RUBRO :") || linea.contains("rubro :")) {
                nuevaLista = new ArrayList<>();
                rubrosSeparados.add(nuevaLista);
            }
            if (!linea.contains("LINEA :") && !linea.contains("linea :")) {
                assert nuevaLista != null;
                nuevaLista.add(linea);
            }
        }
        return rubrosSeparados;
    }
}
