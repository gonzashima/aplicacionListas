package Modelo.Lectores;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorLema implements LectorArchivos{

    private static final String REGEX = ".*\\d{1,},\\d{2}";
    private static final int MAXIMO_COLUMNAS = 4;
    private static final int COLUMNA_CODIGO_BARRAS = 1;

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        File archivoLema = new File("src/main/resources/Lista-de-precios-CasaLema.xls");
        FileInputStream fileInputStream = new FileInputStream(archivoLema);

        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        HSSFSheet hoja = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        List<String> texto = new ArrayList<>();

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher;

        for (Row fila : hoja) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < MAXIMO_COLUMNAS; i++) {
                Cell celda = fila.getCell(i);
                if (i != COLUMNA_CODIGO_BARRAS) {
                    if (!linea.isEmpty())
                        linea.append(" ");
                    linea.append(formatter.formatCellValue(celda));
                }
            }
            matcher = pattern.matcher(linea);
            if (matcher.matches())
                texto.add(linea.toString());
        }
        return texto;
    }
}
