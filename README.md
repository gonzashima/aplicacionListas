# Aplicación para Listas de Precios

## Estado del proyecto

| Módulo | Tecnología | Estado |
|--------|-----------|--------|
| `src/` | Java + JavaFX (aplicación de escritorio) | ⚠️ **DEPRECADO** — solo lectura, no se desarrolla más |
| `aplicacion-web/` | Java + Spring Boot (backend REST) | ✅ **Activo** — migración en curso |
| `frontend/` | React + Nginx | ✅ **Activo** — migración en curso |

---

## Descripción

Aplicación para leer listas de precios de distintos proveedores y actualizar precios en base de datos.

### Nueva versión (Spring + React)
- **Backend:** `aplicacion-web/` → Spring Boot, expone API REST
- **Frontend:** `frontend/` → React, se sirve con Nginx
- **Infraestructura:** `docker-compose.yml` para levantar todo el stack

### Versión legacy (JavaFX) — ⚠️ Deprecada
El código en `src/` corresponde a la aplicación de escritorio original construida con JavaFX.
Se conserva como referencia histórica durante la migración. **No debe modificarse.**
Ver [`README-LEGACY.md`](./README-LEGACY.md) para más detalles.

---

## Levantar el proyecto (nuevo)

```bash
docker-compose up --build
```

---

## Casas soportadas

- Difplast
- Lema
- Respontech
- Mafersa
- Rigolleau
- Rodeca
- Trébol (Duravit)
