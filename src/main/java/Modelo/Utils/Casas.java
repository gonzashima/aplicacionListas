package Modelo.Utils;

/**
 * Son las diferentes casas o proveedores, que pueden tener varias listas
 * */
public enum Casas {
    TREBOL(new String[]{"Duravit"}, "Trebol"),
    MAFERSA(new String[]{"Deses Plast"}, "Mafersa");

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