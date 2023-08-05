package Modelo.Utils;

import Modelo.Productos.Producto;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreadorExcel {
    private static final int MAXIMO_COLUMNAS = 5;

    /**
     * Crea un excel con los productos en formato de lista
     * */
    public void crearLista(List<Producto> productos) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue(producto.getCodigo());
            row.createCell(1).setCellValue(producto.getNombre());
            row.createCell(2).setCellValue(producto.codigoCasa());
            row.createCell(3).setCellValue("-" + producto.getCosto());
            row.createCell(4).setCellValue("$" + producto.getPrecio());
        }
        sheet.setColumnWidth(1,18_000);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel (*xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
        }
        workbook.close();
    }

    /**
     * Crea un excel con los productos como carteles
     * */
    public void crearCarteles(List<Producto> productos) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

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

        int j = 0;
        int contadorFilas = 0;
        
        Row row = null;

        for (Producto producto : productos) {
            if (j == 0) {
                row = sheet.createRow(contadorFilas);
                row.setHeight((short) 1400);
            }

            String textoCompleto = producto.stringCartel() + "$" +  producto.getPrecio();
            RichTextString richTextString = workbook.getCreationHelper().createRichTextString(textoCompleto);
            int ultimoIndicePrecio = textoCompleto.length();
            int primerIndicePrecio = ultimoIndicePrecio - String.valueOf(producto.getPrecio()).length() - 1;

            int indiceSalto = textoCompleto.indexOf("\n");

            richTextString.applyFont(primerIndicePrecio, ultimoIndicePrecio, font2);
            richTextString.applyFont(0, indiceSalto, font1);

            Cell celda = row.createCell(j);
            celda.setCellValue(richTextString);
            celda.setCellStyle(style);

            j++;

            if (j >= MAXIMO_COLUMNAS) {
                j = 0;
                contadorFilas++;
            }
        }
        for (int i = 0; i < MAXIMO_COLUMNAS; i++)
            sheet.setColumnWidth(i, 13_000);

        sheet.setPrintGridlines(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel (*xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
        }
        workbook.close();
    }
}
