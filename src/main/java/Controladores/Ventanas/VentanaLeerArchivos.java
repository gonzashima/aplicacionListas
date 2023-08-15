package Controladores.Ventanas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Modelo.Aplicacion;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Controlador de la ventana que se encarga de leer los archivos
 * */
public class VentanaLeerArchivos {

    @FXML AnchorPane contenedorPrincipal;

    @FXML Button seleccionarArchivo, botonCancelar;

    /**
     * Muestra por pantalla la ventana para leer archivos
     * */
    public void display() throws IOException {
        Stage ventana = new Stage();

        ventana.setTitle("Leer archivo");
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setResizable(false);

        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/fxml/VentanaLeerArchivos.fxml")));

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    /**
     * Cierra la ventana
     * */
    public void cerrarVentana() {
        Stage ventana = (Stage) contenedorPrincipal.getScene().getWindow();
        ventana.close();
    }

    /**
     * Lee el archivo seleccionado en el buscador
     * */
    public void buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDFs (*.pdf)", "*.pdf"),
                                                new FileChooser.ExtensionFilter("Excel (*xlsx)", "*.xlsx"),
                                                new FileChooser.ExtensionFilter("Excel viejo(*xls)", "*.xls"));

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

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(mensaje);
        alert.showAndWait();
    }

}
