package Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Modelo.Productos.Producto;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VentanaPorcentaje implements Initializable {

    @FXML TextField campoTexto;

    @FXML Button botonAceptar;

    @FXML Label advertencia;

    static Producto producto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        advertencia.setVisible(false);
    }

    public static Producto display(Producto input) throws IOException {
        Stage ventana = new Stage();
        producto = input;

        ventana.setTitle("Modificar porcantaje");
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setResizable(false);

        URL url = new File("src/main/java/interfaz/VentanaPorcentaje.fxml").toURI().toURL();
        AnchorPane root = FXMLLoader.load(url);

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        ventana.showAndWait();

        return producto;
    }

    public void cambiarPorcentaje(ActionEvent event) {
        String texto = campoTexto.getText();

        try {
            int numero = Integer.parseInt(texto);
            producto.setPorcentaje(numero);
            producto.calcularPrecio();
        } catch (Exception e) {
            advertencia.setVisible(true);
        }

        Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
        ventana.close();
    }

}
