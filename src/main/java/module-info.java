module aplicacionListas {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    exports Controladores.Ventanas to javafx.fxml;

    opens Modelo;
    opens Modelo.Estado;
    opens Modelo.Utils;
    exports Modelo.Productos;
    opens Controladores.Ventanas to javafx.fxml, javafx.base;
    opens Modelo.Lectores;
    exports Controladores.Alertas to javafx.fxml;
    opens Controladores.Alertas to javafx.base, javafx.fxml;
    opens Modelo.Constantes;
}