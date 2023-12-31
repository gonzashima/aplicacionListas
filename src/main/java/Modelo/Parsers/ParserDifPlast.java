package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoDifPlast;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ParserDifPlast implements Parser{

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaDifPlast;

        if (datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST)) == null) {
            mapaDifPlast = ConectorDB.seleccionarProductos(ConstantesStrings.DIF_PLAST);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST), mapaDifPlast);
        }
        mapaDifPlast = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.DIF_PLAST));

        for (String linea : texto) {
            String nombre, codigoString, costoString;
            int codigo, costo;
            Producto producto;

            String[] lineaSeparada = linea.trim().split("~");
            codigoString = lineaSeparada[0];
            nombre = lineaSeparada[1];
            costoString = lineaSeparada[2];

            codigo = Integer.parseInt(codigoString);

            costoString = costoString.trim().replace("$","").replace(".","").split(",")[0].trim();
            costo = Integer.parseInt(costoString);

            producto = new ProductoDifPlast(nombre.toUpperCase(), codigo, costo);
            producto.calcularPrecio();

            if (mapaDifPlast.isEmpty() || !mapaDifPlast.containsKey(producto.getCodigo()))
                mapaDifPlast.put(producto.getCodigo(), producto);
            else {
                Producto anterior = mapaDifPlast.get(producto.getCodigo());
                int porcentajeAnterior = anterior.getPorcentaje();
                int idAnterior = anterior.getId();

                producto.setPorcentaje(porcentajeAnterior);
                producto.setId(idAnterior);
                producto.calcularPrecio();
                mapaDifPlast.put(producto.getCodigo(), producto);
            }
        }
    }
}
