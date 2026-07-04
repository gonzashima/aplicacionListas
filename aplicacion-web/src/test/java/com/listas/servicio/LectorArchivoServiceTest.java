package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.entity.Producto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LectorArchivoServiceTest {

    @Test
    void leerExcelDifPlastUsaNombreEnCdeYPrecioEnG() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        byte[] contenidoExcel;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();

            Row filaValida = sheet.createRow(0);
            filaValida.createCell(1).setCellValue("12345");
            filaValida.createCell(2).setCellValue("VASO");
            filaValida.createCell(3).setCellValue("PLASTICO");
            filaValida.createCell(4).setCellValue("500ML");
            filaValida.createCell(6).setCellValue(2500);

            Row filaInvalida = sheet.createRow(1);
            filaInvalida.createCell(1).setCellValue("ABC");
            filaInvalida.createCell(2).setCellValue("NO ENTRA");
            filaInvalida.createCell(6).setCellValue(999);

            workbook.write(os);
            contenidoExcel = os.toByteArray();
        }

        MultipartFile archivo = new MockMultipartFile(
                "archivo",
                "difplast.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                contenidoExcel
        );

        List<String> lineas = leerExcel(service, archivo, Casa.DIFPLAST);

        assertEquals(1, lineas.size());
        assertEquals("12345~VASO PLASTICO 500ML~2500", lineas.get(0));
    }

    @Test
    void leerExcelRespontechAceptaPrecioNumericoSinFormatoMoneda() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        byte[] contenidoExcel;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();

            Row filaValida = sheet.createRow(0);
            filaValida.createCell(0).setCellValue("ABC 123");
            filaValida.createCell(1).setCellValue("TAPA");
            filaValida.createCell(2).setCellValue("UN");
            filaValida.createCell(3).setCellValue(1475);

            workbook.write(os);
            contenidoExcel = os.toByteArray();
        }

        MultipartFile archivo = new MockMultipartFile(
                "archivo",
                "respontech.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                contenidoExcel
        );

        List<String> lineas = leerExcel(service, archivo, Casa.RESPONTECH);
        List<Producto> productos = parsearRespontech(service, lineas);

        assertEquals(1, lineas.size());
        assertEquals(1, productos.size());
        assertEquals("TAPA", productos.get(0).getNombre());
        assertEquals(1475, productos.get(0).getCosto());
    }

    @Test
    void leerExcelRespontechMantienePrecioConFormatoMonedaLegacy() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        byte[] contenidoExcel;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle monedaArgentina = workbook.createCellStyle();
            monedaArgentina.setDataFormat(dataFormat.getFormat("$#,##0.00"));

            Row filaValida = sheet.createRow(0);
            filaValida.createCell(0).setCellValue("123");
            filaValida.createCell(1).setCellValue("FRASCO");
            filaValida.createCell(2).setCellValue("UN");
            filaValida.createCell(3).setCellValue(1475);
            filaValida.getCell(3).setCellStyle(monedaArgentina);

            workbook.write(os);
            contenidoExcel = os.toByteArray();
        }

        MultipartFile archivo = new MockMultipartFile(
                "archivo",
                "respontech.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                contenidoExcel
        );

        List<String> lineas = leerExcel(service, archivo, Casa.RESPONTECH);
        List<Producto> productos = parsearRespontech(service, lineas);

        assertEquals(1, lineas.size());
        assertEquals(1, productos.size());
        assertEquals(123, productos.get(0).getCodigo());
        assertEquals("FRASCO", productos.get(0).getNombre());
        assertEquals(1475, productos.get(0).getCosto());
    }

    @Test
    void procesarArchivoRespontechLeeParseaEInsertaProductos() throws Exception {
        CapturadorProductoService productoService = new CapturadorProductoService();
        LectorArchivoService service = new LectorArchivoService(productoService);

        byte[] contenidoExcel;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();

            Row filaValida = sheet.createRow(0);
            filaValida.createCell(0).setCellValue("ABC 123");
            filaValida.createCell(1).setCellValue("TAPA");
            filaValida.createCell(2).setCellValue("UN");
            filaValida.createCell(3).setCellValue(1475);

            workbook.write(os);
            contenidoExcel = os.toByteArray();
        }

        MultipartFile archivo = new MockMultipartFile(
                "archivo",
                "respontech.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                contenidoExcel
        );

        String mensaje = service.procesarArchivo(archivo);

        assertEquals("respontech se leyó correctamente. 1 productos procesados.", mensaje);
        assertEquals(22, productoService.listaId);
        assertEquals(1, productoService.productos.size());
        assertEquals("TAPA", productoService.productos.get(0).getNombre());
        assertEquals(1475, productoService.productos.get(0).getCosto());
    }

    @Test
    void parsearDuravitExtraeNombreCodigoCostoYPrecio() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        List<Producto> productos = parsearDuravit(service, List.of("JARRA VIDRIO 12345 300.00"));

        assertEquals(1, productos.size());
        Producto producto = productos.get(0);
        assertEquals(12345, producto.getCodigo());
        assertEquals("JARRA VIDRIO", producto.getNombre());
        assertEquals(300, producto.getCosto());
        assertEquals(600, producto.getPrecio());
        assertEquals(1, producto.getListaId());
    }

    @Test
    void parsearRigolleauConcatenaCapacidadYAclaraciones() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        List<Producto> productos = parsearRigolleau(service, List.of("123:VASO:500 ML:12:$300,00:AZUL"));

        assertEquals(1, productos.size());
        Producto producto = productos.get(0);
        assertEquals(123, producto.getCodigo());
        assertEquals("VASO 500ML AZUL", producto.getNombre());
        assertEquals(300, producto.getCosto());
        assertEquals(650, producto.getPrecio());
        assertEquals(23, producto.getListaId());
    }

    @Test
    void parsearLemaSoportaCodigoDecimalDeExcel() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        List<Producto> productos = parsearLema(service, List.of("123.0~LAPIZ BIC NEGRO~300"));

        assertEquals(1, productos.size());
        Producto producto = productos.get(0);
        assertEquals(123, producto.getCodigo());
        assertEquals("LAPIZ BIC NEGRO", producto.getNombre());
        assertEquals(300, producto.getCosto());
        assertEquals(660, producto.getPrecio());
        assertEquals(24, producto.getListaId());
    }

    @Test
    void parsearRodecaUsaHashEstableParaCodigoAlfanumerico() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        List<Producto> productos = parsearRodeca(service, List.of("VASO VIDRIO", "Código: AB-1", "$300,00"));

        assertEquals(1, productos.size());
        Producto producto = productos.get(0);
        assertTrue(producto.getCodigo() >= 0);
        assertEquals("VASO VIDRIO", producto.getNombre());
        assertEquals(300, producto.getCosto());
        assertEquals(660, producto.getPrecio());
        assertEquals(25, producto.getListaId());
    }

    @Test
    void parsearDifPlastUsaFormatoSeparadoPorTilde() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        List<Producto> productos = parsearDifPlast(service, List.of("123~VASO PLASTICO~300"));

        assertEquals(1, productos.size());
        Producto producto = productos.get(0);
        assertEquals(123, producto.getCodigo());
        assertEquals("VASO PLASTICO", producto.getNombre());
        assertEquals(300, producto.getCosto());
        assertEquals(550, producto.getPrecio());
        assertEquals(26, producto.getListaId());
    }

    @Test
    void parsersDescartanLineasInvalidasSinRomperImportacion() throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);

        assertTrue(parsearDuravit(service, List.of("HEADER")).isEmpty());
        assertTrue(parsearRigolleau(service, List.of("123:INCOMPLETO")).isEmpty());
        assertTrue(parsearLema(service, List.of("123~SIN_COSTO")).isEmpty());
        assertTrue(parsearRodeca(service, List.of("NOMBRE")).isEmpty());
        assertTrue(parsearDifPlast(service, List.of("SIN_TILDES")).isEmpty());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "RUBRO : lumilagro|100 FABCOD BOTELLA ACERO 300|15|460",
            "RUBRO : lumilagro|101 FABCOD REPUESTO TERMO 300|15|500",
            "RUBRO : almandoz|102 FABCOD MATE RIVER 300|3|460"
    })
    void parsearMafersaRespetaSublistaYDistintos(String rubro, String linea, int listaId, int precio) throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);
        List<Producto> productos = parsearMafersa(service, List.of(rubro, linea));

        assertEquals(1, productos.size());
        assertEquals(listaId, productos.get(0).getListaId());
        assertEquals(precio, productos.get(0).getPrecio());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "$1.475,00|1475",
            "$ 1.475,00|1475",
            "$1,475.00|1475",
            "1,475|1475",
            "1475,50|1475",
            "1.475|1475",
            "1475|1475",
            "1475.50|1475"
    })
    void parsearCostoSoportaFormatosEsperados(String raw, int esperado) throws Exception {
        LectorArchivoService service = new LectorArchivoService(null);
        assertEquals(esperado, parsearCosto(service, raw));
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearMafersa(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearMafersa", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearDuravit(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearDuravit", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearRigolleau(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearRigolleau", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearLema(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearLema", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearRodeca(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearRodeca", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearDifPlast(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearDifPlast", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    private int parsearCosto(LectorArchivoService service, String costoRaw) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearCosto", String.class);
        method.setAccessible(true);
        return (int) method.invoke(service, costoRaw);
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearRespontech(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearRespontech", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    @SuppressWarnings("unchecked")
    private List<String> leerExcel(LectorArchivoService service, MultipartFile archivo, Casa casa) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("leerExcel", MultipartFile.class, Casa.class);
        method.setAccessible(true);
        return (List<String>) method.invoke(service, archivo, casa);
    }

    private static class CapturadorProductoService extends ProductoService {
        private List<Producto> productos = new ArrayList<>();
        private int listaId;

        CapturadorProductoService() {
            super(null, null);
        }

        @Override
        public int insertarProductos(List<Producto> productos, int listaId) {
            this.productos = new ArrayList<>(productos);
            this.listaId = listaId;
            return productos.size();
        }
    }
}
