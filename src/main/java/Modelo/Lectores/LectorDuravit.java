package Modelo.Lectores;

import Modelo.Productos.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorDuravit implements LectorArchivos{

    private static final String PATRON_DURAVIT = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";

    @Override
    public HashMap<Integer, Producto> leerArchivo(File archivo, HashMap<Integer, Producto> mapaProductos) throws IOException{
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();

        String texto = String.valueOf(textStripper.getText(pdf));

        String[] lineasTexto = texto.trim().split(textStripper.getLineSeparator());

        Pattern pattern = Pattern.compile(PATRON_DURAVIT);
        Matcher matcher;

        if (mapaProductos == null)
            mapaProductos = new HashMap<>();

        for (String leida : lineasTexto) {
            Producto producto;
            matcher = pattern.matcher(leida);
            if (matcher.matches()) {
                producto = aProducto(leida);
                producto.calcularPrecio();
                if (mapaProductos.isEmpty() || !mapaProductos.containsKey(producto.getCodigo()))
                    mapaProductos.put(producto.getCodigo(), producto);

                else {
                    Producto anterior = mapaProductos.get(producto.getCodigo());
                    anterior.setCosto(producto.getCosto());
                    anterior.calcularPrecio();
                }
            }
        }
        pdf.close();
        return mapaProductos;
    }

    @Override
    public String nombreTabla() {
        return "duravit";
    }

    public Producto aProducto(String linea){
        StringBuilder nombre = new StringBuilder();
        int codigo;
        int costo;

        String[] lineaSeparada = linea.trim().split(" ");
        ArrayList<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));

        boolean quedan = palabras.remove("");

        while (quedan)
            quedan = palabras.remove("");

        int tamanio = palabras.size();

        for (int i = 0; i < tamanio - 2; i++){
            nombre.append(palabras.get(i));

            if(i != tamanio - 3)
                nombre.append(" ");
        }

        codigo = Integer.parseInt(palabras.get(tamanio - 2));
        costo = (int) Math.round(Double.parseDouble(palabras.get(tamanio - 1)));

        return new Producto(nombre.toString(), codigo, costo);
    }
}
