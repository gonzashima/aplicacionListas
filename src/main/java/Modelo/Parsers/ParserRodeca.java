package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoRodeca;
import Modelo.Utils.ConectorDB;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ParserRodeca implements Parser{

    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaRodeca;

        if (datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.RODECA)) == null) {
            mapaRodeca = ConectorDB.seleccionarProductos(ConstantesStrings.RODECA);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.RODECA), mapaRodeca);
        }

        mapaRodeca = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.RODECA));

        String nombre, costoString, codigoString;
        int costo, codigo;

        for (int i = 0; i < texto.size(); i += 3) {
            nombre = texto.get(i);
            codigoString = texto.get(i + 1);
            costoString = texto.get(i + 2);

            codigoString = codigoString.split(":")[1].trim();
            costoString = costoString.replace("$", "").replace(".", "").replace(",",".");

            String codigoHash = DigestUtils.sha256Hex(codigoString);
            codigo = Math.abs(codigoHash.hashCode()) % 1_000_000;

            costo = (int) Math.round(Double.parseDouble(costoString));

            Producto producto = new ProductoRodeca(nombre, codigo, costo);
            producto.calcularPrecio();

            if (mapaRodeca.isEmpty() || !mapaRodeca.containsKey(producto.getCodigo()))
                mapaRodeca.put(producto.getCodigo(), producto);
            else {
                Producto anterior = mapaRodeca.get(producto.getCodigo());
                int porcentajeAnterior = anterior.getPorcentaje();
                int idAnterior = anterior.getId();

                producto.setPorcentaje(porcentajeAnterior);
                producto.setId(idAnterior);
                producto.calcularPrecio();
                mapaRodeca.put(producto.getCodigo(), producto);
            }
        }
    }
}
