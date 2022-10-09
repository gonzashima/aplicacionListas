package Controladores.Alertas;

import javafx.scene.control.Alert;

public class AlertaTabla implements AlertaDB{

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("La tabla buscada no existe");
        alert.showAndWait();
    }
}
