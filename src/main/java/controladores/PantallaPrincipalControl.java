package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Aplicacion;
import modelo.Productos.Producto;
import modelo.Utils.ConectorDB;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PantallaPrincipalControl implements Initializable {

    @FXML private AnchorPane contenedorPrincipal;

    @FXML private ChoiceBox<String> opcionesListas;

    @FXML private Label advertencia, advertenciaDB, advertenciaBuscar;

    @FXML private TableView<ModeloTabla> tabla;

    @FXML private TableColumn<ModeloTabla, String> codigo, nombre, costo, precio;

    @FXML private TextField textoBuscado;

    private final String[] listas = {"Duravit"};

    private final ObservableList<ModeloTabla> listaOb = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opcionesListas.getItems().addAll(listas);
        advertencia.setVisible(false);
        advertenciaDB.setVisible(false);
        advertenciaBuscar.setVisible(false);

        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    /**
     * Muestra toda la informacion de la tabla pedida, exceptuando a aquellos productos cuyo precio sea 0
     * */
    public void mostrarDatos(ActionEvent e) throws SQLException {
        if (advertenciaBuscar.isVisible())
            advertenciaBuscar.setVisible(false);

        String nombreLista = opcionesListas.getValue();
        Aplicacion app = Aplicacion.getInstance();

        if (nombreLista == null)
            advertencia.setVisible(true);
        else {
            if (advertencia.isVisible())
                advertencia.setVisible(false);

            Connection connection = ConectorDB.getConnection();
            nombreLista = nombreLista.toLowerCase();

            /*
             * Basicamente, si la conexion esta y todavia no tengo en memoria la info para mostrar, voy a la DB.
             * Si ya tengo la info, no hace falta ir a la DB.
             * */
            if (connection != null && app.estaVacia(nombreLista)) {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ResultSet rs = statement.executeQuery("SELECT * from " + nombreLista + " WHERE precio != 0");

                ArrayList<Producto> productos = new ArrayList<>();

                while (rs.next()) {
                    int codigo = rs.getInt("codigo");
                    String nombre = rs.getString("nombre");
                    int costo = rs.getInt("costo");
                    int precio = rs.getInt("precio");

                    listaOb.add(new ModeloTabla(codigo, nombre, costo, precio));
                    productos.add(new Producto(codigo, nombre, costo, precio));
                }
                app.agregarListaDeProductos(nombreLista, productos);
                tabla.setItems(listaOb);
            } else if (connection == null)
                advertenciaDB.setVisible(true);
            else {
                ArrayList<Producto> productos = app.obtenerLista(nombreLista);
                listaOb.clear();
                for (Producto p : productos)
                    listaOb.add(new ModeloTabla(p.getCodigo(), p.getNombre(), p.getCosto(), p.getPrecio()));
            }
        }
    }
//TODO hacer la advertencia de buscar invisible una vez que ya se mostro la lista
    public void buscarProducto(ActionEvent e) {
        String tabla = opcionesListas.getValue();
        String nombreBuscado = textoBuscado.getText();
        Aplicacion app = Aplicacion.getInstance();

        if (tabla == null || app.estaVacia(tabla)) {
            advertenciaBuscar.setVisible(true);
        }

        else {
            ArrayList<Producto> filtrados = app.buscarProducto(tabla, nombreBuscado);
            listaOb.clear();

            for (Producto p : filtrados) {
                listaOb.add(new ModeloTabla(p.getCodigo(), p.getNombre(), p.getCosto(), p.getPrecio()));
            }
            this.tabla.setItems(listaOb);
        }
    }

    public void cerrarApp(ActionEvent e){
        Stage ventana = (Stage) contenedorPrincipal.getScene().getWindow();
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");

        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Seguro que desea salir?");
        alerta.getDialogPane().setHeaderText("SALIR");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) { ventana.close();}
    }

    public void cambiarAPantallaArchivos(ActionEvent e) throws IOException {
        Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();

        URL url = new File("src/main/java/interfaz/PantallaLeerArchivos.fxml").toURI().toURL();
        AnchorPane root = FXMLLoader.load(url);

        Scene escenaArchivos = new Scene(root);
        stage.setScene(escenaArchivos);
        stage.show();
    }
}
