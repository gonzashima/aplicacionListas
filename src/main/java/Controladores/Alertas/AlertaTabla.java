package Controladores.Alertas;

import javafx.scene.control.Alert;

/**
 * Alerta que se muestra si la tabla buscada no existe
 * */
public class AlertaTabla implements Alerta {

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("La tabla buscada no existe");
        alert.showAndWait();
    }
}
