package Controladores.Ventanas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Modelo.Productos.Producto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Controlador de la ventana para modificar porcentajes
 */
public class VentanaPorcentaje implements Initializable {

    @FXML TextField campoTexto;

    @FXML AnchorPane contenedorPrincipal;

    @FXML Button botonAceptar;

    @FXML Label advertencia;

    private static List<Producto> productos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        advertencia.setVisible(false);
        campoTexto.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                cambiarPorcentaje();
        });
    }


    /**
     * Muestra por pantalla la ventana para modificar porcentajes
     * */
    public List<Producto> display(List<Producto> productos) throws IOException {
        Stage ventana = new Stage();
        VentanaPorcentaje.productos = productos;

        ventana.setTitle("Modificar porcantaje");
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setResizable(false);

        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/fxml/VentanaPorcentaje.fxml")));

        Scene escena = new Scene(root);
        ventana.setScene(escena);
        ventana.showAndWait();

        return productos;
    }

    /**
     * Cambia el porcentaje
     * */
    public void cambiarPorcentaje() {
        String texto = campoTexto.getText();

        try {
            int numero = Integer.parseInt(texto);
            for(Producto p : productos) {
                p.setPorcentaje(numero);
                p.calcularPrecio();
            }
        } catch (Exception e) {
            advertencia.setVisible(true);
        }

        Stage ventana = (Stage) contenedorPrincipal.getScene().getWindow();
        ventana.close();
    }

}
