package Modelo.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ModificarDB {
    public void modificarDB() throws SQLException {
        ConectorDB.getConnection();
        ConectorDB.modificarID();
    }
}
