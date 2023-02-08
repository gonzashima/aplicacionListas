package Modelo.Parsers;

import Modelo.Productos.Producto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ParserDuravit implements Parser{

    private static final String NOMBRE = "duravit";

    @Override
    public void parsearAProducto(String linea, HashMap<String, HashMap<Integer, Producto>> datos) {
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


    }
}
