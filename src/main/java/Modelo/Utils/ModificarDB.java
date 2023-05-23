package Modelo.Utils;

import java.sql.SQLException;

public class ModificarDB {
    public void modificarDB() throws SQLException {
        ConectorDB.getConnection();
        ConectorDB.modificarID();
    }
}
