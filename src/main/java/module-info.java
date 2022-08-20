module aplicacionListas {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires java.sql;

    exports Controladores to javafx.fxml;

    opens Modelo;
    opens Modelo.Estado;
    opens Modelo.Utils;
    opens Modelo.Productos;
    opens Controladores to javafx.fxml, javafx.base;
    opens Modelo.Lectores;
}