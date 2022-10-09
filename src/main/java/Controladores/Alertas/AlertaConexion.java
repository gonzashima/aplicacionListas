package Controladores.Alertas;

import Controladores.Alertas.AlertaDB;
import javafx.scene.control.Alert;

public class AlertaConexion implements AlertaDB {

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("No se pudo conectar con la base de datos");
        alert.showAndWait();
    }
}
