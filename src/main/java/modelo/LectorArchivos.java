package modelo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorArchivos {
    private ArrayList<Producto> listaProductos;
    private static final String PATRON_DURAVIT = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";

    public LectorArchivos() {
        this.listaProductos = new ArrayList<>();
    }


    public void leerArchivo(File archivo) throws IOException {
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();

        String texto = String.valueOf(textStripper.getText(pdf));

        String[] lineasTexto = texto.trim().split(textStripper.getLineSeparator());
        ArrayList<String> lineas = new ArrayList<>();

        for(String leida : lineasTexto){
            Pattern pattern = Pattern.compile(PATRON_DURAVIT);
            Matcher matcher = pattern.matcher(leida);

            if(matcher.matches())
                lineas.add(leida);
        }


        pdf.close();
    }
}
