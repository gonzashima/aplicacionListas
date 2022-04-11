import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        AnchorPane anchorPane = new AnchorPane();
        Scene escenaPrincipal = new Scene(anchorPane);

        stage.setScene(escenaPrincipal);
        stage.show();
    }
}
