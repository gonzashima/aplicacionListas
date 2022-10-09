package Controladores;

import Controladores.Alertas.AlertaConexion;
import Controladores.Alertas.AlertaDB;
import Controladores.Alertas.AlertaTabla;
import Modelo.Aplicacion;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PantallaPrincipalControl implements Initializable {

    @FXML private AnchorPane contenedorPrincipal;

    @FXML private MenuItem guardarCambios;

    @FXML private ChoiceBox<String> opcionesListas;

    @FXML private Label advertencia, advertenciaBuscar, advertenciaModificacion;

    @FXML private TableView<Producto> tabla;

    @FXML private TableColumn<Producto, String> codigo, nombre, costo, precio, porcentaje;

    @FXML private TextField textoBuscado;

    private final String[] listas = {"Duravit"};

    private final ObservableList<Producto> listaOb = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opcionesListas.getItems().addAll(listas);
        advertencia.setVisible(false);
        advertenciaBuscar.setVisible(false);
        advertenciaModificacion.setVisible(false);

        guardarCambios.setDisable(true);

        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        porcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
    }

    private boolean existeTabla(String nombre, Connection connection) throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, nombre, null);

        return rs.next();
    }

    /**
     * Muestra toda la informacion de la tabla pedida, exceptuando a aquellos productos cuyo precio sea 0
     * */
    public void mostrarDatos() throws SQLException {
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


            if (connection != null) {
                boolean existeTabla, estaVacia;

                existeTabla = existeTabla(nombreLista, connection);
                estaVacia = app.estaVacia(nombreLista);

                /*
                 * Basicamente, si la conexion esta y todavia no tengo en memoria la info para mostrar, voy a la DB.
                 * Si ya tengo la info, no hace falta ir a la DB.
                 * */
                if (estaVacia && existeTabla) {
                    String query = "SELECT * from " + nombreLista + " WHERE precio != 0";
                    ResultSet rs = ConectorDB.ejecutarQuery(query);

                    ArrayList<Producto> productos = new ArrayList<>();

                    while (rs.next()) {
                        int codigo = rs.getInt("codigo");
                        String nombre = rs.getString("nombre");
                        int costo = rs.getInt("costo");
                        int precio = rs.getInt("precio");
                        int porcentaje = rs.getInt("porcentaje");

                        Producto producto = new Producto(codigo, nombre, costo, precio, porcentaje);
                        listaOb.add(producto);
                        productos.add(producto);
                    }
                    app.agregarListaDeProductos(nombreLista, productos);
                    tabla.setItems(listaOb);
                }
                else if (!estaVacia && existeTabla){
                    ArrayList<Producto> productos = app.obtenerLista(nombreLista);
                    listaOb.clear();
                    listaOb.addAll(productos);
                    tabla.setItems(listaOb);
                }
                else {
                    AlertaDB alerta = new AlertaTabla();
                    alerta.display();
                }
            }
            else {
                AlertaDB alerta = new AlertaConexion();
                alerta.display();
            }
        }
    }

    /**
     * Pide a Aplicacion que busque todos los productos que coinciden con la busqueda del usuario
     * */
    public void buscarProducto() {
        String tabla = opcionesListas.getValue();
        String nombreBuscado = textoBuscado.getText();
        Aplicacion app = Aplicacion.getInstance();

        if (tabla == null || app.estaVacia(tabla)) {
            advertenciaBuscar.setVisible(true);
        }

        else {
            ArrayList<Producto> filtrados = app.buscarProducto(tabla, nombreBuscado);
            listaOb.clear();
            listaOb.addAll(filtrados);
            this.tabla.setItems(listaOb);
        }
    }

    /**
     * Modifica el porcentaje del producto seleccionado
     * */
    public void modificarPorcentaje() throws IOException {
        Producto producto = tabla.getSelectionModel().getSelectedItem();

        if (producto == null)
            advertenciaModificacion.setVisible(true);
        else {
            if (advertenciaModificacion.isVisible())
                advertenciaModificacion.setVisible(false);

            int precioAnterior = producto.getPrecio();
            Producto modificado = VentanaPorcentaje.display(producto);
            tabla.refresh();

            if (precioAnterior != modificado.getPrecio()) {
                Aplicacion app = Aplicacion.getInstance();

                String nombreLista = opcionesListas.getValue();
                nombreLista = nombreLista.toLowerCase();

                app.agregarModificacion(nombreLista, producto);
                guardarCambios.setDisable(false);
            }
        }
    }

    public void guardarCambios() throws SQLException {
        Aplicacion app = Aplicacion.getInstance();
        int cambios = app.guardarModificaciones();
        guardarCambios.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cambios realizados");
        alert.setHeaderText("Se actualizaron " + cambios + " productos.");
        alert.showAndWait();
    }


    public void cerrarApp() throws SQLException {
        Stage ventana = (Stage) contenedorPrincipal.getScene().getWindow();
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");

        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Seguro que desea salir?");
        alerta.getDialogPane().setHeaderText("SALIR");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            ConectorDB.close();
            ventana.close();
        }
    }

    public void cambiarAPantallaArchivos() throws IOException {
        VentanaLeerArchivos.display();
    }
}
