import React from 'react';

function TablaPrincipal({ productos, cargando, seleccionados, onSeleccionChange, sortConfig, onSortChange }) {
  const columnasOrdenables = [
    { key: 'codigo', label: 'Codigo' },
    { key: 'nombre', label: 'Nombre' },
    { key: 'costo', label: 'Costo' },
    { key: 'precio', label: 'Precio' },
    { key: 'porcentaje', label: 'Porcentaje' },
  ];

  const indicadorOrden = (key) => {
    if (sortConfig.key !== key) return '  ';
    return sortConfig.direction === 'asc' ? ' ▲' : ' ▼';
  };

  const toggleSeleccion = (id) => {
    if (seleccionados.includes(id)) {
      onSeleccionChange(seleccionados.filter((s) => s !== id));
    } else {
      onSeleccionChange([...seleccionados, id]);
    }
  };

  const toggleTodos = () => {
    if (seleccionados.length === productos.length) {
      onSeleccionChange([]);
    } else {
      onSeleccionChange(productos.map((p) => p.id));
    }
  };

  const todosSeleccionados = productos.length > 0 && seleccionados.length === productos.length;
  const algunoSeleccionado = seleccionados.length > 0 && seleccionados.length < productos.length;

  return (
    <div className="tabla-contenedor">
      <div className="tabla-scroll">
        <table>
          <thead>
            <tr>
              <th>
                <input
                  type="checkbox"
                  checked={todosSeleccionados}
                  ref={(el) => {
                    if (el) el.indeterminate = algunoSeleccionado;
                  }}
                  onChange={toggleTodos}
                  title="Seleccionar todos"
                />
              </th>
              {columnasOrdenables.map((columna) => (
                <th key={columna.key}>
                  <button
                    type="button"
                    className="th-sort-btn"
                    onClick={() => onSortChange(columna.key)}
                    title={`Ordenar por ${columna.label}`}
                  >
                    {columna.label}
                    <span className="th-sort-indicador">{indicadorOrden(columna.key)}</span>
                  </button>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {cargando ? (
              <tr>
                <td colSpan={6}>
                  <div className="spinner-contenedor">
                    <div className="spinner" />
                  </div>
                </td>
              </tr>
            ) : productos.length === 0 ? (
              <tr>
                <td colSpan={6} className="tabla-vacia">
                  Seleccioná una casa y una lista para ver los productos
                </td>
              </tr>
            ) : (
              productos.map((p) => {
                const seleccionado = seleccionados.includes(p.id);
                return (
                  <tr
                    key={p.id}
                    className={seleccionado ? 'fila-seleccionada' : ''}
                    onClick={() => toggleSeleccion(p.id)}
                  >
                    <td onClick={(e) => e.stopPropagation()}>
                      <input
                        type="checkbox"
                        checked={seleccionado}
                        onChange={() => toggleSeleccion(p.id)}
                      />
                    </td>
                    <td>{p.codigo}</td>
                    <td>{p.nombre}</td>
                    <td>${p.costo.toLocaleString('es-AR')}</td>
                    <td><strong>${p.precio.toLocaleString('es-AR')}</strong></td>
                    <td>{p.porcentaje}%</td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
      {productos.length > 0 && (
        <div className="tabla-info">
          {productos.length} producto(s) — {seleccionados.length} seleccionado(s)
        </div>
      )}
    </div>
  );
}

export default TablaPrincipal;

