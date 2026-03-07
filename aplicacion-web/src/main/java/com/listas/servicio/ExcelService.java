package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.entity.Producto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para crear archivos Excel con productos.
 * Migrado desde CreadorExcel.java del proyecto original.
 */
@Service
public class ExcelService {

    /**
     * Crea un Excel en formato lista con los productos dados.
     * @return bytes del archivo Excel
     */
    public byte[] crearLista(List<Producto> productos) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Productos");

            // Data — sin header, igual que el original CreadorExcel.crearLista()
            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(p.getCodigo());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(Casa.codigoCasaPorListaId(p.getListaId()));
                row.createCell(3).setCellValue("-" + p.getCosto());
                row.createCell(4).setCellValue("$" + p.getPrecio());
            }

            sheet.setColumnWidth(1, 18_000);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Crea un Excel con formato de carteles para imprimir.
     * @return bytes del archivo Excel
     */
    public byte[] crearCarteles(List<Producto> productos) throws IOException {
        int maxColumnas = 5;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Carteles");

            Font font1 = workbook.createFont();
            font1.setFontHeight((short) 300);
            font1.setBold(true);

            Font font2 = workbook.createFont();
            font2.setFontHeight((short) 600);
            font2.setBold(true);

            Font font3 = workbook.createFont();
            font3.setFontHeight((short) 250);

            CellStyle style = workbook.createCellStyle();
            style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font3);

            int col = 0;
            int fila = 0;
            Row row = null;

            for (Producto producto : productos) {
                if (col == 0) {
                    row = sheet.createRow(fila);
                    row.setHeight((short) 1400);
                }

                // Obtener el código de casa real desde el enum según el listaId
                int codigoCasa = Casa.codigoCasaPorListaId(producto.getListaId());

                String textoCompleto = producto.getCodigo() + " " + producto.getNombre() + "\n"
                        + codigoCasa + "-" + producto.getCosto() + "\n"
                        + "$" + producto.getPrecio();

                RichTextString richText = workbook.getCreationHelper().createRichTextString(textoCompleto);

                // Aplicar fonts igual que el original
                int indiceSalto = textoCompleto.indexOf("\n");
                int ultimoIndicePrecio = textoCompleto.length();
                int primerIndicePrecio = ultimoIndicePrecio - String.valueOf(producto.getPrecio()).length() - 1;

                richText.applyFont(0, indiceSalto, font1);
                richText.applyFont(primerIndicePrecio, ultimoIndicePrecio, font2);

                Cell celda = row.createCell(col);
                celda.setCellValue(richText);
                celda.setCellStyle(style);

                col++;
                if (col >= maxColumnas) {
                    col = 0;
                    fila++;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}

