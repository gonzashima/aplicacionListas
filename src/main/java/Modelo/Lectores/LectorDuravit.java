package Modelo.Lectores;

import Modelo.Productos.Producto;
import Modelo.Utils.ParserTextoAProducto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorDuravit implements LectorArchivos{

    private static final String PATRON_DURAVIT = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";

    @Override
    public void leerArchivo(File archivo, ArrayList<Producto> listaProductos) throws IOException{
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();
        ParserTextoAProducto parser = new ParserTextoAProducto();

        String texto = String.valueOf(textStripper.getText(pdf));

        String[] lineasTexto = texto.trim().split(textStripper.getLineSeparator());

        Pattern pattern = Pattern.compile(PATRON_DURAVIT);
        Matcher matcher;

        if (listaProductos == null)
            listaProductos = new ArrayList<>();

        if (listaProductos.isEmpty()) {
            for (String leida : lineasTexto) {
                Producto producto;
                matcher = pattern.matcher(leida);
                if (matcher.matches()) {
                    producto = parser.aProducto(leida);
                    producto.calcularPrecio();
                    listaProductos.add(producto);
                }
            }
        }
        else {





        }
        pdf.close();
    }
}
