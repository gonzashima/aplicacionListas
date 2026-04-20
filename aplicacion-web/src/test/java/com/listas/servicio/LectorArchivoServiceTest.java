package com.listas.servicio;

import com.listas.modelo.constantes.Casa;
import com.listas.modelo.entity.Producto;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LectorArchivoServiceTest {

    @Test
    void leerExcelDifPlastUsaNombreEnCdeYPrecioEnG() throws Exception {
        LectorArchivoService service = new LectorArchivoService(mock(ProductoService.class));

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

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "RUBRO : lumilagro|100 FABCOD BOTELLA ACERO 300|15|460",
            "RUBRO : lumilagro|101 FABCOD REPUESTO TERMO 300|15|500",
            "RUBRO : almandoz|102 FABCOD MATE RIVER 300|3|460"
    })
    void parsearMafersaRespetaSublistaYDistintos(String rubro, String linea, int listaId, int precio) throws Exception {
        LectorArchivoService service = new LectorArchivoService(mock(ProductoService.class));
        List<Producto> productos = parsearMafersa(service, List.of(rubro, linea));

        assertEquals(1, productos.size());
        assertEquals(listaId, productos.get(0).getListaId());
        assertEquals(precio, productos.get(0).getPrecio());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "$1.475,00|1475",
            "$ 1.475,00|1475",
            "1.475|1475",
            "1475|1475",
            "1475.50|1475"
    })
    void parsearCostoSoportaFormatosEsperados(String raw, int esperado) throws Exception {
        LectorArchivoService service = new LectorArchivoService(mock(ProductoService.class));
        assertEquals(esperado, parsearCosto(service, raw));
    }

    @SuppressWarnings("unchecked")
    private List<Producto> parsearMafersa(LectorArchivoService service, List<String> lineas) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearMafersa", List.class);
        method.setAccessible(true);
        return (List<Producto>) method.invoke(service, lineas);
    }

    private int parsearCosto(LectorArchivoService service, String costoRaw) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("parsearCosto", String.class);
        method.setAccessible(true);
        return (int) method.invoke(service, costoRaw);
    }

    @SuppressWarnings("unchecked")
    private List<String> leerExcel(LectorArchivoService service, MultipartFile archivo, Casa casa) throws Exception {
        Method method = LectorArchivoService.class.getDeclaredMethod("leerExcel", MultipartFile.class, Casa.class);
        method.setAccessible(true);
        return (List<String>) method.invoke(service, archivo, casa);
    }
}



