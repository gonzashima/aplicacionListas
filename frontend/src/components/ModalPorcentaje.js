import React, { useState, useRef } from 'react';

function ModalPorcentaje({ onConfirmar, onCancelar }) {
  const [valor, setValor] = useState('');
  const [error, setError] = useState('');
  const inputRef = useRef(null);

  const handleConfirmar = () => {
    const num = parseInt(valor, 10);
    if (isNaN(num) || num < 0) {
      setError('Debe ser un número entero positivo');
      return;
    }
    onConfirmar(num);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleConfirmar();
    if (e.key === 'Escape') onCancelar();
  };

  return (
    <div className="modal-overlay" onClick={onCancelar}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>Cambiar porcentaje</h2>
        <div className="modal-campo">
          <label htmlFor="input-porcentaje">Nuevo porcentaje</label>
          <input
            id="input-porcentaje"
            ref={inputRef}
            type="number"
            min="0"
            placeholder="Ej: 120"
            value={valor}
            onChange={(e) => { setValor(e.target.value); setError(''); }}
            onKeyDown={handleKeyDown}
            autoFocus
          />
        </div>
        {error && <p className="modal-error">⚠️ {error}</p>}
        <div className="modal-acciones">
          <button className="btn btn-secondary" onClick={onCancelar}>
            Cancelar
          </button>
          <button className="btn btn-primary" onClick={handleConfirmar}>
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
}

export default ModalPorcentaje;

