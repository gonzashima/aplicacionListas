package Modelo.Utils;

import Modelo.Insertadores.Insertador;
import Modelo.Lectores.LectorArchivos;
import Modelo.Parsers.Parser;

public record Resultado(LectorArchivos lector, Parser parser, Insertador insertador) {
}
