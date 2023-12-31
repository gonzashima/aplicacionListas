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

public class LectorDifPlast implements LectorArchivos{

    private static final int COLUMNA_BARRA = 0;
    private static final int COLUMNA_BULTO = 3;
    private static final int MAXIMO_COLUMNAS = 7;

    private final List<Integer> indicesIgnorados;

    public LectorDifPlast() {
        indicesIgnorados = new ArrayList<>();
        indicesIgnorados.add(0); //codigo barra
        indicesIgnorados.add(3); //nombre vacio
        indicesIgnorados.add(4); //nombre vacio
        indicesIgnorados.add(5); //bulto
    }

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        File file = new File("src/main/resources/difplast.xlsx");
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        List<String> texto = new ArrayList<>();

        for (Row row : sheet) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < MAXIMO_COLUMNAS; i++) {
                Cell cell = row.getCell(i);
                if (!indicesIgnorados.contains(i)) {
                    if (!linea.isEmpty())
                        linea.append("~");
                    linea.append(formatter.formatCellValue(cell).toUpperCase());
                }
            }
            if (linea.toString().contains("$"))
                texto.add(linea.toString());
        }

        return texto;
    }
}
