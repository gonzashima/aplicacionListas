package Controladores.Alertas;

import javafx.scene.control.Alert;

/**
 * Esta alerta se muestra cuando hubo un error y no se pudo conectar a la base de datos
 * */
public class AlertaConexion implements AlertaDB {

    @Override
    public void display() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("No se pudo conectar con la base de datos");
        alert.showAndWait();
    }
}
