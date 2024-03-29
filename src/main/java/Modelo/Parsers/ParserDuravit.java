package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDuravit;
import Modelo.Utils.ConectorDB;
import Modelo.Constantes.ConstantesStrings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserDuravit implements Parser {

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaDuravit;

        if (datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DURAVIT)) == null) { //si la tabla existe pero no esta en memoria, la cargo en memoria
            mapaDuravit = ConectorDB.seleccionarProductos(ConstantesStrings.DURAVIT);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.DURAVIT), mapaDuravit);
        }

        mapaDuravit = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DURAVIT));

        for (String linea : texto) {
            StringBuilder nombre = new StringBuilder();
            int codigo;
            int costo;

            String[] lineaSeparada = linea.trim().split(" ");
            List<String> palabras = new ArrayList<>(Arrays.asList(lineaSeparada));
            boolean quedan = palabras.remove("");

            while (quedan)
                quedan = palabras.remove("");
            int tamanio = palabras.size();

            for (int i = 0; i < tamanio - 2; i++) {
                nombre.append(palabras.get(i));
                if (i != tamanio - 3)
                    nombre.append(" ");
            }
            codigo = Integer.parseInt(palabras.get(tamanio - 2));
            costo = (int) Math.round(Double.parseDouble(palabras.get(tamanio - 1)));

            Producto producto = new ProductoDuravit(nombre.toString(), codigo, costo);
            producto.calcularPrecio();

            if (mapaDuravit.isEmpty() || !mapaDuravit.containsKey(producto.getCodigo()))
                mapaDuravit.put(producto.getCodigo(), producto);
            else {
                Producto anterior = mapaDuravit.get(producto.getCodigo());
                int porcentajeAnterior = anterior.getPorcentaje();
                int idAnterior = anterior.getId();

                producto.setPorcentaje(porcentajeAnterior);
                producto.setId(idAnterior);
                producto.calcularPrecio();
                mapaDuravit.put(producto.getCodigo(), producto);                    //lo reemplazo porque puede pasar que el producto no se haga mas y hayan reasignado el codigo
            }
        }
    }
}
