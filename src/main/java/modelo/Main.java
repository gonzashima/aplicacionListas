package modelo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Main extends Application{

    //TODO revisar que es lo de github actions para java y maven

    @Override
    public void start(Stage stage) throws IOException {
        URL url = new File("src/main/java/interfaz/PantallaPrincipal.fxml").toURI().toURL();

        Parent root = FXMLLoader.load(url);
        Scene escenaPrincipal = new Scene(root);

        stage.setTitle("Listas");
        stage.setScene(escenaPrincipal);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e->{e.consume();
            cerrarPrograma(stage);});
    }

    private void cerrarPrograma(Stage ventana) {
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");

        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Seguro que desea salir?");
        alerta.getDialogPane().setHeaderText("SALIR");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) { ventana.close();}
    }
}
