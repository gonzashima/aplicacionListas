package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.constantes.CodigosListas;
import com.listas.modelo.entity.Lista;
import com.listas.modelo.entity.Producto;
import com.listas.modelo.repository.ListaRepository;
import com.listas.modelo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                p.calcularPrecio(tipoCasa);
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
                p.calcularPrecio(tipoCasa);
            }
        }

        return productos;
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
                p.calcularPrecio(tipoCasa);
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
                p.calcularPrecio(tipoCasa);
            }
        }

        productoRepository.saveAll(productos);
        return productos.size();
    }

    /**
     * Inserta o reemplaza productos de una lista.
     * Primero borra los existentes de esa lista y luego inserta los nuevos.
     */
    @Transactional
    public int insertarProductos(List<Producto> productos, int listaId) {
        // Asegurar que la lista exista
        String nombreLista = CodigosListas.nombreLista(listaId);
        if (nombreLista != null && !listaRepository.existsById(listaId)) {
            Lista lista = new Lista(listaId, nombreLista.toUpperCase(), nombreLista.toUpperCase());
            listaRepository.save(lista);
        }

        // Borrar productos existentes de esa lista
        productoRepository.deleteByListaId(listaId);

        // Insertar los nuevos
        productoRepository.saveAll(productos);
        return productos.size();
    }
}

