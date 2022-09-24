package Controladores;

import Modelo.Utils.ConectorDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Modelo.Aplicacion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;

public class ControladorLeerArchivos {

    @FXML MenuItem cerrar, irAListas;

    @FXML AnchorPane contenedorPrincipal;

    @FXML ListView<String> lista;

    @FXML Button botonBuscar;

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

    public void cambiarAPantallaListas() throws IOException {
        Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();

        URL url = new File("src/main/java/interfaz/PantallaPrincipal.fxml").toURI().toURL();
        AnchorPane root = FXMLLoader.load(url);

        Scene escenaArchivos = new Scene(root);
        stage.setScene(escenaArchivos);
        stage.show();
    }

    /**
     * Lee el archivo seleccionado en el buscador
     * */
    public void buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDFs (*.pdf)", "*.pdf"));

        File archivo = fileChooser.showOpenDialog(null);
        String mensaje;

        try {
            mensaje = archivo.getName();
            mensaje = mensaje.substring(0, mensaje.lastIndexOf("."));
            mensaje = mensaje + " se pudo leer correctamente";

            Aplicacion app = Aplicacion.getInstance();
            app.leerArchivo(archivo);

        } catch (Exception ex) {
            mensaje = "Este archivo no se pudo abrir o no es de una lista";
        }
        lista.getItems().add(mensaje);
    }

}
