import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

// ==================== CASAS ====================

export const getCasas = () => api.get('/casas');

export const getListasPorCasa = (casa) => api.get(`/casas/${casa}/listas`);

// ==================== PRODUCTOS ====================

export const getProductos = (lista, busqueda = '') =>
  api.get('/productos', { params: { lista, busqueda: busqueda || undefined } });

export const modificarPorcentaje = (ids, valor, lista) =>
  api.put('/productos/porcentaje', { ids, valor, lista });

export const modificarCosto = (ids, valor, lista) =>
  api.put('/productos/costo', { ids, valor, lista });

// ==================== ARCHIVOS ====================

export const subirArchivo = (archivo) => {
  const formData = new FormData();
  formData.append('archivo', archivo);
  return api.post('/archivos/subir', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

// ==================== EXCEL ====================

export const exportarExcel = (lista) =>
  api.get('/productos/excel', {
    params: { lista },
    responseType: 'blob',
  });

export const exportarCarteles = (ids, lista) =>
  api.post('/productos/carteles', { ids, lista }, { responseType: 'blob' });

