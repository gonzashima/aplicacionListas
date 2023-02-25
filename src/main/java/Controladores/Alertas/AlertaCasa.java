package Controladores.Alertas;

import javafx.scene.control.Alert;

public class AlertaCasa implements Alerta{

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Selecciona una casa");
        alert.showAndWait();
    }
}
