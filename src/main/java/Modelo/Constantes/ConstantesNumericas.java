package Modelo.Constantes;

import Modelo.Utils.UnificadorString;

import java.util.HashMap;

public class ConstantesNumericas {

    public static final int IVA = 21;
    public static final double MEDIO_IVA = 10.5;

    public static final int CODIGO_TREBOL = 247;
    public static final int CODIGO_MAFERSA = 380;

    private static final HashMap<String, Integer> codigosListas;

    static {
        codigosListas = new HashMap<>();
        codigosListas.put(ConstantesStrings.DURAVIT, 1);
        codigosListas.put(UnificadorString.unirString(ConstantesStrings.NOMBRE_DESES_PLAST), 2);
        codigosListas.put(ConstantesStrings.NOMBRE_ALMANDOZ, 3);
        codigosListas.put(UnificadorString.unirString(ConstantesStrings.NOMBRE_BELGIOCO), 4);
        codigosListas.put(ConstantesStrings.NOMBRE_NADIR, 5);
        codigosListas.put(ConstantesStrings.NOMBRE_TRAMONTINA, 6);
        codigosListas.put(ConstantesStrings.NOMBRE_WHEATON, 7);
        codigosListas.put(ConstantesStrings.NOMBRE_CAMPAGNA, 8);
        codigosListas.put(ConstantesStrings.NOMBRE_CHEF, 9);
        codigosListas.put(UnificadorString.unirString(ConstantesStrings.NOMBRE_LOZAFER), 10);
        codigosListas.put(ConstantesStrings.NOMBRE_KUFO, 11);
        codigosListas.put(ConstantesStrings.NOMBRE_DAYSAL, 12);
        codigosListas.put(ConstantesStrings.NOMBRE_GUADIX, 13);
        codigosListas.put(ConstantesStrings.NOMBRE_LOEKEMEYER, 14);
        codigosListas.put(ConstantesStrings.NOMBRE_LUMILAGRO, 15);
        codigosListas.put(UnificadorString.unirString(ConstantesStrings.NOMBRE_MANFER), 16);
        codigosListas.put(ConstantesStrings.NOMBRE_MARINEX, 17);
        codigosListas.put(ConstantesStrings.NOMBRE_COLORES, 18);
        codigosListas.put(ConstantesStrings.NOMBRE_DATOMAX, 19);
        codigosListas.put(UnificadorString.unirString(ConstantesStrings.NOMBRE_PLASTIC_HOUSE), 20);
        codigosListas.put(ConstantesStrings.NOMBRE_YESI, 21);
        codigosListas.put(ConstantesStrings.RESPONTECH, 22);
        codigosListas.put(ConstantesStrings.RIGOLLEAU, 23);
    }

    private ConstantesNumericas() {}

    public static int codigoLista(String lista) {
        return codigosListas.get(UnificadorString.unirString(lista));
    }
}
