package Controladores.Ventanas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class VentanaExcel {

    @FXML Button botonLista, botonCartel;

    public void display() throws IOException {
        Stage ventana = new Stage();

        ventana.setTitle("Crear excel");
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setResizable(false);

        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/fxml/VentanaExcel.fxml")));

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        ventana.showAndWait();
    }
}
