package Controladores.Ventanas;

import Controladores.Alertas.*;
import Modelo.Aplicacion;
import Modelo.Productos.Producto;
import Modelo.Utils.Casas;
import Modelo.Utils.ConectorDB;
import Modelo.Utils.CreadorExcel;
import Modelo.Utils.UnificadorString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


/**
 * Es el controlador de la pantalla principal y maneja todas las acciones que ocurren en ella.
 * */
public class PantallaPrincipalControl implements Initializable {
    @FXML private Button botonMostrar, botonCrearExcel;

    @FXML private MenuItem guardarCambios;

    @FXML private ChoiceBox<String> opcionesListas, opcionesCasas;

    @FXML private TableView<Producto> tabla;

    @FXML private TableColumn<Producto, String> codigo, nombre, costo, precio, porcentaje, costoDescontado;

    @FXML private TextField textoBuscado;

    @FXML private Label advertenciaGuardado;

    private final ObservableList<Producto> listaOb = FXCollections.observableArrayList();

    /**
     * Pasa todas las casas a string y las devuelve como array
     * */
    private ArrayList<String> casasAString() {
        ArrayList<String> nombresCasas = new ArrayList<>();
        Casas[] casas = Casas.values();

        for (Casas casa : casas) {
            nombresCasas.add(casa.toString());
        }
        return nombresCasas;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opcionesCasas.getItems().addAll(casasAString());
        botonMostrar.setDisable(true);
        guardarCambios.setDisable(true);
        advertenciaGuardado.setVisible(false);

        textoBuscado.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                buscarProducto();
        });

        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        porcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        costoDescontado.setCellValueFactory(new PropertyValueFactory<>("costoDescontado"));
    }

    /**
     * De acuerdo con la casa seleccionada, muestra las listas correspondientes a esa casa en la choicebox de listas
     * */
    public void seleccionarCasa() {
        try {
            String casa = opcionesCasas.getValue();
            casa = casa.toUpperCase();
            Casas casaEnum = Casas.valueOf(casa);
            String[] listas = casaEnum.getListas();

            opcionesListas.getItems().clear();
            opcionesListas.getItems().addAll(listas);

            botonMostrar.setDisable(false);
        } catch (Exception e) {
            Alerta alerta = new AlertaCasa();
            alerta.display();
        }
    }


    /**
     * Muestra toda la informacion de la tabla pedida, exceptuando a aquellos productos cuyo precio sea 0
     * */
    public void mostrarDatos() {
        String nombreLista = opcionesListas.getValue();
        Aplicacion app = Aplicacion.getInstance();
        Alerta alerta;

        try {
            Connection connection = ConectorDB.getConnection();
            nombreLista = nombreLista.toLowerCase();

            if (connection != null) {
                boolean existeTabla, estaVacia;

                existeTabla = ConectorDB.existeTabla(nombreLista);
                estaVacia = app.estaVacia(nombreLista);

                /*
                    * Basicamente, si la conexion esta y todavia no tengo en memoria la info para mostrar, voy a la DB.
                    * Si ya tengo la info, no hace falta ir a la DB.
                    * */
                if (estaVacia && existeTabla) {
                    nombreLista = UnificadorString.unirString(nombreLista);
                    String query = "SELECT * from " + nombreLista + " WHERE precio != 0";
                    HashMap<Integer, Producto> productos = ConectorDB.ejecutarQuery(query, nombreLista);

                    listaOb.clear();
                    listaOb.addAll(productos.values());
                    app.agregarListaDeProductos(nombreLista, productos);
                    tabla.setItems(listaOb);
                } else if (!estaVacia && existeTabla) {
                    nombreLista = UnificadorString.unirString(nombreLista);
                    HashMap<Integer, Producto> productos = app.obtenerLista(nombreLista);
                    listaOb.clear();
                    listaOb.addAll(productos.values());
                    tabla.setItems(listaOb);
                } else {
                    alerta = new AlertaTabla();
                    alerta.display();
                }
            } else {
                alerta = new AlertaConexion();
                alerta.display();
            }
    } catch (NullPointerException e) {
            alerta = new AlertaListaNoSeleccionada();
            alerta.display();
        }
        catch (SQLException sqlException) {
            alerta = new AlertaConexion();
            alerta.display();
        }
    }

    /**
     * Pide a Aplicacion que busque todos los productos que coinciden con la busqueda del usuario
     * */
    public void buscarProducto() {
        String tabla = opcionesListas.getValue();
        String nombreBuscado = textoBuscado.getText();
        Aplicacion app = Aplicacion.getInstance();

        try {
            ArrayList<Producto> filtrados = app.buscarProducto(tabla, nombreBuscado);
            listaOb.clear();
            listaOb.addAll(filtrados);
            this.tabla.setItems(listaOb);
        } catch (Exception e) {
            Alerta alerta = new AlertaListaNoSeleccionada();
            alerta.display();
        }
    }

    /**
     * Muestra una nueva ventana y modifica el porcentaje del producto seleccionado
     * */
    public void modificarPorcentaje() throws IOException {
        Producto producto = tabla.getSelectionModel().getSelectedItem();

        try {
            int porcentajeAnterior = producto.getPorcentaje();
            VentanaPorcentaje ventanaPorcentaje = new VentanaPorcentaje();
            Producto modificado = ventanaPorcentaje.display(producto);
            tabla.refresh();

            if (porcentajeAnterior != modificado.getPorcentaje()) {
                Aplicacion app = Aplicacion.getInstance();
                String nombreLista = opcionesListas.getValue();
                nombreLista = nombreLista.toLowerCase();

                app.agregarModificacion(nombreLista, producto);
                guardarCambios.setDisable(false);
                advertenciaGuardado.setVisible(true);
            }
        } catch (NullPointerException e) {
            Alerta alerta = new AlertaProductoSinSeleccionar();
            alerta.display();
        }
    }

    /**
     * Indica a Aplicacion que debe guardar todos los cambios acumulados hasta el momento
     * */
    public void guardarCambios() throws SQLException {
        Aplicacion app = Aplicacion.getInstance();
        int cambios = app.guardarModificaciones();
        guardarCambios.setDisable(true);
        advertenciaGuardado.setVisible(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cambios realizados");
        alert.setHeaderText("Se actualizaron " + cambios + " productos.");
        alert.showAndWait();
    }

    public void crearExcel() throws IOException {
//        VentanaExcel ventanaExcel = new VentanaExcel();
//        ventanaExcel.display();
    }


    /**
     * Muestra por pantalla la ventana para leer archivos
     * */
    public void mostrarVentanaArchivos() throws IOException {
        VentanaLeerArchivos ventanaLeerArchivos = new VentanaLeerArchivos();
        ventanaLeerArchivos.display();
    }
}
