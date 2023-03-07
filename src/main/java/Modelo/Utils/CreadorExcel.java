package Modelo.Utils;

import Modelo.Productos.Producto;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            row.createCell(2).setCellValue("-" + producto.getCosto());
            row.createCell(3).setCellValue("$" + producto.getPrecio());
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

    public void crearCarteles() {

    }
}
