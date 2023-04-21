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

public class LectorRespontech implements LectorArchivos{
    private static final int LIMITE_CELDA = 4;
    private static final int LONGITUD_MINIMA = 5;

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(archivo);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet hoja = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        List<String> texto = new ArrayList<>();

        for (Row fila : hoja) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < LIMITE_CELDA; i++) {
                Cell celda = fila.getCell(i);
                String contenido;
                if (i == 0)
                    contenido = formatter.formatCellValue(celda).replace(" ","");
                else
                    contenido = formatter.formatCellValue(celda);

                if (linea.length() > 0)
                    linea.append(" ");
                linea.append(contenido);
            }
            if (!linea.isEmpty() && linea.toString().contains("$") && linea.toString().length() > LONGITUD_MINIMA)
                texto.add(linea.toString());
        }
        return texto;
    }
}
