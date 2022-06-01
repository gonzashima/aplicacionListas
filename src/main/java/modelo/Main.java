package modelo;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws IOException, SQLException {
//        AnchorPane anchorPane = new AnchorPane();
//
//        Button boton = new Button("Hola");
//        anchorPane.getChildren().add(boton);
//        Scene escenaPrincipal = new Scene(anchorPane);
//
//        stage.setTitle("hola");
//        stage.setScene(escenaPrincipal);
//        stage.show();
//
//        stage.setOnCloseRequest(e->{e.consume();
//            cerrarPrograma(stage);});

        Aplicacion aplicacion = new Aplicacion();
        File duravit = new File("src/main/resources/DURAVIT.pdf");

        aplicacion.leerArchivo(duravit);
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
