package com.listas.modelo.constantes;

import java.util.Arrays;
import java.util.List;

/**
 * Enum que representa las diferentes casas/proveedores.
 * Migrado de Casas.java y ConstantesStrings.java del proyecto original.
 */
public enum Casa {
    TREBOL(new String[]{"DURAVIT"}, "TREBOL", "duravit", 247),
    MAFERSA(new String[]{
            "DESES PLAST", "ALMANDOZ", "BEL GIOCO", "NADIR", "TRAMONTINA",
            "WHEATON", "CAMPAGNA", "CHEF", "CHURRASQUERAS Y SARTENES", "KUFO",
            "DAYSAL", "GUADIX", "LOEKEMEYER", "LUMILAGRO", "MAN FER",
            "MARINEX", "COLORES", "DATOMAX", "PLASTIC HOUSE", "YESI"
    }, "MAFERSA", "mafersa", 380),
    RESPONTECH(new String[]{"RESPONTECH"}, "RESPONTECH", "respontech", 238),
    RIGOLLEAU(new String[]{"RIGOLLEAU"}, "RIGOLLEAU", "rigolleau", 254),
    LEMA(new String[]{"LEMA"}, "LEMA", "lema", 4),
    RODECA(new String[]{"RODECA"}, "RODECA", "rodeca", 342),
    DIFPLAST(new String[]{"DIFPLAST"}, "DIFPLAST", "difplast", 147);

    private final String[] listas;
    private final String nombre;
    private final String claveLectura;
    private final int codigoCasa;

    Casa(String[] listas, String nombre, String claveLectura, int codigoCasa) {
        this.listas = listas;
        this.nombre = nombre;
        this.claveLectura = claveLectura;
        this.codigoCasa = codigoCasa;
    }

    public List<String> getListas() {
        return Arrays.asList(listas);
    }

    public String getNombre() {
        return nombre;
    }

    public String getClaveLectura() {
        return claveLectura;
    }

    public int getCodigoCasa() {
        return codigoCasa;
    }

    /**
     * Busca la casa a la que pertenece un archivo por su nombre.
     */
    public static Casa buscarPorNombreArchivo(String nombreArchivo) {
        nombreArchivo = nombreArchivo.toLowerCase();
        for (Casa casa : values()) {
            if (nombreArchivo.contains(casa.claveLectura)) {
                return casa;
            }
            // Caso especial para "casa lema" o "casalema"
            if (casa == LEMA && (nombreArchivo.contains("casa lema") || nombreArchivo.contains("casalema"))) {
                return casa;
            }
        }
        return null;
    }

    /**
     * Devuelve el tipo de cálculo de precio para una lista específica.
     * Útil para sublistas de MAFERSA que tienen distintas fórmulas.
     */
    public static String tipoCasaParaLista(String nombreLista) {
        nombreLista = nombreLista.toLowerCase();
        if (nombreLista.contains("lumilagro")) {
            return "lumilagro";
        }
        for (Casa casa : values()) {
            for (String lista : casa.listas) {
                if (lista.equalsIgnoreCase(nombreLista)) {
                    return casa.claveLectura;
                }
            }
        }
        return null;
    }

    /**
     * Devuelve el código de casa (ej: 247, 380) a partir del listaId.
     * Útil para armar carteles cuando codigoCasa no está persistido.
     */
    public static int codigoCasaPorListaId(int listaId) {
        String nombreLista = CodigosListas.nombreLista(listaId);
        if (nombreLista == null) return 0;
        for (Casa casa : values()) {
            for (String lista : casa.listas) {
                if (lista.equalsIgnoreCase(nombreLista) || casa.claveLectura.equalsIgnoreCase(nombreLista)) {
                    return casa.codigoCasa;
                }
            }
        }
        return 0;
    }
}

