package Modelo.Parsers;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Productos.Producto;
import Modelo.Productos.ProductoRigolleau;
import Modelo.Utils.ConectorDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserRigolleau implements Parser{
    @Override
    public void parsearAProducto(List<String> texto, HashMap<Integer, HashMap<Integer, Producto>> datos) throws SQLException {
        ConectorDB.getConnection();
        HashMap<Integer, Producto> mapaRigolleau;
        mapaRigolleau = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.RIGOLLEAU));

        if (mapaRigolleau == null) {
            mapaRigolleau = ConectorDB.seleccionarProductos(ConstantesStrings.RIGOLLEAU);
            datos.put(ConstantesNumericas.codigoLista(ConstantesStrings.RIGOLLEAU), mapaRigolleau);
        }

        for (String linea: texto) {
            List<String> palabras = new ArrayList<>(Arrays.asList(linea.split(":")));
            int codigo = Integer.parseInt(palabras.remove(0));
            StringBuilder nombre = new StringBuilder(palabras.remove(0) + " " + palabras.remove(0).replace(" ", ""));  //nombre = nombre + capacidad

            palabras.remove(0); //elimino las unidades por bulto

            String costoString = palabras.remove(0).replace("$","").replace(",",".").replace(" ","");
            int costo = Integer.parseInt(costoString.split("\\.")[0]);

            palabras.remove(0); //elimino las aclaraciones/bonificaciones

            for (String s : palabras) {
                nombre.append(" ");
                nombre.append(s);
            }

            Producto producto = new ProductoRigolleau(nombre.toString().toUpperCase(), codigo, costo);
            producto.calcularPrecio();
            insertarAlMapa(producto, mapaRigolleau);
        }
    }

    private void insertarAlMapa(Producto producto, HashMap<Integer, Producto> mapa) {
        if (mapa.isEmpty() || !mapa.containsKey(producto.getCodigo()))
            mapa.put(producto.getCodigo(), producto);
        else {
            int porcentajeAnterior = mapa.get(producto.getCodigo()).getPorcentaje();
            producto.setPorcentaje(porcentajeAnterior);
            producto.calcularPrecio();
            mapa.put(producto.getCodigo(), producto);           //lo reemplazo porque puede pasar que el producto no se haga mas y hayan reasignado el codigo
        }
    }
}
