package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.ConectorDB;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class PantallaPrincipalControl implements Initializable {

    @FXML private ChoiceBox<String> opcionesListas;

    @FXML private Label advertencia, advertenciaDB;

    @FXML private TableView<ModeloTabla> tabla;

    @FXML private TableColumn<ModeloTabla, String> codigo, nombre, costo, precio;

    private final String[] listas = {"Duravit"};

    private final ObservableList<ModeloTabla> listaOb = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opcionesListas.getItems().addAll(listas);
        advertencia.setVisible(false);
        advertenciaDB.setVisible(false);

        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        costo.setCellValueFactory(new PropertyValueFactory<>("costo"));
        precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    public void apretoBotonMostrar(ActionEvent e) throws SQLException {
        String opcion = opcionesListas.getValue();
        if (opcion == null) {
            advertencia.setVisible(true);
        }
        else {
            if(advertencia.isVisible())
                advertencia.setVisible(false);

            Connection connection = ConectorDB.getConnection();

            if(connection != null && listaOb.isEmpty()) {
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * from productos WHERE precio != 0");

                while (rs.next()) {
                    listaOb.add(new ModeloTabla(rs.getString("codigo"), rs.getString("nombre"),
                            rs.getInt("costo"), rs.getInt("precio")));
                }
                tabla.setItems(listaOb);
            } else if (connection == null)
                advertenciaDB.setVisible(true);

        }
    }
}
