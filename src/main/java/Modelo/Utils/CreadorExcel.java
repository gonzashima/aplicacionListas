package Modelo.Utils;

import Modelo.Productos.Producto;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreadorExcel {

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

    public void crearCarteles(List<Producto> productos) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        Font font = workbook.createFont();
        font.setFontHeight((short) 250);
        font.setBold(true);
        font.setUnderline((byte) 1);

        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            Row row = sheet.createRow(i);
            row.setHeight((short) 700);
            List<Cell> celdas = new ArrayList<>();

            for (int j = 0; j < 5; j++) {
                Cell celda = row.createCell(j);
                celda.setCellStyle(style);
                celdas.add(celda);
            }

            celdas.get(0).setCellValue(producto.getCodigo());
            celdas.get(1).setCellValue(producto.getNombre());
            celdas.get(2).setCellValue(producto.codigoCasa());
            celdas.get(3).setCellValue("-" + producto.getCosto());
            celdas.get(4).setCellValue("$" + producto.getPrecio());
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
}
