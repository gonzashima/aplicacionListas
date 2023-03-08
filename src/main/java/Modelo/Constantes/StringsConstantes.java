package Modelo.Constantes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class StringsConstantes {
    public static final String DURAVIT = "duravit";


    //COSAS QUE SE MARCAN DIFERENTE EN MAFERSA
    public static final String LUMILAGRO_LUMINOX = "LUMINOX";
    public static final String LUMILAGRO_REPUESTO_TERMO = "REPUESTO TERMO";
    public static final String LUMILAGRO_REPUESTO_COMPACTO = "REPUESTO COMPACTO";
    public static final String LUMILAGRO_TAPON_CEBADOR = "TAPON CEBADOR";
    public static final String LUMILAGRO_TAPON_ESPIRAL = "TAPON ESPIRAL";
    public static final String LUMILAGRO_TAPON_TERMO = "TAPON P/TERMO";
    public static final String LUMILAGRO_TAPON_JARRA = "TAPON P/JARRA";
    public static final String LUMILAGRO_TAPON_PICO = "TAPON PICO";

    public static final String NOMBRE_ALMANDOZ = "almandoz";
    public static final String NOMBRE_BELGIOCO = "bel gioco";
    public static final String NOMBRE_NADIR = "nadir";
    public static final String NOMBRE_TRAMONTINA = "tramontina";
    public static final String NOMBRE_WHEATON = "wheaton";
    public static final String NOMBRE_CAMPAGNA = "campagna";
    public static final String NOMBRE_CHEF = "chef";
    public static final String NOMBRE_LOZAFER = "churrasqueras y sartenes";
    public static final String NOMBRE_KUFO = "kufo";
    public static final String NOMBRE_DAYSAL = "daysal";
    public static final String NOMBRE_GUADIX = "guadix";
    public static final String NOMBRE_LOEKEMEYER = "loekemeyer";
    public static final String NOMBRE_LUMILAGRO = "lumilagro";
    public static final String NOMBRE_MANFER = "man-fer";
    public static final String NOMBRE_MARINEX = "marinex";
    public static final String NOMBRE_COLORES = "colores";
    public static final String NOMBRE_DATOMAX = "datomax";
    public static final String NOMBRE_DESES_PLAST = "deses-plast";
    public static final String NOMBRE_PLASTIC_HOUSE = "plastic house";
    public static final String NOMBRE_YESI = "yesi";

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

    public static ArrayList<String> getCodigosMafersa() {
        ArrayList<String> codigos = new ArrayList<>();
        Field[] fields = StringsConstantes.class.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class) && field.getName().contains("CODIGO_")) {
                try {
                    String valor = (String) field.get(null);
                    codigos.add(valor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return codigos;
    }

    public static ArrayList<String> getNombresMafersa() {
        ArrayList<String> nombres = new ArrayList<>();
        Field[] fields = StringsConstantes.class.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class) && field.getName().contains("NOMBRE_")) {
                try {
                    String valor = (String) field.get(null);
                    nombres.add(valor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return nombres;
    }

    public static ArrayList<String> getDistintosLumilagro() {
        ArrayList<String> nombres = new ArrayList<>();
        Field[] fields = StringsConstantes.class.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class) && field.getName().contains("LUMILAGRO_")) {
                try {
                    String valor = (String) field.get(null);
                    nombres.add(valor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return nombres;
    }
}
