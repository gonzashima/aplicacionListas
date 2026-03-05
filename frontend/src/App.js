import React, { useState, useEffect, useCallback } from 'react';
import { getCasas, getProductos, subirArchivo, modificarPorcentaje, modificarCosto, exportarExcel, exportarCarteles } from './api';
import TablaPrincipal from './components/TablaPrincipal';
import BarraHerramientas from './components/BarraHerramientas';
import ModalPorcentaje from './components/ModalPorcentaje';
import ModalCosto from './components/ModalCosto';
import ModalArchivo from './components/ModalArchivo';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  // Datos
  const [casas, setCasas] = useState([]);
  const [casaSeleccionada, setCasaSeleccionada] = useState('');
  const [listaSeleccionada, setListaSeleccionada] = useState('');
  const [productos, setProductos] = useState([]);
  const [seleccionados, setSeleccionados] = useState([]); // ids seleccionados

  // UI
  const [cargando, setCargando] = useState(false);
  const [busqueda, setBusqueda] = useState('');
  const [mensaje, setMensaje] = useState(null); // { tipo: 'success'|'error', texto }

  // Modales
  const [modalPorcentaje, setModalPorcentaje] = useState(false);
  const [modalCosto, setModalCosto] = useState(false);
  const [modalArchivo, setModalArchivo] = useState(false);

  // Cargar casas al inicio
  useEffect(() => {
    getCasas()
      .then((res) => setCasas(res.data))
      .catch(() => mostrarMensaje('error', 'No se pudo conectar al servidor'));
  }, []);

  const mostrarMensaje = (tipo, texto) => {
    setMensaje({ tipo, texto });
    setTimeout(() => setMensaje(null), 4000);
  };

  // Cargar productos cuando se selecciona una lista
  const cargarProductos = useCallback((lista, texto = '') => {
    if (!lista) return;
    setCargando(true);
    setSeleccionados([]);
    getProductos(lista, texto)
      .then((res) => {
        setProductos(res.data);
      })
      .catch(() => mostrarMensaje('error', 'Error al cargar los productos'))
      .finally(() => setCargando(false));
  }, []);

  const handleMostrar = () => {
    setBusqueda('');
    cargarProductos(listaSeleccionada);
  };

  const handleBuscar = () => {
    cargarProductos(listaSeleccionada, busqueda);
  };

  // Modificar porcentaje
  const handleConfirmarPorcentaje = async (valor) => {
    try {
      const ids = seleccionados;
      await modificarPorcentaje(ids, valor, listaSeleccionada);
      mostrarMensaje('success', `Porcentaje actualizado en ${ids.length} producto(s)`);
      setModalPorcentaje(false);
      cargarProductos(listaSeleccionada, busqueda);
    } catch {
      mostrarMensaje('error', 'Error al modificar el porcentaje');
    }
  };

  // Modificar costo
  const handleConfirmarCosto = async (valor) => {
    try {
      const ids = seleccionados;
      await modificarCosto(ids, valor, listaSeleccionada);
      mostrarMensaje('success', `Costo actualizado en ${ids.length} producto(s)`);
      setModalCosto(false);
      cargarProductos(listaSeleccionada, busqueda);
    } catch {
      mostrarMensaje('error', 'Error al modificar el costo');
    }
  };

  // Subir archivo
  const handleSubirArchivo = async (archivo) => {
    try {
      const res = await subirArchivo(archivo);
      mostrarMensaje('success', res.data.mensaje);
      setModalArchivo(false);
      if (listaSeleccionada) cargarProductos(listaSeleccionada);
    } catch (err) {
      const msg = err.response?.data?.error || 'Error al procesar el archivo';
      mostrarMensaje('error', msg);
    }
  };

  // Exportar Excel - lista completa
  const handleExportarLista = async () => {
    if (!listaSeleccionada) return;
    try {
      const res = await exportarExcel(listaSeleccionada);
      descargarBlob(res.data, `${listaSeleccionada}_productos.xlsx`);
    } catch {
      mostrarMensaje('error', 'Error al exportar el Excel');
    }
  };

  // Exportar carteles
  const handleExportarCarteles = async () => {
    if (seleccionados.length === 0) {
      mostrarMensaje('error', 'Seleccioná al menos un producto para crear carteles');
      return;
    }
    try {
      const res = await exportarCarteles(seleccionados, listaSeleccionada);
      descargarBlob(res.data, 'carteles.xlsx');
    } catch {
      mostrarMensaje('error', 'Error al exportar los carteles');
    }
  };

  const descargarBlob = (blob, nombre) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombre;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  const listasActuales = casas.find((c) => c.id === casaSeleccionada)?.listas || [];

  return (
    <div className="app">
      <Navbar onAbrirArchivo={() => setModalArchivo(true)} />

      {mensaje && (
        <div className={`alerta alerta-${mensaje.tipo}`}>{mensaje.texto}</div>
      )}

      <div className="contenido">
        <BarraHerramientas
          casas={casas}
          casaSeleccionada={casaSeleccionada}
          onCasaChange={(c) => {
            setCasaSeleccionada(c);
            setListaSeleccionada('');
            setProductos([]);
          }}
          listas={listasActuales}
          listaSeleccionada={listaSeleccionada}
          onListaChange={setListaSeleccionada}
          onMostrar={handleMostrar}
          busqueda={busqueda}
          onBusquedaChange={setBusqueda}
          onBuscar={handleBuscar}
          onModificarPorcentaje={() => {
            if (seleccionados.length === 0) {
              mostrarMensaje('error', 'Seleccioná al menos un producto');
              return;
            }
            setModalPorcentaje(true);
          }}
          onModificarCosto={() => {
            if (seleccionados.length === 0) {
              mostrarMensaje('error', 'Seleccioná al menos un producto');
              return;
            }
            setModalCosto(true);
          }}
          onExportarLista={handleExportarLista}
          onExportarCarteles={handleExportarCarteles}
          seleccionados={seleccionados}
        />

        <TablaPrincipal
          productos={productos}
          cargando={cargando}
          seleccionados={seleccionados}
          onSeleccionChange={setSeleccionados}
        />
      </div>

      {modalPorcentaje && (
        <ModalPorcentaje
          onConfirmar={handleConfirmarPorcentaje}
          onCancelar={() => setModalPorcentaje(false)}
        />
      )}

      {modalCosto && (
        <ModalCosto
          onConfirmar={handleConfirmarCosto}
          onCancelar={() => setModalCosto(false)}
        />
      )}

      {modalArchivo && (
        <ModalArchivo
          onSubir={handleSubirArchivo}
          onCancelar={() => setModalArchivo(false)}
        />
      )}
    </div>
  );
}

export default App;

