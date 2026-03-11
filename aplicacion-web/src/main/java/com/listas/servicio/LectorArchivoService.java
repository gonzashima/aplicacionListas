package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.constantes.CodigosListas;
import com.listas.modelo.entity.Producto;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Servicio que se encarga de leer archivos (PDF/Excel) y parsearlos a productos.
 * Migrado desde los paquetes Lectores y Parsers del proyecto original.
 */
@Service
public class LectorArchivoService {

    private final ProductoService productoService;

    public LectorArchivoService(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Lee un archivo subido, determina a qué casa pertenece, parsea los productos
     * y los inserta/actualiza en la base de datos.
     *
     * @return mensaje con el resultado de la operación
     */
    public String procesarArchivo(MultipartFile archivo) throws Exception {
        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            throw new IllegalArgumentException("El archivo no tiene nombre");
        }

        String nombreSinExtension = nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")).toLowerCase();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();

        Casa casa = Casa.buscarPorNombreArchivo(nombreSinExtension);
        if (casa == null) {
            throw new IllegalArgumentException("No se pudo determinar la casa del archivo: " + nombreOriginal);
        }

        // Leer las líneas del archivo según la casa
        List<String> lineas;
        if ("pdf".equals(extension)) {
            lineas = leerPdf(archivo, casa);
        } else if ("xlsx".equals(extension) || "xls".equals(extension)) {
            lineas = leerExcel(archivo, casa);
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado: " + extension);
        }

        // Parsear a productos según la casa
        List<Producto> productos = parsear(lineas, casa);

        // Mafersa: cada producto ya tiene su lista_id correcto (sublista),
        // se insertan agrupados por lista_id
        int cantidad;
        if (casa == Casa.MAFERSA) {
            cantidad = productoService.insertarProductosMafersa(productos);
        } else {
            int listaId = CodigosListas.codigoLista(casa.getClaveLectura());
            cantidad = productoService.insertarProductos(productos, listaId);
        }

        return nombreSinExtension + " se leyó correctamente. " + cantidad + " productos procesados.";
    }

    // ==================== Lectores ====================

    // Constantes de líneas de Mafersa (igual que LectorMafersa.java original)
    private static final List<String> LINEAS_MAFERSA = List.of(
            "051A -", "051G -", "A1 -", "034V1B -", "034V1 -", "06A2B -",
            "06A4 -", "06CH -", "06CI -", "06E -", "06E5 -", "06E6 -",
            "10A -", "26KY -", "033T -", "03AB1 -", "03AB2 -", "1100 -",
            "1101 -", "1103 -", "11K -", "121AL -", "121AN -", "09 -",
            "091 -", "18L -", "21001 -", "21002 -", "21004 -", "21008 -",
            "21009 -", "21C -", "22MF -", "23MC -", "24C -", "24C1 -",
            "25D1 -", "25D4 -", "30PH -", "353 -"
    );

    // Códigos de rubros de Mafersa que sí se trabajan (de ConstantesStrings.getCodigosMafersa())
    private static final List<String> CODIGOS_MAFERSA = List.of(
            "02A -", "02BG -", "03NA -", "03T -", "03W -", "06C -",
            "10A -", "11 -", "11K -", "12D -", "16B -", "18L -",
            "21 -", "22 -", "23M -", "24C -", "24C1 -", "25D -",
            "30P -", "35Y -"
    );

    private static final String MAFERSA_REGEX = "^(\\S+(?:\\s+\\S+)*?)\\s*\\|\\s*(\\S+(?:\\s+\\S+)*?)$";

