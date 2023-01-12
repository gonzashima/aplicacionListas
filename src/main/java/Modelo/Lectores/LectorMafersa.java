package Modelo.Lectores;

import Modelo.Productos.Producto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class LectorMafersa implements LectorArchivos{

    @Override
    public HashMap<Integer, Producto> leerArchivo(File archivo, HashMap<Integer, Producto> mapaProductos) throws IOException {
        if (mapaProductos == null)
            mapaProductos = new HashMap<>();

        FileInputStream file = new FileInputStream("src/main/resources/lista-precios-mafersa-06.01.23.xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet hoja = workbook.getSheetAt(0);

        Iterator<Row> iteradorFilas = hoja.iterator();
        Iterator<Cell> iteradorCeldas;
        Row fila;
        Cell celda;

        while (iteradorFilas.hasNext()) {
            fila = iteradorFilas.next();
            iteradorCeldas = fila.cellIterator();

            while (iteradorCeldas.hasNext()) {
                celda = iteradorCeldas.next();

                switch (celda.getCellType()) {
                    case NUMERIC -> System.out.print(celda.getNumericCellValue() + " ");
                    case STRING -> System.out.print(celda.getStringCellValue() + " ");
                }
            }
            System.out.println();
        }

        workbook.close();

        return null;
    }

    @Override
    public String nombreTabla() {
        return "mafersa";
    }
}
