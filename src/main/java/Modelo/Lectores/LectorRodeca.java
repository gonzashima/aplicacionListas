package Modelo.Lectores;

import Modelo.Constantes.ConstantesStrings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorRodeca implements LectorArchivos{

    private List<String> excluidas;

    public LectorRodeca() {
        excluidas = new ArrayList<>();
        excluidas.add(ConstantesStrings.RODECA.toUpperCase());
        excluidas.add("NUEVO");
        excluidas.add(".COM.AR");
        excluidas.add("/");
        excluidas.add("POWERED");
        excluidas.add("MEDIDAS");
        excluidas.add("BULTO");
    }

    @Override
    public List<String> leerArchivo(File archivo) throws IOException {
        File file = new File("src/main/resources/23-11-23 JUEGOS DE MESA.pdf");
        PDDocument pdf = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();

        String textoOriginal = textStripper.getText(pdf);
        textoOriginal = textoOriginal.toUpperCase();

        List<String> texto = new ArrayList<>(List.of(textoOriginal.trim().split(textStripper.getLineSeparator())));

        for (int i = 0; i < texto.size(); i++) {
            if (excluidas.stream().anyMatch(texto.get(i) :: contains)) {
                texto.remove(i);
                i--;
            }
        }

        return texto;
    }
}
