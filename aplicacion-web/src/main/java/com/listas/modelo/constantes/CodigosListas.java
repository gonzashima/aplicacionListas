package com.listas.modelo.constantes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapeo de nombres de listas a sus IDs en la base de datos.
 * Migrado de ConstantesNumericas.java del proyecto original.
 */
public class CodigosListas {

    private static final Map<String, Integer> codigosListas = new HashMap<>();

    static {
        codigosListas.put("duravit", 1);
        codigosListas.put("deses_plast", 2);
        codigosListas.put("almandoz", 3);
        codigosListas.put("bel_gioco", 4);
        codigosListas.put("nadir", 5);
        codigosListas.put("tramontina", 6);
        codigosListas.put("wheaton", 7);
        codigosListas.put("campagna", 8);
        codigosListas.put("chef", 9);
        codigosListas.put("churrasqueras_y_sartenes", 10);
        codigosListas.put("kufo", 11);
        codigosListas.put("daysal", 12);
        codigosListas.put("guadix", 13);
        codigosListas.put("loekemeyer", 14);
        codigosListas.put("lumilagro", 15);
        codigosListas.put("man_fer", 16);
        codigosListas.put("marinex", 17);
        codigosListas.put("colores", 18);
        codigosListas.put("datomax", 19);
        codigosListas.put("plastic_house", 20);
        codigosListas.put("yesi", 21);
        codigosListas.put("respontech", 22);
        codigosListas.put("rigolleau", 23);
        codigosListas.put("lema", 24);
        codigosListas.put("rodeca", 25);
        codigosListas.put("difplast", 26);
    }

    private CodigosListas() {}

    /**
     * Devuelve el código de lista (lista_id) para un nombre dado.
     * El nombre se normaliza: espacios y guiones se reemplazan por '_'.
     */
    public static int codigoLista(String nombre) {
        String clave = unirString(nombre.toLowerCase());
        Integer codigo = codigosListas.get(clave);
        if (codigo == null) {
            throw new IllegalArgumentException("Lista desconocida: " + nombre);
        }
        return codigo;
    }

    /**
     * Busca el nombre de la lista por su código.
     */
    public static String nombreLista(int codigo) {
        return codigosListas.entrySet().stream()
                .filter(e -> e.getValue() == codigo)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Unifica strings reemplazando espacios y guiones por '_'.
     * Migrado de UnificadorString.java
     */
    public static String normalizarNombre(String string) {
        return unirString(string);
    }

    private static String unirString(String string) {
        if (!string.contains(" ") && !string.contains("-"))
            return string;
        return string.trim().replace(" ", "_").replace("-", "_");
    }
}

