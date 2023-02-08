package Modelo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/fxml/PantallaPrincipal.fxml")));
        Scene escenaPrincipal = new Scene(root);

        stage.setTitle("Listas");
        stage.setScene(escenaPrincipal);
        stage.setResizable(false);

        stage.setMinHeight(750);
        stage.setMinWidth(1200);

        stage.setMaxHeight(750);
        stage.setMaxWidth(1200);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/icon.png"))));

        stage.show();
        root.requestFocus();   //hace que nada este seleccionado al iniciar la aplicacion

        stage.setOnCloseRequest(e -> {
            e.consume();
            cerrarPrograma(stage);
        });
    }

    private void cerrarPrograma(Stage ventana) {
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");

        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Seguro que desea salir?");
        alerta.getDialogPane().setHeaderText("SALIR");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            ventana.close();
        }
    }

    public static void main(String[] args) throws IOException {
        launch(args);
//        LectorMafersa lectorMafersa = new LectorMafersa();
//        ArrayList<String> texto = lectorMafersa.leerArchivo(null);
//
//        for(String s: texto)
//            System.out.println(s);
    }
}