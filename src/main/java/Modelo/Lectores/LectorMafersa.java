package Modelo.Lectores;
import Modelo.Utils.Constantes;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LectorMafersa implements LectorArchivos{

    private static final String LINEA_ALMANDOZ_MOLDES = "051A -";
    private static final String LINEA_ALMANDOZ_GASTRON = "051G -";
    private static final String LINEA_BELGIOCO = "A1 -";
    private static final String LINEA_NADIR = "034V1B -";
    private static final String LINEA_WHEATON = "034V1 -";
    private static final String LINEA_RIVOLI = "06A2B -";
    private static final String LINEA_ANTIAD_NEGRO_CAMP = "06A4 -";
    private static final String LINEA_CHAPA_CAMP = "06CH -";
    private static final String LINEA_ENLOZADO_CAMP = "06E -";
    private static final String LINEA_HOJALATA_MOLDES_CAMP = "06E5 -";
    private static final String LINEA_PARRILLEROS_CAMP = "06E6 -";
    private static final String LINEA_CHEF = "10A -";
    private static final String LINEA_KEY = "26KY -";
    private static final String LINEA_TRAMONTINA_TEFLON = "033T -";
    private static final String LINEA_TRAMONTINA_NEW_KOLOR = "03AB1 -";
    private static final String LINEA_TRAMONTINA_DYNAMIC = "03AB2 -";
    private static final String LINEA_SARTEN_LOZAFER = "1100 -";
    private static final String LINEA_CHURRASQ_ENLOZ = "1101 -";
    private static final String LINEA_CHURRASQ_FUND = "1103 -";
    private static final String LINEA_KUFO = "11K -";
    private static final String LINEA_DAYSAL = "121AL -";
    private static final String LINEA_DAYSAL_LUNARES = "121AN -";
    private static final String LINEA_GUADIX = "09 -";
    private static final String LINEA_TEFLON_GUADIX = "091 -";
    private static final String LINEA_LOEKEMEYER = "18L -";
    private static final String LINEA_LUMILAGRO_PLAST = "21001 -";
    private static final String LINEA_LUMILAGRO_POLI = "21002 -";
    private static final String LINEA_LUMILAGRO_SUPER = "21004 -";
    private static final String LINEA_LUMILAGRO_REPUESTOS = "21008 -";
    private static final String LINEA_LUMILAGRO_TAPONES = "21009 -";
    private static final String LINEA_LUMILAGRO_ACERO = "21C -";
    private static final String LINEA_MANFER = "22MF -";
    private static final String LINEA_MARINEX = "23MC -";
    private static final String LINEA_COLORES = "24C -";
    private static final String LINEA_DATOMAX = "24C1 -";
    private static final String LINEA_DESES = "25D1 -";
    private static final String LINEA_DESES_GASTRON = "25D4 -";
    private static final String LINEA_PLASTIC_HOUSE = "30PH -";
    private static final String LINEA_YESI = "353 -";


    private final ArrayList<String> codigos;
    private final ArrayList<String> lineas;

    /**
     * Construye la instancia del lector. Inicializa todos los codigos de las listas que va a necesitar
     * */
    public LectorMafersa() {
        codigos = Constantes.getCodigosMafersa();
        lineas = new ArrayList<>();

        Field[] fields = getClass().getDeclaredFields(); //obtengo los atributos de la clase

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) { //filtro los atributos que son static (que en este caso solo son las constantes)
                try {
                    String nombre = field.getName();
                    String valor = (String) field.get(null);
                    if (nombre.contains("LINEA"))
                        lineas.add(valor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ArrayList<String> leerArchivo(File archivo) throws IOException {
        FileInputStream file = new FileInputStream(archivo);

        XSSFWorkbook workbook = new XSSFWorkbook(file);         //creo todas las cosas necesarias
        XSSFSheet hoja = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        ArrayList<String> texto = new ArrayList<>();

        ArrayList<Integer> indicesRubros = new ArrayList<>();   //aca se guardan los indices de TODOS los rubros en la lista
        ArrayList<Integer> indicesLineas = new ArrayList<>();

        for (Row fila : hoja) {
            StringBuilder linea = new StringBuilder();
            for (Cell celda : fila){
                if (linea.length() > 0)
                    linea.append(" ");
                linea.append(formatter.formatCellValue(celda));
            }
            if(!linea.isEmpty()) {    //solo la agrego si la linea tiene algo de texto
                texto.add(linea.toString());
                if (linea.toString().contains("rubro") || linea.toString().contains("RUBRO"))
                    indicesRubros.add(texto.indexOf(linea.toString()));
                else if (linea.toString().contains("LINEA"))
                    indicesLineas.add(texto.indexOf(linea.toString()));
            }
        }

        workbook.close();

        int indicePrimerRubro = indicesRubros.get(0);
        indicesLineas = (ArrayList<Integer>) indicesLineas.stream().map(x -> x - indicePrimerRubro).collect(Collectors.toList());
        texto.subList(0,indicePrimerRubro).clear();
        indicesRubros = (ArrayList<Integer>) indicesRubros.stream().map(r -> r - indicePrimerRubro).collect(Collectors.toList());

        ArrayList<ArrayList<Integer>> resultado = filtrarRubros(indicesRubros, indicesLineas, texto);
        indicesRubros = resultado.get(0);
        indicesLineas = resultado.get(1);


        filtrarLineas(indicesRubros, indicesLineas, texto);

        return texto;
    }


    /**
     * Saca del texto los articulos de los rubros que no se trabajan
     * */
    private ArrayList<ArrayList<Integer>> filtrarRubros(ArrayList<Integer> indicesRubros, ArrayList<Integer> indicesLineas, ArrayList<String> texto) {
        for (int i = 0; i < indicesRubros.size(); i++) {
            int indice = indicesRubros.get(i);                  //indice (en el texto) en el cual hay una linea que diga rubro
            String rubro = texto.get(indice);
            int tamanioAntes = texto.size();

            if (codigos.stream().noneMatch(rubro :: contains)) {    //si el rubro no se trabaja, se elimina el texto desde el mismo hasta el siguiente rubro
                if ((i + 1) >= indicesRubros.size()) {
                    indicesLineas = (ArrayList<Integer>) indicesLineas.stream().filter(x -> !(x >= indice && x < texto.size())).collect(Collectors.toList());
                    texto.subList(indice, texto.size()).clear();
                }
                else {
                    int finalDeSublista = indicesRubros.get(i + 1);
                    indicesLineas = (ArrayList<Integer>) indicesLineas.stream().filter(x -> !(x >= indice && x < finalDeSublista)).collect(Collectors.toList());
                    texto.subList(indice, finalDeSublista).clear();
                }
            }
            int diferencia = tamanioAntes - texto.size();

            if (diferencia != 0) {
                indicesRubros.remove(i);

                List<Integer> rubrosSalteados = indicesRubros.subList(0,i);
                List<Integer> rubrosAModificar = indicesRubros.subList(i, indicesRubros.size());

                ArrayList<Integer> rubrosResultantes = (ArrayList<Integer>) rubrosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                rubrosResultantes.addAll(0, rubrosSalteados);
                indicesRubros = rubrosResultantes;

                if (i + 1 <= indicesRubros.size())
                    indicesLineas = (ArrayList<Integer>) indicesLineas.stream().map(x -> (x > indice) ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
        return new ArrayList<>(Arrays.asList(indicesRubros,indicesLineas));
    }


    /**
     * Fitra y deja solo a las lineas que se trabajan
     * */
    private void filtrarLineas(ArrayList<Integer> indicesRubros, ArrayList<Integer> indicesLineas, ArrayList<String> texto) {
        for (int i = 0; i < indicesLineas.size(); i++) {
            int indice = indicesLineas.get(i);
            String leida = texto.get(indice);
            int tamanioAntes = texto.size();

            if (lineas.stream().noneMatch(leida :: contains)) {
                if ((i + 1) >= indicesLineas.size())
                    texto.subList(indice, texto.size()).clear();
                else if (indicesRubros.contains(indicesLineas.get(i + 1) - 1))
                    texto.subList(indice, indicesLineas.get(i + 1) - 1).clear();
                else
                    texto.subList(indice, indicesLineas.get(i + 1)).clear();
            }
            int diferencia = tamanioAntes - texto.size();

            if (diferencia != 0) {
                indicesLineas.remove(i);

                List<Integer> elementosSalteados = indicesLineas.subList(0,i);
                List<Integer> elementosAModificar = indicesLineas.subList(i, indicesLineas.size());

                ArrayList<Integer> listaResultante = (ArrayList<Integer>) elementosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                listaResultante.addAll(0, elementosSalteados);
                indicesLineas = listaResultante;

                indicesRubros = (ArrayList<Integer>) indicesRubros.stream().map(x -> x > indice ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
    }

    @Override
    public String nombreTabla() {
        return "mafersa";
    }
}
