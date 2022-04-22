package modelo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorArchivos {
    private LinkedList<Producto> listaProductos;
    private static final String PATRON_DURAVIT = "(\\w*\\s*)*(\\d{5})(\\s)*(\\d*)\\.\\d{2}";

    public LectorArchivos() {
        this.listaProductos = new LinkedList<>();
    }


    public void leerArchivo(File archivo) throws IOException {
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();

        String texto = String.valueOf(textStripper.getText(pdf));

        Pattern pattern = Pattern.compile(PATRON_DURAVIT, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(texto);

        while(matcher.find()){
            String leido = matcher.group();
            String[] partido = leido.split("\\s*");
            int indice = partido.length;

            System.out.println(partido[0]);
        }

        pdf.close();
    }
}
