package Controladores.Ventanas;

import Modelo.Productos.Producto;
import Modelo.Utils.CreadorExcel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VentanaExcel {

    @FXML Button botonLista, botonCartel;

    private static List<Producto> productos;


    public void display(List<Producto> productos) throws IOException {
        VentanaExcel.productos = productos;
        Stage ventana = new Stage();

        ventana.setTitle("Crear excel");
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setResizable(false);

        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/fxml/VentanaExcel.fxml")));

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        root.requestFocus();
        ventana.showAndWait();
    }

    public void crearLista() {
        try {
            CreadorExcel creadorExcel = new CreadorExcel();
            creadorExcel.crearLista(productos);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("El archivo pudo crearse correctamente");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Hubo un error al crear el excel");
            alert.show();
        }
    }

    public void crearCarteles() {
        try {
            CreadorExcel creadorExcel = new CreadorExcel();
            creadorExcel.crearCarteles(productos);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("El archivo pudo crearse correctamente");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Hubo un error al crear el excel");
            alert.show();
        }

    }
}
