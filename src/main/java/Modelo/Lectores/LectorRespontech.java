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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorRespontech implements LectorArchivos{
    private static final int LIMITE_CELDA = 4;
    private static final int LONGITUD_MINIMA = 5;

    private static final String REGEX = ".*\\d{1,},\\d{2}";

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(archivo);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet hoja = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        List<String> texto = new ArrayList<>();

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher;

        for (Row fila : hoja) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < LIMITE_CELDA; i++) {
                Cell celda = fila.getCell(i);
                String contenido;
                if (i == 0)
                    contenido = formatter.formatCellValue(celda).replace(" ","");
                else
                    contenido = formatter.formatCellValue(celda);

                if (!linea.isEmpty())
                    linea.append(" ");
                linea.append(contenido);
            }
            matcher = pattern.matcher(linea);
            if (matcher.matches() && linea.toString().length() > LONGITUD_MINIMA)
                texto.add(linea.toString());
        }
        return texto;
    }
}
