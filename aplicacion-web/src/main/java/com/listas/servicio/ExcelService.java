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

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Código");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Costo");
            header.createCell(3).setCellValue("Precio");
            header.createCell(4).setCellValue("Porcentaje");

            // Bold header
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            for (int i = 0; i < 5; i++) {
                header.getCell(i).setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(p.getCodigo());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getCosto());
                row.createCell(3).setCellValue(p.getPrecio());
                row.createCell(4).setCellValue(p.getPorcentaje() + "%");
            }

            sheet.setColumnWidth(1, 18_000);
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);

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

