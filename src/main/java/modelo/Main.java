package modelo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        AnchorPane anchorPane = new AnchorPane();

        Button boton = new Button("Hola");
        anchorPane.getChildren().add(boton);
        Scene escenaPrincipal = new Scene(anchorPane);

        stage.setTitle("hola");
        stage.setScene(escenaPrincipal);
        stage.show();

        stage.setOnCloseRequest(e->{e.consume();
            cerrarPrograma(stage);});
    }

    private void cerrarPrograma(Stage ventana) {
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Desea Cerrar?");
        alerta.getDialogPane().setHeaderText("SALIR");
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) { ventana.close();}
    }
}
