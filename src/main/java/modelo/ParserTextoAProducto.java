package modelo;

import java.util.ArrayList;
import java.util.Arrays;

public class ParserTextoAProducto {

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
