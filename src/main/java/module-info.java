module aplicacionListas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires java.sql;

    opens modelo;
    opens modelo.Estado;
    opens modelo.Utils;
}