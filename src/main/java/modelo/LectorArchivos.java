package modelo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorArchivos {
    private static final String PATRON_DURAVIT = "(\\w*[+/.]*\\s*)* (\\d{5})(\\s*) ((\\d*)\\.\\d{2})";

    /**
     * Lee el archivo pdf y parsea a productos, con sus respectivos nombres, codigos y costos
     * */
    public void leerArchivo(File archivo, ArrayList<Producto> listaProductos) throws IOException, SQLException {
        PDDocument pdf = PDDocument.load(archivo);
        PDFTextStripper textStripper = new PDFTextStripper();
        ParserTextoAProducto parser = new ParserTextoAProducto();

        String texto = String.valueOf(textStripper.getText(pdf));

        String[] lineasTexto = texto.trim().split(textStripper.getLineSeparator());

        Pattern pattern = Pattern.compile(PATRON_DURAVIT);
        Matcher matcher;

        for(String leida : lineasTexto){
            Producto producto;
            matcher = pattern.matcher(leida);
            if(matcher.matches()) {
                producto = parser.aProducto(leida);
                producto.calcularPrecio();
                listaProductos.add(producto);
            }
        }
        pdf.close();
        subirABaseDeDatos(listaProductos);
    }

    private void subirABaseDeDatos(ArrayList<Producto> productos) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/productos", "root", "2520804");
        Statement statement = connection.createStatement();

        int codigo;
        String nombre;
        int costo;
        int precio;
        StringBuilder query = new StringBuilder("INSERT INTO productos(codigo, nombre, costo, precio) VALUES ");

        for(int i = 0; i < productos.size(); i++) {
            codigo = productos.get(i).getCodigo();
            nombre = productos.get(i).getNombre();
            costo = productos.get(i).getCosto();
            precio = productos.get(i).precio();

            query.append("(").append(codigo).append(", '").append(nombre).append("',").append(costo).append(",").append(precio).append(")");
            if(i < productos.size() - 1)
                query.append(',');
        }
        statement.executeUpdate(query.toString());
    }
}
