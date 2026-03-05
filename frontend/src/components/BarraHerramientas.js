import React from 'react';

function BarraHerramientas({
  casas,
  casaSeleccionada,
  onCasaChange,
  listas,
  listaSeleccionada,
  onListaChange,
  onMostrar,
  busqueda,
  onBusquedaChange,
  onBuscar,
  onModificarPorcentaje,
  onModificarCosto,
  onExportarLista,
  onExportarCarteles,
  seleccionados,
}) {
  const hayLista = !!listaSeleccionada;
  const haySeleccion = seleccionados.length > 0;

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') onBuscar();
  };

  return (
    <div className="barra-herramientas">
      {/* Selector de casa */}
      <div className="barra-grupo">
        <span className="barra-label">Casa:</span>
        <select
          value={casaSeleccionada}
          onChange={(e) => onCasaChange(e.target.value)}
        >
          <option value="">-- Seleccionar --</option>
          {casas.map((c) => (
            <option key={c.id} value={c.id}>
              {c.nombre}
            </option>
          ))}
        </select>
      </div>

      {/* Selector de lista */}
      <div className="barra-grupo">
        <span className="barra-label">Lista:</span>
        <select
          value={listaSeleccionada}
          onChange={(e) => onListaChange(e.target.value)}
          disabled={listas.length === 0}
        >
          <option value="">-- Seleccionar --</option>
          {listas.map((l) => (
            <option key={l} value={l}>
              {l}
            </option>
          ))}
        </select>
        <button className="btn btn-primary" onClick={onMostrar} disabled={!hayLista}>
          Mostrar
        </button>
      </div>

      <div className="barra-separador" />

      {/* Búsqueda */}
      <div className="barra-grupo">
        <span className="barra-label">Buscar:</span>
        <input
          type="text"
          placeholder="Nombre del producto"
          value={busqueda}
          onChange={(e) => onBusquedaChange(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={!hayLista}
          style={{ minWidth: 180 }}
        />
        <button className="btn btn-secondary" onClick={onBuscar} disabled={!hayLista}>
          Buscar
        </button>
      </div>

      <div className="barra-separador" />

      {/* Acciones sobre selección */}
      <div className="barra-grupo">
        <button
          className="btn btn-warning"
          onClick={onModificarPorcentaje}
          disabled={!haySeleccion}
          title={haySeleccion ? `${seleccionados.length} producto(s) seleccionado(s)` : 'Seleccioná productos primero'}
        >
          % Porcentaje
        </button>
        <button
          className="btn btn-warning"
          onClick={onModificarCosto}
          disabled={!haySeleccion}
          title={haySeleccion ? `${seleccionados.length} producto(s) seleccionado(s)` : 'Seleccioná productos primero'}
        >
          $ Costo
        </button>
      </div>

      <div className="barra-separador" />

      {/* Exportar Excel */}
      <div className="barra-grupo">
        <button
          className="btn btn-success"
          onClick={onExportarLista}
          disabled={!hayLista}
          title="Exportar lista completa a Excel"
        >
          📊 Exportar lista
        </button>
        <button
          className="btn btn-outline"
          onClick={onExportarCarteles}
          disabled={!haySeleccion}
          title={haySeleccion ? 'Exportar carteles de productos seleccionados' : 'Seleccioná productos primero'}
        >
          🏷️ Carteles
        </button>
      </div>

      {haySeleccion && (
        <span style={{ fontSize: '0.8rem', color: '#6c757d', marginLeft: 4 }}>
          {seleccionados.length} seleccionado(s)
        </span>
      )}
    </div>
  );
}

export default BarraHerramientas;

