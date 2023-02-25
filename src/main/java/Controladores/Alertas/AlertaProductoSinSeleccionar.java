package Controladores.Alertas;

import javafx.scene.control.Alert;

public class AlertaProductoSinSeleccionar implements Alerta{
    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Selecciona un producto");
        alert.showAndWait();
    }
}
