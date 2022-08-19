package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Productos.Producto;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VentanaPorcentaje implements Initializable {

    @FXML TextField campoTexto;

    @FXML Button botonAceptar;

    @FXML Label advertencia;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        advertencia.setVisible(false);
    }

    public static void display(Producto producto) throws IOException {
        Stage ventana = new Stage();

        ventana.setTitle("Modificar porcantaje");
        ventana.initModality(Modality.APPLICATION_MODAL);

        URL url = new File("src/main/java/interfaz/Ventana porcentaje.fxml").toURI().toURL();
        AnchorPane root = FXMLLoader.load(url);

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

}
