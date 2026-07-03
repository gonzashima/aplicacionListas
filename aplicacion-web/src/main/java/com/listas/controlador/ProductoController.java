package com.listas.controlador;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.entity.Producto;
import com.listas.servicio.ExcelService;
import com.listas.servicio.LectorArchivoService;
import com.listas.servicio.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * API REST para la gestión de productos y listas.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;
    private final LectorArchivoService lectorArchivoService;
    private final ExcelService excelService;

    public ProductoController(ProductoService productoService,
                              LectorArchivoService lectorArchivoService,
                              ExcelService excelService) {
        this.productoService = productoService;
        this.lectorArchivoService = lectorArchivoService;
        this.excelService = excelService;
    }

    // ==================== CASAS ====================

    /**
     * Devuelve todas las casas con sus listas.
     */
    @GetMapping("/casas")
    public List<Map<String, Object>> obtenerCasas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Casa casa : Casa.values()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", casa.name());
            map.put("nombre", casa.getNombre());
            map.put("listas", casa.getListas());
            resultado.add(map);
        }
        return resultado;
    }

    /**
     * Devuelve las listas de una casa específica.
     */
    @GetMapping("/casas/{casa}/listas")
    public ResponseEntity<List<String>> obtenerListasPorCasa(@PathVariable String casa) {
        try {
            Casa casaEnum = Casa.valueOf(casa.toUpperCase());
            return ResponseEntity.ok(casaEnum.getListas());
        } catch (IllegalArgumentException e) {
            log.warn("Casa desconocida solicitada: {}", casa);
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== PRODUCTOS ====================

    /**
     * Obtiene los productos de una lista, con búsqueda opcional.
     */
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerProductos(
            @RequestParam String lista,
            @RequestParam(required = false) String busqueda) {
        try {
            List<Producto> productos;
            if (busqueda != null && !busqueda.isBlank()) {
                productos = productoService.buscarProductos(lista, busqueda);
            } else {
                productos = productoService.obtenerProductosPorLista(lista);
            }
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            log.warn("No se pudieron obtener productos. lista={}, busqueda={}", lista, busqueda, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Modifica el porcentaje de los productos seleccionados.
     */
    @PutMapping("/productos/porcentaje")
    public ResponseEntity<Map<String, Object>> modificarPorcentaje(@RequestBody ModificarRequest request) {
        try {
            int cantidad = productoService.modificarPorcentaje(request.ids(), request.valor(), request.lista());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Se actualizó el porcentaje de " + cantidad + " producto(s).",
                    "cantidad", cantidad
            ));
        } catch (Exception e) {
            log.warn("No se pudo modificar porcentaje. lista={}, ids={}, valor={}",
                    request.lista(), request.ids(), request.valor(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Modifica el costo de los productos seleccionados.
     */
    @PutMapping("/productos/costo")
    public ResponseEntity<Map<String, Object>> modificarCosto(@RequestBody ModificarRequest request) {
        try {
            int cantidad = productoService.modificarCosto(request.ids(), request.valor(), request.lista());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Se actualizó el costo de " + cantidad + " producto(s).",
                    "cantidad", cantidad
            ));
        } catch (Exception e) {
            log.warn("No se pudo modificar costo. lista={}, ids={}, valor={}",
                    request.lista(), request.ids(), request.valor(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ARCHIVOS ====================

    /**
     * Sube y procesa un archivo de lista de precios.
     */
    @PostMapping("/archivos/subir")
    public ResponseEntity<Map<String, String>> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {
        try {
            String mensaje = lectorArchivoService.procesarArchivo(archivo);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            log.error("No se pudo procesar archivo. nombre={}, size={}",
                    archivo.getOriginalFilename(), archivo.getSize(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Exporta los productos de una lista a Excel.
     */
    @GetMapping("/productos/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam String lista,
            @RequestParam(required = false) String ids) throws IOException {
        try {
            List<Producto> productos = filtrarPorIds(productoService.obtenerProductosPorLista(lista), parsearIds(ids));
            byte[] excelBytes = excelService.crearLista(productos);

            String filename = lista.toLowerCase().replace(" ", "_") + "_productos.xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
        } catch (IOException | RuntimeException e) {
            log.error("No se pudo exportar lista a Excel. lista={}, ids={}", lista, ids, e);
            throw e;
        }
    }

    /**
     * Exporta productos seleccionados como carteles.
     */
    @PostMapping("/productos/carteles")
    public ResponseEntity<byte[]> exportarCarteles(@RequestBody CartelesRequest request) throws IOException {
        try {
            List<Producto> seleccionados = filtrarPorIds(productoService.obtenerProductosPorLista(request.lista()), request.ids());

            byte[] excelBytes = excelService.crearCarteles(seleccionados);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=carteles.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
        } catch (IOException | RuntimeException e) {
            log.error("No se pudieron exportar carteles. lista={}, ids={}", request.lista(), request.ids(), e);
            throw e;
        }
    }

    // ==================== DTOs ====================

    public record ModificarRequest(List<Integer> ids, int valor, String lista) {}
    public record CartelesRequest(List<Integer> ids, String lista) {}

    private static Set<Integer> parsearIds(String idsCsv) {
        if (idsCsv == null || idsCsv.isBlank()) {
            return Set.of();
        }

        Set<Integer> ids = new LinkedHashSet<>();
        for (String parte : idsCsv.split(",")) {
            String valor = parte.trim();
            if (valor.isEmpty()) {
                continue;
            }
            ids.add(Integer.parseInt(valor));
        }
        return ids;
    }

    private static List<Producto> filtrarPorIds(List<Producto> todos, Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return todos;
        }

        Set<Integer> setIds = ids instanceof Set<Integer> set ? set : new HashSet<>(ids);
        return todos.stream()
                .filter(p -> setIds.contains(p.getId()))
                .toList();
    }
}
