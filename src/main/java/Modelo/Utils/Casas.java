package Modelo.Utils;

/**
 * Son las diferentes casas o proveedores, que pueden tener varias listas
 * */
public enum Casas {
    TREBOL(new String[]{"DURAVIT"}, "TREBOL"),
    MAFERSA(new String[]{"DESES PLAST", "ALMANDOZ", "BEL GIOCO", "NADIR", "TRAMONTINA",
    "WHEATON", "CAMPAGNA", "CHEF", "CHURRASQUERAS Y SARTENES", "KUFO", "DAYSAL", "GUADIX", "LOEKEMEYER", "LUMILAGRO",
    "MAN FER", "MARINEX", "COLORES", "DATOMAX", "PLASTIC HOUSE", "YESI"}, "MAFERSA");



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