    private List<String> leerPdf(MultipartFile archivo, Casa casa) throws IOException {
        try (InputStream is = archivo.getInputStream();
             PDDocument pdf = PDDocument.load(is)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String textoOriginal = textStripper.getText(pdf);
            String[] lineasTexto = textoOriginal.trim().split(textStripper.getLineSeparator());

            if (casa == Casa.RODECA) {
                List<String> excluidas = List.of("RODECA", "NUEVO", ".COM.AR", "/", "POWERED", "MEDIDAS", "BULTO");
                List<String> resultado = new ArrayList<>();
                for (String linea : lineasTexto) {
                    String upper = linea.toUpperCase();
                    if (excluidas.stream().noneMatch(upper::contains) && !upper.isBlank())
                        resultado.add(upper);
                }
                return resultado;
            }

            if (casa == Casa.MAFERSA) {
                return leerPdfMafersa(lineasTexto);
            }

            return Arrays.asList(lineasTexto);
        }
    }

    /**
     * Reproduce exactamente la lógica de LectorMafersa.leerArchivo():
     * 1. Filtra términos de encabezado
     * 2. Registra índices de rubros y líneas
     * 3. Recorta al primer rubro
     * 4. filtrarRubros: elimina rubros que no se trabajan
     * 5. filtrarLineas: elimina líneas que no se trabajan
     * 6. Normaliza espacios
     * 7. Divide líneas con | en 2 productos (MAFERSA_REGEX)
     * 8. Limpia | restantes
     */
    private List<String> leerPdfMafersa(String[] lineasTexto) {
        List<String> texto = new ArrayList<>();
        List<Integer> indicesRubros = new ArrayList<>();
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

        if (indicesRubros.isEmpty()) return texto;

        int indicePrimerRubro = indicesRubros.get(0);
        indicesLineas = indicesLineas.stream().map(x -> x - indicePrimerRubro).collect(Collectors.toList());
        texto.subList(0, indicePrimerRubro).clear();
        indicesRubros = indicesRubros.stream().map(r -> r - indicePrimerRubro).collect(Collectors.toList());

        List<List<Integer>> resultado = filtrarRubrosMafersa(indicesRubros, indicesLineas, texto);
        indicesRubros = resultado.get(0);
        indicesLineas = resultado.get(1);

        filtrarLineasMafersa(indicesRubros, indicesLineas, texto);

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

    private List<List<Integer>> filtrarRubrosMafersa(List<Integer> indicesRubros, List<Integer> indicesLineas, List<String> texto) {
        for (int i = 0; i < indicesRubros.size(); i++) {
            int indice = indicesRubros.get(i);
            String rubro = texto.get(indice);
            int tamanioAntes = texto.size();

            if (CODIGOS_MAFERSA.stream().noneMatch(rubro::contains)) {
                if ((i + 1) >= indicesRubros.size()) {
                    indicesLineas = indicesLineas.stream().filter(x -> !(x >= indice && x < texto.size())).collect(Collectors.toList());
                    texto.subList(indice, texto.size()).clear();
                } else {
                    int finalDeSublista = indicesRubros.get(i + 1);
                    indicesLineas = indicesLineas.stream().filter(x -> !(x >= indice && x < finalDeSublista)).collect(Collectors.toList());
                    texto.subList(indice, finalDeSublista).clear();
                }
            }
            int diferencia = tamanioAntes - texto.size();

            if (diferencia != 0) {
                indicesRubros.remove(i);

                List<Integer> rubrosSalteados = indicesRubros.subList(0, i);
                List<Integer> rubrosAModificar = indicesRubros.subList(i, indicesRubros.size());

                List<Integer> rubrosResultantes = rubrosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                rubrosResultantes.addAll(0, rubrosSalteados);
                indicesRubros = rubrosResultantes;

                if (i + 1 <= indicesRubros.size())
                    indicesLineas = indicesLineas.stream().map(x -> (x > indice) ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
        return new ArrayList<>(Arrays.asList(indicesRubros, indicesLineas));
    }

    private void filtrarLineasMafersa(List<Integer> indicesRubros, List<Integer> indicesLineas, List<String> texto) {
        for (int i = 0; i < indicesLineas.size(); i++) {
            int indice = indicesLineas.get(i);
            String leida = texto.get(indice);
            int tamanioAntes = texto.size();

            if (LINEAS_MAFERSA.stream().noneMatch(leida::contains)) {
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

                List<Integer> elementosSalteados = indicesLineas.subList(0, i);
                List<Integer> elementosAModificar = indicesLineas.subList(i, indicesLineas.size());

                List<Integer> listaResultante = elementosAModificar.stream().map(e -> e - diferencia).collect(Collectors.toList());

                listaResultante.addAll(0, elementosSalteados);
                indicesLineas = listaResultante;

                indicesRubros = indicesRubros.stream().map(x -> x > indice ? (x - diferencia) : x).collect(Collectors.toList());

                i--;
            }
        }
    }



    private List<String> leerExcel(MultipartFile archivo, Casa casa) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (InputStream is = archivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            if (casa == Casa.RIGOLLEAU) {
                // Rigolleau: une celdas con ':', saltea la primera fila (header)
                boolean primera = true;
                for (Row row : sheet) {
                    if (primera) { primera = false; continue; }
                    StringBuilder sb = new StringBuilder();
                    for (Cell cell : row) {
                        if (!sb.isEmpty()) sb.append(":");
                        sb.append(formatter.formatCellValue(cell).toUpperCase());
                    }
                    if (!sb.isEmpty()) lineas.add(sb.toString());
                }
            } else if (casa == Casa.DIFPLAST) {
                // DifPlast: 7 columnas (0-6), ignora cols 0 (cód. barras), 3, 5 (vacías/bulto).
                // Columnas leídas: 1 (código), 2 (nombre), 4 (precio) → unidas con '~'.
                // El precio (col 4) se lee como número directo si la celda es numérica
                // para evitar que celdas sin formato moneda no tengan '$' y se filtren mal.
                // Filtro: código (col 1) debe ser numérico y precio debe ser > 0.
                for (Row row : sheet) {
                    Cell celdaCodigo = row.getCell(1);
                    Cell celdaNombre  = row.getCell(2);
                    Cell celdaPrecio  = row.getCell(4);

                    if (celdaCodigo == null || celdaPrecio == null) continue;

                    String codigoStr = formatter.formatCellValue(celdaCodigo).trim();
                    if (codigoStr.isBlank()) continue;
                    try { Integer.parseInt(codigoStr); } catch (NumberFormatException e) { continue; }

                    String nombreStr = formatter.formatCellValue(celdaNombre).toUpperCase().trim();

                    // Precio: si es numérica tomamos el valor directo para evitar problemas de formato
                    String precioStr;
                    if (celdaPrecio.getCellType() == CellType.NUMERIC) {
                        long precioLong = (long) celdaPrecio.getNumericCellValue();
                        if (precioLong <= 0) continue;
                        precioStr = String.valueOf(precioLong);
                    } else {
                        precioStr = formatter.formatCellValue(celdaPrecio).trim();
                        if (precioStr.isBlank()) continue;
                    }

                    lineas.add(codigoStr + "~" + nombreStr + "~" + precioStr);
                }
            } else if (casa == Casa.LEMA) {
                // Lema: .xls, 4 columnas — col 0: código interno, col 1: código barras (ignorar),
                // col 2: nombre, col 3: precio.
                // Formato línea: CODIGO~NOMBRE~PRECIO  — igual que LectorLema.java del legacy.
                // La celda de precio se lee como número directo para evitar problemas de formato.
                List<String> palabrasClave = List.of(
                        "ABROCHADORA MAPED", "ACRILICO AD", "ACRILICO EQARTE",
                        "ADHESIVO SIMBALL", "ADHESIVO EZCO", "ACUARELA FILGO",
                        "ANOTADOR CONGRESO", "ACCESORIO EQUARTE ", "APRIETAPAPEL EZCO",
                        "AUTOADHESIVO ", "AROS ", "BANDA ELASTICA ",
                        "BLOCK AMERICA", "BLOCK AVON", "BLOCK EXITO", "BLOCK TRIUNFANTE",
                        "BOLIGRAFO BIC", "BOLIGRAFO EZCO", "BOLIGRAFO FABER",
                        "BOLIGRAFO FILGO", "BOLIGRAFO MICRO", "BOLIGRAFO SIMBALL",
                        "BOLSA GP ", "BRILLANTINA MINI", "BORRADOR ", "BRILLITO EZCO",
                        "CARPETA ", "CINTA ADHESIVA STIKO", "CINTA EMBALAR OKA",
                        "CLIPS EZCO", "COMPAS MAPED", "COLAS VINILICAS ", "CORRECTOR ",
                        "CUADERNO EXITO", "CUADERNO GLORIA", "CUADERNO TRIUNFANTE",
                        "CUADERNO LAPRIDA", "DETECTOR MARCADOR", "ESCARAPELAS ",
                        "ESCUADRA MAPED", "ESCUADRA PIZZINI", "ETIQUETAS PEGASOLA",
                        "FOLIOS ", "FUNDAS ", "GIRVE ", "GOMA EVA ", "GOMAS ",
                        "LAMINAS PLAST LUMA", "LAPIZ BIC ", "LAPIZ FABER ",
                        "LAPIZ FILGO ", "LAPIZ PAPER ", "LIBRETA NORTE ",
                        "MARCADOR EZCO", "MARCADOR FABER", "MARCADOR FILGO",
                        "MARCADOR SHARPIE FINO", "MARCADOR SIMBALL", "MARCADOR TRABI",
                        "MASA ", "MICROFIBRA FILGO", "MICROFIBRA SIMBALL",
                        "MICROFIBRA TRABI", "MICROFIBRA TOYO",
                        "MINAS FABER", "MINAS PIZZINI", "MINAS DOZENT", "OJALILLOS ",
                        "PAPEL AFICHE CAPITOLIO", "PAPEL AFICHE MURESCO",
                        "PAPEL CARTULINA ALFA", "PAPEL CARTULINA CAPITOLIO",
                        "PAPEL CELOFAN ", "PAPEL CREPE ", "PAPEL PLASTIF",
                        "PAPEL GLACE", "PAPEL MADERA", "PAPEL RESMA",
                        "PEGAMENTOS EZCO", "PEGAMENTOS UHU", "PEGAMENTOS VOLIGOMA",
                        "PINCEL OLAMI", "PINCEL PICCASO", "PINES",
                        "PINTURITAS EZCO", "PINTURITAS FABER", "PINTURITAS FILGO",
                        "PINTURITAS SYLVAPEN", "PLASTILINA",
                        "PORTAMINAS BIC", "REGLA MAPED",
                        "REPUESTOS 1028", "REPUESTOS EXITO", "REPUESTOS GLORIA",
                        "REPUESTOS RIVADAVIA", "REPUESTOS TRIUNFANTE",
                        "RESALTADOR FILGO", "RESALTADOR TRABI", "SACAPUNTAS",
                        "SOBRE MEDORO", "TANQUE EZCO", "TANQUE FILGO", "TANQUE SIMBALL",
                        "TACO ", "TEMPERA MAPED", "TEMPERA MODEL",
                        "TEMPERA PLAYCOLOR", "TEMPERA TINTORETTO",
                        "TIJERA ", "TIZAS ", "TRANSPORTADOR "
                );
                for (Row row : sheet) {
                    // col 0: código interno
                    Cell celdaCodigo = row.getCell(0);
                    // col 1: código barras — ignorada
                    // col 2: nombre
                    Cell celdaNombre = row.getCell(2);
                    // col 3: precio
                    Cell celdaPrecio = row.getCell(3);

                    if (celdaCodigo == null || celdaPrecio == null) continue;

                    // Leer código como número directo para evitar "12345.0"
                    String codigoStr;
                    if (celdaCodigo.getCellType() == CellType.NUMERIC) {
                        codigoStr = String.valueOf((long) celdaCodigo.getNumericCellValue());
                    } else {
                        codigoStr = formatter.formatCellValue(celdaCodigo);
                    }

                    String nombreStr = formatter.formatCellValue(celdaNombre);

                    // Precio: si la celda es numérica, leer el valor directo para evitar
                    // problemas de formato (igual que se hizo con DifPlast y Respontech).
                    String precioStr;
                    if (celdaPrecio.getCellType() == CellType.NUMERIC) {
                        long precioLong = (long) celdaPrecio.getNumericCellValue();
                        if (precioLong <= 0) continue;
                        precioStr = String.valueOf(precioLong);
                    } else {
                        precioStr = formatter.formatCellValue(celdaPrecio).trim();
                        if (precioStr.isBlank()) continue;
                    }

                    if (codigoStr.isBlank()) continue;

                    String lineaStr = codigoStr + "~" + nombreStr + "~" + precioStr;

                    // Filtrar por palabras clave — comparación en mayúsculas igual que LectorLema.java
                    String lineaMayus = lineaStr.toUpperCase();
                    if (palabrasClave.stream().anyMatch(lineaMayus::contains)) {
                        lineas.add(lineaStr);
                    }
                }
            } else if (casa == Casa.RESPONTECH) {
                // Respontech: 4 celdas, col 0 sin espacios, une con espacio.
                // Replica LectorRespontech.java del original.
                // El precio (col 3) se lee SIEMPRE con DataFormatter para preservar el formato
                // "$1.475,00" y que parsearCosto pueda manejar correctamente miles/decimales.
                // Filtro REGEX igual al legacy: línea debe terminar en X,XX (número con coma decimal)
                int maxCol = 4;
                Pattern regexRespontech = Pattern.compile(".*\\d{1,},\\d{2}");
                for (Row row : sheet) {
                    // Construir línea con cols 0-2 (código, nombre, unidad)
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < maxCol - 1; i++) {
                        Cell cell = row.getCell(i);
                        String contenido;
                        if (i == 0) {
                            contenido = formatter.formatCellValue(cell).replace(" ", "");
                        } else {
                            contenido = formatter.formatCellValue(cell);
                        }
                        if (!sb.isEmpty()) sb.append(" ");
                        sb.append(contenido);
                    }
                    // Col 3: precio — usar SIEMPRE el formatter para preservar formato de miles
                    Cell celdaPrecio = row.getCell(maxCol - 1);
                    if (celdaPrecio == null) continue;
                    String precioStr = formatter.formatCellValue(celdaPrecio);
                    if (precioStr.isBlank()) continue;
                    sb.append(" ").append(precioStr);
                    String linea = sb.toString();
                    // Mismo filtro del legacy: la línea debe terminar en X,XX y tener longitud mínima
                    if (regexRespontech.matcher(linea).matches() && linea.length() > 5) {
                        lineas.add(linea);
                    }
                }
            } else {
                // Genérico: une con espacio
                for (Row row : sheet) {
                    StringBuilder sb = new StringBuilder();
                    for (Cell cell : row) {
                        if (!sb.isEmpty()) sb.append(" ");
                        sb.append(formatter.formatCellValue(cell).trim());
                    }
                    String linea = sb.toString().trim();
                    if (!linea.isEmpty()) lineas.add(linea);
                }
            }
        }
        return lineas;
    }

    // ==================== Parsers ====================

    private List<Producto> parsear(List<String> lineas, Casa casa) {
        return switch (casa) {
            case TREBOL    -> parsearDuravit(lineas);
            case MAFERSA   -> parsearMafersa(lineas);
            case RESPONTECH -> parsearRespontech(lineas);
            case RIGOLLEAU -> parsearRigolleau(lineas);
            case LEMA      -> parsearLema(lineas);
            case RODECA    -> parsearRodeca(lineas);
            case DIFPLAST  -> parsearDifPlast(lineas);
        };
    }

    /**
     * Parser de Duravit - migrado de ParserDuravit.java
     */
    private List<Producto> parsearDuravit(List<String> lineas) {
        String patron = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";
        Pattern pattern = Pattern.compile(patron);
        List<Producto> productos = new ArrayList<>();

        for (String linea : lineas) {
            Matcher matcher = pattern.matcher(linea);
            if (!matcher.matches()) continue;

            String[] partes = linea.trim().split("\\s+");
            List<String> palabras = new ArrayList<>(Arrays.asList(partes));
            palabras.removeIf(String::isEmpty);

            int tam = palabras.size();
            if (tam < 3) continue;

            StringBuilder nombre = new StringBuilder();
            for (int i = 0; i < tam - 2; i++) {
                nombre.append(palabras.get(i));
                if (i != tam - 3) nombre.append(" ");
            }
            int codigo = Integer.parseInt(palabras.get(tam - 2));
            int costo = (int) Math.round(Double.parseDouble(palabras.get(tam - 1)));

            Producto p = new Producto(codigo, nombre.toString(), costo, 100,
                    CodigosListas.codigoLista("duravit"));
            p.calcularPrecio("duravit");
            productos.add(p);
        }
        return productos;
    }

    /**
     * Respontech: separado por espacios. Código al inicio (numérico o alfanumérico).
     * Si el código no es numérico, se genera un hash SHA-256 → int de 6 dígitos.
     * Formato por línea: CODIGO ... NOMBRE ... UNIDAD COSTO
     * El penúltimo token (unidad de medida) se descarta — igual que ParserRespontech.java del legacy.
     * Soporta costo con formato "$1.234,56" (Excel con formato moneda) y "2500" o "2500.50" (celda numérica).
     */
    private List<Producto> parsearRespontech(List<String> lineas) {
        int listaId = CodigosListas.codigoLista("respontech");
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            String[] arr = linea.trim().split("\\s+");
            List<String> palabras = new ArrayList<>(Arrays.asList(arr));
            palabras.removeIf(String::isEmpty);
            if (palabras.size() < 3) continue;
            try {
                String codigoStr = palabras.remove(0);
                String costoRaw = palabras.remove(palabras.size() - 1);
                palabras.remove(palabras.size() - 1); // descartar unidad de medida (penúltimo)

                int costo = parsearCosto(costoRaw);

                String nombre = String.join(" ", palabras).toUpperCase();
                int codigo;
                try {
                    codigo = Integer.parseInt(codigoStr);
                } catch (NumberFormatException e) {
                    // Código alfanumérico → SHA-256 hash → int de 6 dígitos (igual que el original)
                    String hash = DigestUtils.sha256Hex(codigoStr);
                    codigo = Math.abs(hash.hashCode()) % 1_000_000;
                }
                Producto p = new Producto(codigo, nombre, costo, 100, listaId);
                p.calcularPrecio("respontech");
                productos.add(p);
            } catch (Exception ignored) {}
        }
        return productos;
    }

    /**
     * Parsea un string de costo a entero, soportando múltiples formatos:
     * - "$1.475,00"   → 1475  (formato moneda argentino: punto=miles, coma=decimal)
     * - "$ 1.475,00"  → 1475  (con espacio después del $)
     * - "1.475"       → 1475  (DataFormatter con miles, sin decimales)
     * - "1475"        → 1475  (número puro)
     * - "1475.50"     → 1475  (decimal con punto anglosajón)
     *
     * Regla: si hay coma → formato argentino (punto=miles, coma=decimal).
     *        si solo hay punto y 3 dígitos finales → separador de miles.
     *        si solo hay punto y 2 dígitos finales → decimal → tomar parte entera.
     */
    private int parsearCosto(String costoRaw) {
        // Quitar $ y espacios
        String s = costoRaw.replace("$", "").trim();
        if (s.contains(",")) {
            // Formato argentino: "1.475,00" → quitar puntos de miles → "1475,00" → parte entera
            s = s.replace(".", "").split(",")[0].trim();
        } else if (s.contains(".")) {
            String[] partes = s.split("\\.");
            String fraccion = partes[partes.length - 1];
            if (fraccion.length() == 3) {
                // "1.475" → el punto es separador de miles → eliminar todos los puntos
                s = s.replace(".", "");
            } else {
                // "1475.50" → el punto es separador decimal → tomar solo parte entera
                s = partes[0];
            }
        }
        return Integer.parseInt(s.trim());
    }

    /**
     * Rigolleau: separado por ':'.
     * Formato: CODIGO:NOMBRE:CAPACIDAD:UNIDADES_BULTO:COSTO:ACLARACIONES...
     */
    private List<Producto> parsearRigolleau(List<String> lineas) {
        int listaId = CodigosListas.codigoLista("rigolleau");
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            List<String> partes = new ArrayList<>(Arrays.asList(linea.split(":")));
            if (partes.size() < 5) continue;
            try {
                int codigo = Integer.parseInt(partes.remove(0).trim());
                // nombre = nombre + capacidad
                StringBuilder nombre = new StringBuilder(partes.remove(0).trim())
                        .append(" ").append(partes.remove(0).trim().replace(" ", ""));
                partes.remove(0); // unidades por bulto
                String costoStr = partes.remove(0).replace("$", "").replace(",", ".").replace(" ", "");
                int costo = Integer.parseInt(costoStr.split("\\.")[0]);
                // resto son aclaraciones, las agregamos al nombre
                for (String s : partes) {
                    if (!s.isBlank()) nombre.append(" ").append(s.trim());
                }
                Producto p = new Producto(codigo, nombre.toString().toUpperCase(), costo, 100, listaId);
                p.calcularPrecio("rigolleau");
                productos.add(p);
            } catch (NumberFormatException ignored) {}
        }
        return productos;
    }

