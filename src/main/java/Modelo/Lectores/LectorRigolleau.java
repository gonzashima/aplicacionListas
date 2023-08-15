package Modelo.Lectores;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorRigolleau implements LectorArchivos{

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(archivo);
        XSSFWorkbook  workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet hoja = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        List<String> texto = new ArrayList<>();

        for (Row fila : hoja) {
            StringBuilder linea = new StringBuilder();
            for (Cell celda : fila) {
                if (!linea.isEmpty())
                    linea.append(":");
                linea.append(formatter.formatCellValue(celda).toUpperCase());
            }
            if (!linea.isEmpty())
                texto.add(linea.toString());
        }
        texto.remove(0);
        return texto;
    }
}
