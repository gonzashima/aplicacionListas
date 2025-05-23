package Modelo.Lectores;
import Modelo.Constantes.ConstantesStrings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static final String LINEA_CHURRASQUERAS_JOVIFEL = "06CI -";
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

    private static final String MAFERSA_REGEX = "^(\\S+(?:\\s+\\S+)*?)\\s*\\|\\s*(\\S+(?:\\s+\\S+)*?)$";

    private final List<String> codigos;
    private final List<String> lineas;

    /**
     * Construye la instancia del lector. Inicializa todos los codigos de las listas que va a necesitar
     * */
    public LectorMafersa() {
        codigos = ConstantesStrings.getCodigosMafersa();
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
                    System.out.println("Error al obtener los nombres de las lineas de mafersa");
                }
            }
        }
    }

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
//        FileInputStream file = new FileInputStream(archivo);
//        XSSFWorkbook workbook = new XSSFWorkbook(file);         //creo todas las cosas necesarias
//        XSSFSheet hoja = workbook.getSheetAt(0);
//        DataFormatter formatter = new DataFormatter();
//        List<String> texto = new ArrayList<>();
//
//        List<Integer> indicesRubros = new ArrayList<>();   //aca se guardan los indices de TODOS los rubros en la lista
//        List<Integer> indicesLineas = new ArrayList<>();
//
//        for (Row fila : hoja) {
//            StringBuilder linea = new StringBuilder();
//            for (Cell celda : fila){
//                if (!linea.isEmpty())
//                    linea.append(" ");
//                linea.append(formatter.formatCellValue(celda));
//            }
//            if(!linea.isEmpty()) {    //solo la agrego si la linea tiene algo de texto
//                texto.add(linea.toString());
//                if (linea.toString().contains("rubro") || linea.toString().contains("RUBRO"))
//                    indicesRubros.add(texto.indexOf(linea.toString()));
//                else if (linea.toString().contains("LINEA"))
//                    indicesLineas.add(texto.indexOf(linea.toString()));
//            }
//        }
//
//        workbook.close();
//
//        int indicePrimerRubro = indicesRubros.get(0);
//        indicesLineas = indicesLineas.stream().map(x -> x - indicePrimerRubro).collect(Collectors.toList());
//        texto.subList(0,indicePrimerRubro).clear();
//        indicesRubros = indicesRubros.stream().map(r -> r - indicePrimerRubro).collect(Collectors.toList());
//
//        List<List<Integer>> resultado = filtrarRubros(indicesRubros, indicesLineas, texto);
//        indicesRubros = resultado.get(0);
//        indicesLineas = resultado.get(1);
//
//
//        filtrarLineas(indicesRubros, indicesLineas, texto);

        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();

        String textoOriginal = String.valueOf(textStripper.getText(pdf));
        String[] lineasTexto = textoOriginal.trim().split(textStripper.getLineSeparator());
        List<String> texto = new ArrayList<>();

        List<Integer> indicesRubros = new ArrayList<>();   //aca se guardan los indices de TODOS los rubros en la lista
        List<Integer> indicesLineas = new ArrayList<>();

        Set<String> terminosExcluir = new HashSet<>(List.of(
                "MAFERSA HOJA", "Codigo CODFAB", "Teléfono/FAX",
                "www.bazarmafersa", "San Mauro 961", "LISTA DE PRECIOS"
        ));

        for (String linea : lineasTexto) {
            if (!linea.isEmpty() && !linea.matches("^-+$") && terminosExcluir.stream().noneMatch(linea::contains)) {
                texto.add(linea);
            }
            if (linea.contains("rubro") || linea.contains("RUBRO")) {
                indicesRubros.add(texto.indexOf(linea));
            } else if (linea.contains("LINEA")) {
                indicesLineas.add(texto.indexOf(linea));
            }
        }

        int indicePrimerRubro = indicesRubros.get(0);
        indicesLineas = indicesLineas.stream().map(x -> x - indicePrimerRubro).collect(Collectors.toList());
        texto.subList(0,indicePrimerRubro).clear();
        indicesRubros = indicesRubros.stream().map(r -> r - indicePrimerRubro).collect(Collectors.toList());

        List<List<Integer>> resultado = filtrarRubros(indicesRubros, indicesLineas, texto);
        indicesRubros = resultado.get(0);
        indicesLineas = resultado.get(1);

        filtrarLineas(indicesRubros, indicesLineas, texto);

        texto = texto.stream().map(linea -> linea.replaceAll("(?<=\\S)\\s+(?=\\S)", " ").trim()).collect(Collectors.toList());

        Pattern pattern = Pattern.compile(MAFERSA_REGEX);
        ListIterator<String> iterator = texto.listIterator();

        while (iterator.hasNext()) {
            String linea = iterator.next();
            Matcher matcher = pattern.matcher(linea);

            if (matcher.matches()) {
                String bloque1 = matcher.group(1).trim();
                String bloque2 = matcher.group(2) != null ? matcher.group(2).trim() : null;

                iterator.set(bloque1);

                if (bloque2 != null && !bloque2.isBlank()) {
                    iterator.add(bloque2);
                }
            }
        }

        texto = texto.stream().map(linea -> linea.replace("|", "")).collect(Collectors.toList());

        return texto;
    }


    /**
     * Saca del texto los articulos de los rubros que no se trabajan
     * */
    private List<List<Integer>> filtrarRubros(List<Integer> indicesRubros, List<Integer> indicesLineas, List<String> texto) {
        for (int i = 0; i < indicesRubros.size(); i++) {
            int indice = indicesRubros.get(i);                  //indice (en el texto) en el cual hay una linea que diga rubro
            String rubro = texto.get(indice);
            int tamanioAntes = texto.size();

            if (codigos.stream().noneMatch(rubro :: contains)) {    //si el rubro no se trabaja, se elimina el texto desde el mismo hasta el siguiente rubro
                if ((i + 1) >= indicesRubros.size()) {
                    indicesLineas = indicesLineas.stream().filter(x -> !(x >= indice && x < texto.size())).collect(Collectors.toList());
                    texto.subList(indice, texto.size()).clear();
                }
                else {
                    int finalDeSublista = indicesRubros.get(i + 1);
                    indicesLineas = indicesLineas.stream().filter(x -> !(x >= indice && x < finalDeSublista)).collect(Collectors.toList());
                    texto.subList(indice, finalDeSublista).clear();
                }
            }
            int diferencia = tamanioAntes - texto.size();

            if (diferencia != 0) {
                indicesRubros.remove(i);

                List<Integer> rubrosSalteados = indicesRubros.subList(0,i);
                List<Integer> rubrosAModificar = indicesRubros.subList(i, indicesRubros.size());

                List<Integer> rubrosResultantes = rubrosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                rubrosResultantes.addAll(0, rubrosSalteados);
                indicesRubros = rubrosResultantes;

                if (i + 1 <= indicesRubros.size())
                    indicesLineas = indicesLineas.stream().map(x -> (x > indice) ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
        return new ArrayList<>(Arrays.asList(indicesRubros,indicesLineas));
    }


    /**
     * Fitra y deja solo a las lineas que se trabajan
     * */
    private void filtrarLineas(List<Integer> indicesRubros, List<Integer> indicesLineas, List<String> texto) {
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

                List<Integer> listaResultante = elementosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                listaResultante.addAll(0, elementosSalteados);
                indicesLineas = listaResultante;

                indicesRubros = indicesRubros.stream().map(x -> x > indice ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
    }
}
