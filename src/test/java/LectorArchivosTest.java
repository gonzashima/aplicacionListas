import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LectorArchivosTest {

    @Test
    public void archivoExisteTest(){
        File archivo = new File("src/main/resources/DURAVIT.pdf");
        assertNotNull(archivo);
    }
}
