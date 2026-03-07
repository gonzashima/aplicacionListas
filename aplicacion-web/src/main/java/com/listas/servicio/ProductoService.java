package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.constantes.CodigosListas;
import com.listas.modelo.entity.Lista;
import com.listas.modelo.entity.Producto;
import com.listas.modelo.repository.ListaRepository;
import com.listas.modelo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio principal para la gestión de productos.
 * Migrado de Aplicacion.java y ConectorDB.java del proyecto original.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ListaRepository listaRepository;

    public ProductoService(ProductoRepository productoRepository, ListaRepository listaRepository) {
        this.productoRepository = productoRepository;
        this.listaRepository = listaRepository;
    }

    // Nombres de producto que usan fórmula Lumilagro (IVA completo)
    // Migrado de ConstantesStrings.getDistintosLumilagro()
    private static final List<String> DISTINTOS_LUMILAGRO = List.of(
            "LUMINOX", "REPUESTO TERMO", "REPUESTO COMPACTO",
            "TAPON CEBADOR", "TAPON ESPIRAL", "TAPON P/TERMO",
            "TAPON P/JARRA", "TAPON PICO", "BOCA",
            "RIVER", "INDEPENDIENTE", "RACING", "ESTUDIANTES",
            "GIMNASIA", "SAN LORENZO", "SELECCION"
    );

    /**
     * Obtiene todos los productos de una lista y recalcula sus precios.
     */
    public List<Producto> obtenerProductosPorLista(String nombreLista) {
        int listaId = CodigosListas.codigoLista(nombreLista);
        List<Producto> productos = productoRepository.findByListaId(listaId);

        // Recalcular precios (como hacía el original al traer de DB)
        String tipoCasa = Casa.tipoCasaParaLista(nombreLista);
        for (Producto p : productos) {
            if (tipoCasa != null) {
                p.calcularPrecio(detectarTipoCasa(tipoCasa, p.getNombre()));
            }
        }

        return productos;
    }

    /**
     * Busca productos por nombre dentro de una lista.
     */
    public List<Producto> buscarProductos(String nombreLista, String busqueda) {
        int listaId = CodigosListas.codigoLista(nombreLista);
        List<Producto> productos = productoRepository.findByListaIdAndNombreContainingIgnoreCase(listaId, busqueda);

        String tipoCasa = Casa.tipoCasaParaLista(nombreLista);
        for (Producto p : productos) {
            if (tipoCasa != null) {
                p.calcularPrecio(detectarTipoCasa(tipoCasa, p.getNombre()));
            }
        }

        return productos;
    }

    /**
     * Para sublistas de Mafersa: detecta si un producto específico usa la fórmula
     * de Lumilagro (IVA completo) o Mafersa normal (medio IVA).
     * En el original: ProductoLumilagro si nombre contiene término especial Y contiene "LUMILAGRO".
     */
    private String detectarTipoCasa(String tipoCasaBase, String nombreProducto) {
        if ("mafersa".equals(tipoCasaBase) || "lumilagro".equals(tipoCasaBase)) {
            if (nombreProducto != null
                    && nombreProducto.contains("LUMILAGRO")
                    && DISTINTOS_LUMILAGRO.stream().anyMatch(nombreProducto::contains)) {
                return "lumilagro";
            }
            return "mafersa";
        }
        return tipoCasaBase;
    }

    /**
     * Modifica el porcentaje de los productos seleccionados y guarda los cambios.
     */
    @Transactional
    public int modificarPorcentaje(List<Integer> ids, int nuevoPorcentaje, String nombreLista) {
        String tipoCasa = Casa.tipoCasaParaLista(nombreLista);
        List<Producto> productos = productoRepository.findAllById(ids);

        for (Producto p : productos) {
            p.setPorcentaje(nuevoPorcentaje);
            if (tipoCasa != null) {
                p.calcularPrecio(detectarTipoCasa(tipoCasa, p.getNombre()));
            }
        }

        productoRepository.saveAll(productos);
        return productos.size();
    }

    /**
     * Modifica el costo de los productos seleccionados y recalcula precios.
     */
    @Transactional
    public int modificarCosto(List<Integer> ids, int nuevoCosto, String nombreLista) {
        String tipoCasa = Casa.tipoCasaParaLista(nombreLista);
        List<Producto> productos = productoRepository.findAllById(ids);

        for (Producto p : productos) {
            p.setCosto(nuevoCosto);
            if (tipoCasa != null) {
                p.calcularPrecio(detectarTipoCasa(tipoCasa, p.getNombre()));
            }
        }

        productoRepository.saveAll(productos);
        return productos.size();
    }

    /**
     * Inserta o reemplaza productos de una lista.
     * Si un producto ya existía (mismo código), conserva su porcentaje y recalcula el precio.
     * Esto reproduce la lógica original de los Parsers (insertarAlMapa).
     */
    @Transactional
    public int insertarProductos(List<Producto> productos, int listaId) {
        asegurarLista(listaId);

        // Cargar los productos existentes indexados por código
        List<Producto> existentes = productoRepository.findByListaId(listaId);
        Map<Integer, Producto> mapaPorCodigo = new HashMap<>();
        for (Producto p : existentes) {
            mapaPorCodigo.put(p.getCodigo(), p);
        }

        // Para cada producto nuevo, si ya existía por código, conservar porcentaje e id
        String tipoCasa = Casa.tipoCasaParaLista(CodigosListas.nombreLista(listaId));
        for (Producto nuevo : productos) {
            Producto anterior = mapaPorCodigo.get(nuevo.getCodigo());
            if (anterior != null) {
                nuevo.setPorcentaje(anterior.getPorcentaje());
                nuevo.setId(anterior.getId());
                if (tipoCasa != null) {
                    nuevo.calcularPrecio(detectarTipoCasa(tipoCasa, nuevo.getNombre()));
                }
            }
        }

        // Borrar los que ya no existen y guardar los nuevos
        productoRepository.deleteByListaId(listaId);
        productoRepository.saveAll(productos);
        return productos.size();
    }

    /**
     * Versión para Mafersa: agrupa los productos por su lista_id (sublista)
     * y los inserta/reemplaza sublista por sublista, preservando porcentajes.
     */
    @Transactional
    public int insertarProductosMafersa(List<Producto> productos) {
        // Agrupar por lista_id
        Map<Integer, List<Producto>> porLista = new HashMap<>();
        for (Producto p : productos) {
            porLista.computeIfAbsent(p.getListaId(), k -> new ArrayList<>()).add(p);
        }
        int total = 0;
        for (Map.Entry<Integer, List<Producto>> entry : porLista.entrySet()) {
            total += insertarProductos(entry.getValue(), entry.getKey());
        }
        return total;
    }

    private void asegurarLista(int listaId) {
        if (!listaRepository.existsById(listaId)) {
            String nombreLista = CodigosListas.nombreLista(listaId);
            if (nombreLista != null) {
                Lista lista = new Lista(listaId, nombreLista.toUpperCase(), nombreLista.toUpperCase());
                listaRepository.save(lista);
            }
        }
    }
}