    /**
     * Lema: separado por '~'.
     * Formato: CODIGO~NOMBRE~COSTO  — igual que ParserLema.java del legacy.
     * El código puede venir como "12345" o "12345.0" si la celda es numérica.
     */
    private List<Producto> parsearLema(List<String> lineas) {
        int listaId = CodigosListas.codigoLista("lema");
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            List<String> partes = new ArrayList<>(Arrays.asList(linea.trim().split("~")));
            if (partes.size() < 3) continue;
            try {
                // El código puede venir como "12345" o "12345.0" (celda numérica)
                String codigoRaw = partes.remove(0).trim().split("\\.")[0];
                int codigo = Integer.parseInt(codigoRaw);
                int costo = parsearCosto(partes.remove(partes.size() - 1));
                StringBuilder nombre = new StringBuilder();
                for (String parte : partes) {
                    if (!nombre.isEmpty()) nombre.append(" ");
                    nombre.append(parte.trim());
                }
                Producto p = new Producto(codigo, nombre.toString().toUpperCase(), costo, 100, listaId);
                p.calcularPrecio("lema");
                productos.add(p);
            } catch (Exception ignored) {}
        }
        return productos;
    }

    /**
     * Rodeca: bloques de 3 líneas (nombre, "Código: XXX", "$costo").
     * El código es alfanumérico → hash SHA-256 → int de 6 dígitos.
     */
    private List<Producto> parsearRodeca(List<String> lineas) {
        int listaId = CodigosListas.codigoLista("rodeca");
        List<Producto> productos = new ArrayList<>();
        for (int i = 0; i + 2 < lineas.size(); i += 3) {
            try {
                String nombre = lineas.get(i).trim();
                String codigoStr = lineas.get(i + 1).split(":")[1].trim();
                String costoStr = lineas.get(i + 2).replace("$", "").replace(".", "").replace(",", ".");
                String hash = DigestUtils.sha256Hex(codigoStr);
                int codigo = Math.abs(hash.hashCode()) % 1_000_000;
                int costo = (int) Math.round(Double.parseDouble(costoStr.trim()));
                Producto p = new Producto(codigo, nombre.toUpperCase(), costo, 100, listaId);
                p.calcularPrecio("rodeca");
                productos.add(p);
            } catch (Exception ignored) {}
        }
        return productos;
    }

    /**
     * DifPlast: separado por '~' (desde leerExcel con casa DIFPLAST).
     * Formato: CODIGO~NOMBRE~COSTO  (columnas 1, 2, 6 del Excel)
     * Migrado de ParserDifPlast.java del original.
     */
    private List<Producto> parsearDifPlast(List<String> lineas) {
        int listaId = CodigosListas.codigoLista("difplast");
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            String[] partes = linea.trim().split("~");
            if (partes.length < 3) continue;
            try {
                String codigoStr = partes[0].trim();
                String nombre = partes[1].trim();
                // Costo: puede venir como "$1.475,00" o "1475" o "1.475"
                String costoStr = partes[2];

                if (codigoStr.isEmpty() || costoStr.isEmpty()) continue;
                int codigo = Integer.parseInt(codigoStr);
                int costo = parsearCosto(costoStr);
                Producto p = new Producto(codigo, nombre.toUpperCase(), costo, 100, listaId);
                p.calcularPrecio("difplast");
                productos.add(p);
            } catch (NumberFormatException e) {
                // Línea de encabezado o fila inválida — ignorar
            }
        }
        return productos;
    }

    /**
     * Parser de Mafersa - reproduce la lógica real del original (ParserMafersa.java).
     * El PDF de Mafersa agrupa productos por "RUBRO : nombre_lista".
     * Cada rubro corresponde a una sublista distinta con su propio lista_id.
     * Los productos de Lumilagro con nombres especiales usan fórmula de IVA completo.
     */
    private List<Producto> parsearMafersa(List<String> lineas) {
        // Nombres originales de las sublistas de Mafersa — igual que getNombresMafersa() del legacy
        List<String> nombres = Casa.MAFERSA.getListas();

        List<String> distintos = List.of(
                "LUMINOX", "REPUESTO TERMO", "REPUESTO COMPACTO",
                "TAPON CEBADOR", "TAPON ESPIRAL", "TAPON P/TERMO",
                "TAPON P/JARRA", "TAPON PICO", "BOCA",
                "RIVER", "INDEPENDIENTE", "RACING", "ESTUDIANTES",
                "GIMNASIA", "SAN LORENZO", "SELECCION"
        );

        // separarRubros — copia exacta del original
        Map<String, List<String>> rubrosSeparados = new LinkedHashMap<>();
        List<String> nuevaLista = null;

        for (String linea : lineas) {
            linea = linea.toLowerCase();
            if (linea.contains("rubro :")) {
                nuevaLista = new ArrayList<>();
                Optional<String> nombreLista = nombres.stream().filter(linea::contains).findFirst();
                if (nombreLista.isPresent())
                    rubrosSeparados.put(CodigosListas.normalizarNombre(nombreLista.get()), nuevaLista);
            } else if (!linea.contains("linea :")) {
                if (nuevaLista != null)
                    nuevaLista.add(linea.toUpperCase());
            }
        }

        List<Producto> productos = new ArrayList<>();

        for (String nombreLista : nombres) {
            String nombreUnido = CodigosListas.normalizarNombre(nombreLista);
            List<String> lista = rubrosSeparados.get(nombreUnido);

            if (lista == null) continue;

            int listaId;
            try {
                listaId = CodigosListas.codigoLista(nombreUnido);
            } catch (IllegalArgumentException e) {
                continue;
            }

            for (String linea : lista) {
                String[] lineaSeparada = linea.trim().split(" ");
                List<String> partes = new ArrayList<>(Arrays.asList(lineaSeparada));

                try {
                    partes.remove(1); // saco el codigo del fabricante

                    int codigo = Integer.parseInt(partes.remove(0));

                    String costoString = partes.get(partes.size() - 1);
                    if (costoString.contains(","))
                        costoString = costoString.replace(",", ".");

                    int costo = (int) Math.round(Double.parseDouble(costoString));
                    partes.remove(partes.size() - 1);
                    String nombre = String.join(" ", partes);

                    boolean esLumilagro = distintos.stream().anyMatch(nombre::contains);
                    String tipoCasa = esLumilagro ? "lumilagro" : "mafersa";

                    Producto p = new Producto(codigo, nombre, costo, 100, listaId);
                    p.calcularPrecio(tipoCasa);
                    productos.add(p);
                } catch (Exception ignored) {}
            }
        }
        return productos;
    }
}

