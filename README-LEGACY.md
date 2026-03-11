# ⚠️ Módulo Legacy — Aplicación de Escritorio (JavaFX)

> **Este módulo está DEPRECADO.**
> El código se conserva únicamente como referencia histórica durante la migración a Spring + React.
> **No debe modificarse ni desplegarse.**

---

## Descripción

Aplicación de escritorio construida con Java + JavaFX que permitía:

- Leer listas de precios en formato Excel (`.xls` / `.xlsx`) y PDF
- Parsear productos de distintos proveedores
- Insertar/actualizar precios en base de datos MySQL
- Aplicar porcentajes de ganancia por producto

## Tecnologías

- Java 17+
- JavaFX
- Apache POI (lectura de Excel)
- PDFBox (lectura de PDF)
- MySQL (JDBC)

## Estructura

```
src/
└── main/
    ├── java/
    │   ├── Controladores/       # Controladores de pantallas FXML
    │   │   ├── Alertas/         # Diálogos de alerta
    │   │   └── Ventanas/        # Ventanas principales
    │   └── Modelo/
    │       ├── Aplicacion.java  # Lógica central
    │       ├── Constantes/      # Strings y números constantes
    │       ├── Insertadores/    # Inserción en DB
    │       ├── Lectores/        # Lectura de archivos Excel/PDF por casa
    │       ├── Parsers/         # Parseo de texto a objetos Producto
    │       ├── Productos/       # Entidades (ProductoLema, ProductoDifPlast, etc.)
    │       └── Utils/           # ConectorDB, Triple, etc.
    └── resources/
        ├── icon.png
        └── fxml/                # Layouts de pantallas JavaFX
```

## Estado de la migración

| Componente legacy | Equivalente nuevo |
|-------------------|-------------------|
| `LectorDifPlast.java` | `LectorArchivoService` — caso `DIFPLAST` |
| `LectorLema.java` | `LectorArchivoService` — caso `LEMA` |
| `LectorRespontech.java` | `LectorArchivoService` — caso `RESPONTECH` |
| `LectorMafersa.java` | `LectorArchivoService` — `leerPdfMafersa()` |
| `LectorRigolleau.java` | `LectorArchivoService` — caso `RIGOLLEAU` |
| `LectorRodeca.java` | `LectorArchivoService` — caso `RODECA` |
| `ParserDifPlast.java` | `LectorArchivoService.parsearDifPlast()` |
| `ParserLema.java` | `LectorArchivoService.parsearLema()` |
| `ParserRespontech.java` | `LectorArchivoService.parsearRespontech()` |
| `ParserMafersa.java` | `LectorArchivoService.parsearMafersa()` |
| `ParserRigolleau.java` | `LectorArchivoService.parsearRigolleau()` |
| `ParserRodeca.java` | `LectorArchivoService.parsearRodeca()` |
| `ParserDuravit.java` | `LectorArchivoService.parsearDuravit()` |
| `InsertadorMafersa.java` | `ProductoService.insertarProductosMafersa()` |
| `ConectorDB.java` | Spring Data JPA + repositorios |
| Pantallas FXML | Componentes React en `frontend/src/components/` |

---

*Última versión desplegada: **1.5.2-alpha***

