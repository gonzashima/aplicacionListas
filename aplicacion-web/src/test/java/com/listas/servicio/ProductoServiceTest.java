package com.listas.servicio;

import com.listas.modelo.entity.Lista;
import com.listas.modelo.entity.Producto;
import com.listas.modelo.repository.ListaRepository;
import com.listas.modelo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoServiceTest {

    @ParameterizedTest
    @CsvSource({
            "mafersa,'MATE RIVER 1L',mafersa",
            "lumilagro,'repuesto termo universal',lumilagro",
            "lumilagro,'JARRA ACERO INOX',mafersa",
            "duravit,'PRODUCTO X',duravit"
    })
    void detectarTipoCasaRespetaReglas(String base, String nombre, String esperado) throws Exception {
        ProductoService service = new ProductoService(null, null);
        assertEquals(esperado, detectarTipoCasa(service, base, nombre));
    }

    @Test
    void insertarProductosReemplazaListaYConservaPorcentajeEIdPorCodigo() {
        RepositoriosEnMemoria repos = new RepositoriosEnMemoria();
        Producto existente = producto(10, 123, "VASO VIEJO", 300, 80, 22);
        Producto eliminado = producto(11, 456, "NO VIENE MAS", 100, 70, 22);
        repos.productos.add(existente);
        repos.productos.add(eliminado);
        ProductoService service = new ProductoService(repos.productoRepository(), repos.listaRepository());

        Producto actualizado = producto(null, 123, "VASO NUEVO", 400, 100, 22);
        Producto nuevo = producto(null, 999, "BOTELLA", 200, 100, 22);
        int cantidad = service.insertarProductos(List.of(actualizado, nuevo), 22);

        assertEquals(2, cantidad);
        assertEquals(2, repos.productos.size());

        Producto preservado = repos.productos.stream()
                .filter(p -> p.getCodigo() == 123)
                .findFirst()
                .orElseThrow();
        assertEquals(10, preservado.getId());
        assertEquals("VASO NUEVO", preservado.getNombre());
        assertEquals(400, preservado.getCosto());
        assertEquals(80, preservado.getPorcentaje());
        assertEquals(700, preservado.getPrecio());

        assertTrue(repos.productos.stream().noneMatch(p -> p.getCodigo() == 456));
        assertTrue(repos.productos.stream().anyMatch(p -> p.getCodigo() == 999));
    }

    @Test
    void insertarProductosCreaListaSiNoExiste() {
        RepositoriosEnMemoria repos = new RepositoriosEnMemoria();
        ProductoService service = new ProductoService(repos.productoRepository(), repos.listaRepository());

        service.insertarProductos(List.of(producto(null, 1, "VASO", 300, 100, 22)), 22);

        assertEquals(1, repos.listas.size());
        assertEquals(22, repos.listas.get(0).getId());
        assertEquals("RESPONTECH", repos.listas.get(0).getNombre());
    }

    @Test
    void modificarPorcentajeRecalculaPrecioSegunLista() {
        RepositoriosEnMemoria repos = new RepositoriosEnMemoria();
        Producto producto = producto(1, 123, "VASO", 300, 100, 22);
        repos.productos.add(producto);
        ProductoService service = new ProductoService(repos.productoRepository(), repos.listaRepository());

        int cantidad = service.modificarPorcentaje(List.of(1), 50, "RESPONTECH");

        assertEquals(1, cantidad);
        assertEquals(50, producto.getPorcentaje());
        assertEquals(450, producto.getPrecio());
    }

    @Test
    void buscarProductosFiltraPorNombreDentroDeLista() {
        RepositoriosEnMemoria repos = new RepositoriosEnMemoria();
        repos.productos.add(producto(1, 123, "VASO ALTO", 300, 100, 22));
        repos.productos.add(producto(2, 456, "BOTELLA", 300, 100, 22));
        repos.productos.add(producto(3, 789, "VASO OTRA LISTA", 300, 100, 23));
        ProductoService service = new ProductoService(repos.productoRepository(), repos.listaRepository());

        List<Producto> encontrados = service.buscarProductos("RESPONTECH", "vaso");

        assertEquals(1, encontrados.size());
        assertEquals("VASO ALTO", encontrados.get(0).getNombre());
    }

    private String detectarTipoCasa(ProductoService service, String tipoCasaBase, String nombreProducto) throws Exception {
        Method method = ProductoService.class.getDeclaredMethod("detectarTipoCasa", String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, tipoCasaBase, nombreProducto);
    }

    private static Producto producto(Integer id, int codigo, String nombre, int costo, int porcentaje, int listaId) {
        Producto producto = new Producto(codigo, nombre, costo, porcentaje, listaId);
        producto.setId(id);
        producto.calcularPrecio("respontech");
        return producto;
    }

    private static class RepositoriosEnMemoria {
        private final List<Producto> productos = new ArrayList<>();
        private final List<Lista> listas = new ArrayList<>();
        private int proximoId = 1000;

        ProductoRepository productoRepository() {
            return proxy(ProductoRepository.class, (proxy, method, args) -> switch (method.getName()) {
                case "findByListaId" -> productos.stream()
                        .filter(p -> p.getListaId() == (int) args[0])
                        .toList();
                case "findByListaIdAndNombreContainingIgnoreCase" -> productos.stream()
                        .filter(p -> p.getListaId() == (int) args[0])
                        .filter(p -> p.getNombre().toLowerCase().contains(((String) args[1]).toLowerCase()))
                        .toList();
                case "findAllById" -> buscarPorIds((Iterable<Integer>) args[0]);
                case "deleteByListaId" -> {
                    productos.removeIf(p -> p.getListaId() == (int) args[0]);
                    yield null;
                }
                case "saveAll" -> guardarTodos((Iterable<Producto>) args[0]);
                case "save" -> guardar((Producto) args[0]);
                default -> metodoObjectOUnsupported(proxy, method, args);
            });
        }

        ListaRepository listaRepository() {
            return proxy(ListaRepository.class, (proxy, method, args) -> switch (method.getName()) {
                case "existsById" -> listas.stream().anyMatch(l -> l.getId().equals(args[0]));
                case "save" -> {
                    Lista lista = (Lista) args[0];
                    listas.removeIf(l -> l.getId().equals(lista.getId()));
                    listas.add(lista);
                    yield lista;
                }
                default -> metodoObjectOUnsupported(proxy, method, args);
            });
        }

        private List<Producto> buscarPorIds(Iterable<Integer> ids) {
            List<Integer> solicitados = new ArrayList<>();
            ids.forEach(solicitados::add);
            return productos.stream()
                    .filter(p -> solicitados.contains(p.getId()))
                    .toList();
        }

        private List<Producto> guardarTodos(Iterable<Producto> productosAGuardar) {
            List<Producto> guardados = new ArrayList<>();
            for (Producto producto : productosAGuardar) {
                guardados.add(guardar(producto));
            }
            return guardados;
        }

        private Producto guardar(Producto producto) {
            if (producto.getId() == null) {
                producto.setId(proximoId++);
            }
            productos.removeIf(p -> p.getId().equals(producto.getId()));
            productos.add(producto);
            return producto;
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(Class<T> tipo, InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, handler);
        }

        private static Object metodoObjectOUnsupported(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "toString" -> proxy.getClass().getInterfaces()[0].getSimpleName() + "Proxy";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Metodo no implementado en test: " + method.getName());
            };
        }
    }
}
