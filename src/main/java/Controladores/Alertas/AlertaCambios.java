package Controladores.Alertas;

import javafx.scene.control.Alert;

/**
 * Es la alerta que se muestra por pantalla si ocurre algun error a la hora de hacer los cambios pedidos
 * */
public class AlertaCambios implements AlertaDB{

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Hubo un error al hacer los cambios");
        alert.showAndWait();
    }
}
