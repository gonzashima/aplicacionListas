package Controladores.Alertas;

import javafx.scene.control.Alert;

public class AlertaListaNoSeleccionada implements Alerta{
    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Ninguna lista seleccionada");
        alert.showAndWait();
    }
}
