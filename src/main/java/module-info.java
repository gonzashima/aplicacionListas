module aplicacionListas {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires java.sql;

    exports controladores to javafx.fxml;

    opens modelo;
    opens modelo.Estado;
    opens modelo.Utils;
    opens modelo.Productos;
    opens controladores to javafx.fxml, javafx.base;
    opens modelo.Lectores;
}