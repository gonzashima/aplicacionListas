package Modelo.Utils;

import Modelo.Lectores.LectorArchivos;
import Modelo.Parsers.Parser;

public record Resultado(LectorArchivos lector, Parser parser) {
}
