package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.constantes.CodigosListas;
import com.listas.modelo.entity.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Leer las líneas del archivo
        List<String> lineas;
        if ("pdf".equals(extension)) {
            lineas = leerPdf(archivo);
        } else if ("xlsx".equals(extension) || "xls".equals(extension)) {
            lineas = leerExcel(archivo);
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado: " + extension);
        }

        // Parsear a productos según la casa
        List<Producto> productos = parsear(lineas, casa);

        // Insertar/actualizar en DB
        int listaId = CodigosListas.codigoLista(casa.getClaveLectura());
        int cantidad = productoService.insertarProductos(productos, listaId);

        return nombreSinExtension + " se leyó correctamente. " + cantidad + " productos procesados.";
    }

    // ==================== Lectores ====================

    private List<String> leerPdf(MultipartFile archivo) throws IOException {
        try (InputStream is = archivo.getInputStream();
             PDDocument pdf = PDDocument.load(is)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String texto = textStripper.getText(pdf);
            String[] lineas = texto.trim().split(textStripper.getLineSeparator());
            return Arrays.asList(lineas);
        }
    }

    private List<String> leerExcel(MultipartFile archivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (InputStream is = archivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                StringBuilder sb = new StringBuilder();
                for (Cell cell : row) {
                    if (!sb.isEmpty()) sb.append(" ");
                    sb.append(formatter.formatCellValue(cell).trim());
                }
                String linea = sb.toString().trim();
                if (!linea.isEmpty()) {
                    lineas.add(linea);
                }
            }
        }
        return lineas;
    }

    // ==================== Parsers ====================

    private List<Producto> parsear(List<String> lineas, Casa casa) {
        return switch (casa) {
            case TREBOL -> parsearDuravit(lineas);
            case MAFERSA -> parsearMafersa(lineas);
            case RESPONTECH, RIGOLLEAU, LEMA, RODECA, DIFPLAST -> parsearGenerico(lineas);
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
     * Parser genérico para listas simples.
     * Asume formato: CODIGO NOMBRE PRECIO por línea.
     */
    private List<Producto> parsearGenerico(List<String> lineas) {
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            String[] partes = linea.trim().split("\\s+");
            if (partes.length < 3) continue;

            try {
                int codigo = Integer.parseInt(partes[0]);
                int costo = (int) Math.round(Double.parseDouble(partes[partes.length - 1]
                        .replace("$", "").replace(",", ".")));
                StringBuilder nombre = new StringBuilder();
                for (int i = 1; i < partes.length - 1; i++) {
                    nombre.append(partes[i]);
                    if (i < partes.length - 2) nombre.append(" ");
                }
                Producto p = new Producto(codigo, nombre.toString(), costo, 100, 0);
                productos.add(p);
            } catch (NumberFormatException ignored) {
                // Línea que no matchea el formato, saltar
            }
        }
        return productos;
    }

    /**
     * Parser de Mafersa - simplificado
     */
    private List<Producto> parsearMafersa(List<String> lineas) {
        List<Producto> productos = new ArrayList<>();
        for (String linea : lineas) {
            String[] partes = linea.trim().split("\\s+");
            if (partes.length < 3) continue;

            try {
                int codigo = Integer.parseInt(partes[0].replaceAll("[^0-9]", ""));
                int costo = (int) Math.round(Double.parseDouble(partes[partes.length - 1]
                        .replace("$", "").replace(",", ".")));
                StringBuilder nombre = new StringBuilder();
                for (int i = 1; i < partes.length - 1; i++) {
                    nombre.append(partes[i]);
                    if (i < partes.length - 2) nombre.append(" ");
                }
                Producto p = new Producto(codigo, nombre.toString(), costo, 100,
                        CodigosListas.codigoLista("mafersa"));
                p.calcularPrecio("mafersa");
                productos.add(p);
            } catch (NumberFormatException ignored) {
            }
        }
        return productos;
    }
}

