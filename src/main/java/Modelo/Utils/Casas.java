package Modelo.Utils;

/**
 * Son las diferentes casas o proveedores, que pueden tener varias listas
 * */
public enum Casas {
    TREBOL(new String[]{"1-DURAVIT"}, "TREBOL"),
    MAFERSA(new String[]{"2-DESES PLAST", "3-ALMANDOZ", "4-BEL GIOCO", "5-NADIR", "6-TRAMONTINA",
    "7-WHEATON", "8-CAMPAGNA", "9-CHEF", "10-CHURRASQUERAS Y SARTENES", "11-KUFO", "12-DAYSAL", "13-GUADIX", "14-LOEKEMEYER", "15-LUMILAGRO",
    "16-MAN FER", "17-MARINEX", "18-COLORES", "19-DATOMAX", "20-PLASTIC HOUSE", "21-YESI"}, "MAFERSA"),
    RESPONTECH(new String[]{"22-RESPONTECH"}, "RESPONTECH");


    private final String[] listas;
    private final String nombre;

    Casas(String[] listas, String nombre) {
        this.listas = listas;
        this.nombre = nombre;
    }

    public String[] getListas() {
        return listas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
