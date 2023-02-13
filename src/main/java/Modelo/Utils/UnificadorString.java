package Modelo.Utils;

public class UnificadorString {
    public static String unirString(String string) {
        String[] separado;
        if (!string.contains(" ") && !string.contains("-"))
            return string;
        else if (string.contains(" "))
            separado = string.trim().split(" ");
        else
            separado = string.trim().split("-");
        return String.join("_", separado);
    }
}
