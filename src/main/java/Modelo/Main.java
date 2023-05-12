package Modelo;

import Modelo.Constantes.ConstantesNumericas;
import Modelo.Constantes.ConstantesStrings;
import Modelo.Lectores.LectorRigolleau;
import Modelo.Parsers.Parser;
import Modelo.Parsers.ParserRespontech;
import Modelo.Parsers.ParserRigolleau;
import Modelo.Productos.Producto;
import Modelo.Utils.ConectorDB;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

        stage.setMinHeight(700);
        stage.setMinWidth(1200);

        stage.setMaxHeight(700);
        stage.setMaxWidth(1200);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/icon.png"))));

        stage.show();
        root.requestFocus();   //hace que nada este seleccionado al iniciar la aplicacion

        stage.setOnCloseRequest(e -> {
            e.consume();
            try {
                cerrarPrograma(stage);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void cerrarPrograma(Stage ventana) throws SQLException {
        Alert.AlertType tipo = Alert.AlertType.CONFIRMATION;
        Alert alerta = new Alert(tipo, "");

        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(ventana);
        alerta.getDialogPane().setContentText("Seguro que desea salir?");
        alerta.getDialogPane().setHeaderText("SALIR");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            ventana.close();
            ConectorDB.close();
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        launch(args);
//        LectorRigolleau lectorRigolleau = new LectorRigolleau();
//        List<String> texto  = lectorRigolleau.leerArchivo(null);
//        Parser parserRigolleau = new ParserRigolleau();
//
//        HashMap<Integer, HashMap<Integer, Producto>> datos = new HashMap<>();
//        parserRigolleau.parsearAProducto(texto, datos);
//
//        HashMap<Integer,Producto> rigolleau = datos.get(ConstantesNumericas.codigoLista(ConstantesStrings.RIGOLLEAU));
//
//        for (int codigo : rigolleau.keySet()) {
//            System.out.println(codigo + " " + rigolleau.get(codigo));
//        }
//
//        System.out.println(rigolleau.keySet().size());


    }
}