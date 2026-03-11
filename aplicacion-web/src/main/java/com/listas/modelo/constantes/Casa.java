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
            "deses-plast", "almandoz", "bel gioco", "nadir", "tramontina",
            "wheaton", "campagna", "chef", "churrasqueras y sartenes", "kufo",
            "daysal", "guadix", "loekemeyer", "lumilagro", "man-fer",
            "marinex", "colores", "datomax", "plastic house", "yesi"
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
        // Normalizar: minúsculas, reemplazar espacios y guiones por nada (para comparación compacta)
        String normalizado = nombreArchivo.toLowerCase();
        String compacto = normalizado.replace(" ", "").replace("_", "").replace("-", "");
        for (Casa casa : values()) {
            // Comparar versión compacta de la clave (ej: "difplast" matchea "dif plast", "dif_plast")
            String claveCompacta = casa.claveLectura.replace(" ", "").replace("_", "").replace("-", "");
            if (compacto.contains(claveCompacta)) {
                return casa;
            }
            // Caso especial para "casa lema" o "casalema"
            if (casa == LEMA && (normalizado.contains("casa lema") || normalizado.contains("casalema"))) {
                return casa;
            }
        }
        return null;
    }

    /**
     * Devuelve el tipo de cálculo de precio para una lista específica.
     * Útil para sublistas de MAFERSA que tienen distintas fórmulas.
     * El nombre de lista se normaliza (espacios y guiones → '_') antes de comparar.
     */
    public static String tipoCasaParaLista(String nombreLista) {
        if (nombreLista == null) return null;
        String normalizado = CodigosListas.normalizarNombre(nombreLista.toLowerCase());
        if (normalizado.contains("lumilagro")) {
            return "lumilagro";
        }
        for (Casa casa : values()) {
            // Comparar contra el array de listas
            for (String lista : casa.listas) {
                if (CodigosListas.normalizarNombre(lista).equalsIgnoreCase(normalizado)) {
                    return casa.claveLectura;
                }
            }
            // Fallback: comparar directamente contra la claveLectura
            if (casa.claveLectura.equalsIgnoreCase(normalizado)) {
                return casa.claveLectura;
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
        String normalizado = CodigosListas.normalizarNombre(nombreLista.toLowerCase());
        for (Casa casa : values()) {
            for (String lista : casa.listas) {
                if (CodigosListas.normalizarNombre(lista).equalsIgnoreCase(normalizado)
                        || casa.claveLectura.equalsIgnoreCase(normalizado)) {
                    return casa.codigoCasa;
                }
            }
        }
        return 0;
    }
}

