import React, { useState, useRef } from 'react';
import { MAPEO_FORMATOS } from '../listasConfig';

function ModalArchivo({ onSubir, onCancelar, listaSeleccionada }) {
  const [archivo, setArchivo] = useState(null);
  const [dragOver, setDragOver] = useState(false);
  const [cargando, setCargando] = useState(false);
  const inputRef = useRef(null);

  const handleArchivo = (file) => {
    if (!file) return;
    setArchivo(file);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const file = e.dataTransfer.files[0];
    handleArchivo(file);
  };

  const handleConfirmar = async () => {
    if (!archivo) return;
    setCargando(true);
    await onSubir(archivo);
    setCargando(false);
  };

  return (
    <div className="modal-overlay" onClick={onCancelar}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>

        <div className="modal-titulo-fila">
          <h2>📂 Leer archivo de lista</h2>
          <div className="modal-formato-tooltip">
            <span className="modal-formato-icono" title="">📋</span>
            <div className="modal-formato-popup">
              <div className="modal-formato-popup-titulo">Formatos por lista</div>
              <table className="modal-formato-tabla">
                <tbody>
                  {MAPEO_FORMATOS.map(({ lista, formato }) => (
                    <tr key={lista}>
                      <td className="modal-formato-tabla-lista">{lista}</td>
                      <td>
                        <span className={`modal-formato-badge modal-formato-badge-${formato.toLowerCase()}`}>
                          {formato}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div
          className={`zona-drop ${dragOver ? 'drag-over' : ''}`}
          onClick={() => inputRef.current.click()}
          onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
          onDragLeave={() => setDragOver(false)}
          onDrop={handleDrop}
        >
          <input
            ref={inputRef}
            type="file"
            accept=".pdf,.xlsx,.xls"
            onChange={(e) => handleArchivo(e.target.files[0])}
          />
          {archivo ? (
            <div>
              <div>✅ Archivo seleccionado:</div>
              <div className="zona-drop-nombre">{archivo.name}</div>
            </div>
          ) : (
            <div>
              <div>📎 Hacé clic o arrastrá un archivo aquí</div>
              <div style={{ fontSize: '0.8rem', marginTop: 6, color: '#adb5bd' }}>
                Formatos aceptados: PDF, XLSX, XLS
              </div>
            </div>
          )}
        </div>

        <div className="modal-acciones">
          <button className="btn btn-secondary" onClick={onCancelar} disabled={cargando}>
            Cancelar
          </button>
          <button
            className="btn btn-primary"
            onClick={handleConfirmar}
            disabled={!archivo || cargando}
          >
            {cargando ? 'Procesando...' : 'Procesar'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ModalArchivo;
