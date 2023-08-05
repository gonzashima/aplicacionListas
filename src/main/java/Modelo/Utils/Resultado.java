package Modelo.Utils;

import Modelo.Insertadores.Insertador;
import Modelo.Lectores.LectorArchivos;
import Modelo.Parsers.Parser;

/**
 * Modela el trio de utilidades necesarias al leer un archivo
 * */
public record Resultado(LectorArchivos lector, Parser parser, Insertador insertador) {
}
