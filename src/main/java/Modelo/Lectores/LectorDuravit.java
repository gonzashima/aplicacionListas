package Modelo.Lectores;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorDuravit implements LectorArchivos{

    private static final String PATRON_DURAVIT = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";

    @Override
    public List<String> leerArchivo(File archivo) throws IOException{
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();

        String textoOriginal = String.valueOf(textStripper.getText(pdf));

        String[] lineasTexto = textoOriginal.trim().split(textStripper.getLineSeparator());
        List<String> textoFinal = new ArrayList<>();


        Pattern pattern = Pattern.compile(PATRON_DURAVIT);
        Matcher matcher;

        for (String leida : lineasTexto) {
            matcher = pattern.matcher(leida);
            if (matcher.matches())
                textoFinal.add(leida);
        }
        pdf.close();
        return textoFinal;
    }

}
