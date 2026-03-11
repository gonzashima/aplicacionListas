import React from 'react';

function Navbar({ onAbrirArchivo }) {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        📋 Listas — Gestión de Precios
      </div>
      <button className="navbar-btn" onClick={onAbrirArchivo}>
        📂 Leer archivo
      </button>
    </nav>
  );
}

export default Navbar;

