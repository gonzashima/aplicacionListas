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
    private final List<String> palabrasClave;

    public LectorLema() {
        palabrasClave = new ArrayList<>();

        palabrasClave.add("ABROCHADORA MAPED");
        palabrasClave.add("ACRILICO AD");
        palabrasClave.add("ACRILICO EQARTE");
        palabrasClave.add("ADHESIVO SIMBALL");
        palabrasClave.add("ADHESIVO EZCO");
        palabrasClave.add("ACUARELA FILGO");
        palabrasClave.add("ANOTADOR CONGRESO");
        palabrasClave.add("ACCESORIO EQUARTE ");
        palabrasClave.add("APRIETAPAPEL EZCO");
        palabrasClave.add("AUTOADHESIVO ");
        palabrasClave.add("AROS ");
        palabrasClave.add("BANDA ELASTICA ");
        palabrasClave.add("BLOCK AMERICA");
        palabrasClave.add("BLOCK AVON");
        palabrasClave.add("BLOCK EXITO");
        palabrasClave.add("BLOCK TRIUNFANTE");
        palabrasClave.add("BOLIGRAFO BIC");
        palabrasClave.add("BOLIGRAFO EZCO");
        palabrasClave.add("BOLIGRAFO FABER");
        palabrasClave.add("BOLIGRAFO FILGO");
        palabrasClave.add("BOLIGRAFO MICRO");
        palabrasClave.add("BOLIGRAFO SIMBALL");
        palabrasClave.add("BOLSA GP ");
        palabrasClave.add("BRILLANTINA MINI");
        palabrasClave.add("BORRADOR ");
        palabrasClave.add("BRILLITO EZCO");
        palabrasClave.add("CARPETA ");
        palabrasClave.add("CINTA ADHESIVA STIKO");
        palabrasClave.add("CINTA EMBALAR OKA");
        palabrasClave.add("CLIPS EZCO");
        palabrasClave.add("COMPAS MAPED");
        palabrasClave.add("COLAS VINILICAS ");
        palabrasClave.add("CORRECTOR ");
        palabrasClave.add("CUADERNO EXITO");
        palabrasClave.add("CUADERNO GLORIA");
        palabrasClave.add("CUADERNO TRIUNFANTE");
        palabrasClave.add("CUADERNO LAPRIDA");
        palabrasClave.add("DETECTOR MARCADOR");
        palabrasClave.add("ESCARAPELAS ");
        palabrasClave.add("ESCUADRA MAPED");
        palabrasClave.add("ESCUADRA PIZZINI");
        palabrasClave.add("ETIQUETAS PEGASOLA");
        palabrasClave.add("FOLIOS ");
        palabrasClave.add("FUNDAS ");
        palabrasClave.add("GIRVE ");
        palabrasClave.add("GOMA EVA ");
        palabrasClave.add("GOMAS ");
        palabrasClave.add("LAMINAS PLAST LUMA");
        palabrasClave.add("LAPIZ BIC ");
        palabrasClave.add("LAPIZ FABER ");
        palabrasClave.add("LAPIZ FILGO ");
        palabrasClave.add("LAPIZ PAPER ");
        palabrasClave.add("LIBRETA NORTE ");
        palabrasClave.add("MARCADOR EZCO");
        palabrasClave.add("MARCADOR FABER");
        palabrasClave.add("MARCADOR FILGO");
        palabrasClave.add("MARCADOR SHARPIE FINO");
        palabrasClave.add("MARCADOR SIMBALL");
        palabrasClave.add("MARCADOR TRABI");
        palabrasClave.add("MASA ");
        palabrasClave.add("MICROFIBRA FILGO");
        palabrasClave.add("MICROFIBRA SIMBALL");
        palabrasClave.add("MICROFIBRA TRABI");
        palabrasClave.add("MICROFIBRA TOYO");
        palabrasClave.add("MINAS FABER");
        palabrasClave.add("MINAS PIZZINI");
        palabrasClave.add("MINAS DOZENT");
        palabrasClave.add("OJALILLOS ");
        palabrasClave.add("PAPEL AFICHE CAPITOLIO");
        palabrasClave.add("PAPEL AFICHE MURESCO");
        palabrasClave.add("PAPEL CARTULINA ALFA");
        palabrasClave.add("PAPEL CARTULINA CAPITOLIO");
        palabrasClave.add("PAPEL CELOFAN ");
        palabrasClave.add("PAPEL CREPE ");
        palabrasClave.add("PAPEL PLASTIF");
        palabrasClave.add("PAPEL GLACE");
        palabrasClave.add("PAPEL MADERA");
        palabrasClave.add("PAPEL RESMA");
        palabrasClave.add("PEGAMENTOS EZCO");
        palabrasClave.add("PEGAMENTOS UHU");
        palabrasClave.add("PEGAMENTOS VOLIGOMA");
        palabrasClave.add("PINCEL OLAMI");
        palabrasClave.add("PINCEL PICCASO");
        palabrasClave.add("PINES");
        palabrasClave.add("PINTURITAS EZCO");
        palabrasClave.add("PINTURITAS FABER");
        palabrasClave.add("PINTURITAS FILGO");
        palabrasClave.add("PINTURITAS SYLVAPEN");
        palabrasClave.add("PLASTILINA");
        palabrasClave.add("PORTAMINAS BIC");
        palabrasClave.add("REGLA MAPED");
        palabrasClave.add("REPUESTOS 1028");
        palabrasClave.add("REPUESTOS EXITO");
        palabrasClave.add("REPUESTOS GLORIA");
        palabrasClave.add("REPUESTOS RIVADAVIA");
        palabrasClave.add("REPUESTOS TRIUNFANTE");
        palabrasClave.add("RESALTADOR FILGO");
        palabrasClave.add("RESALTADOR TRABI");
        palabrasClave.add("SACAPUNTAS");
        palabrasClave.add("SOBRE MEDORO");
        palabrasClave.add("TANQUE EZCO");
        palabrasClave.add("TANQUE FILGO");
        palabrasClave.add("TANQUE SIMBALL");
        palabrasClave.add("TACO ");
        palabrasClave.add("TEMPERA MAPED");
        palabrasClave.add("TEMPERA MODEL");
        palabrasClave.add("TEMPERA PLAYCOLOR");
        palabrasClave.add("TEMPERA TINTORETTO");
        palabrasClave.add("TIJERA ");
        palabrasClave.add("TIZAS ");
        palabrasClave.add("TRANSPORTADOR ");
    }

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
                        linea.append("~");
                    linea.append(formatter.formatCellValue(celda));
                }
            }
            matcher = pattern.matcher(linea);
            if (matcher.matches() && palabrasClave.stream().anyMatch(linea.toString() :: contains))
                texto.add(linea.toString());
        }
        return texto;
    }
}
