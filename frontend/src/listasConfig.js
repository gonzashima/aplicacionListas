/**
 * Configuración centralizada de listas:
 * - FORMULA_POR_LISTA: cómo se calcula el costo de cada lista
 * - FORMATO_POR_LISTA: qué tipo de archivo acepta cada lista
 */

// Las sublistas de MAFERSA (excepto lumilagro)
const SUBLISTAS_MAFERSA = [
  'deses_plast', 'almandoz', 'bel_gioco', 'nadir', 'tramontina',
  'wheaton', 'campagna', 'chef', 'churrasqueras_y_sartenes', 'kufo',
  'daysal', 'guadix', 'loekemeyer', 'man_fer', 'marinex',
  'colores', 'datomax', 'plastic_house', 'yesi',
];

/**
 * Devuelve la info de fórmula para una lista dada.
 * @returns {{ texto: string, color: string } | null}
 */
export function getFormulaLista(lista) {
  if (!lista) return null;
  const key = lista.toLowerCase().replace(/ /g, '_').replace(/-/g, '_');

  if (key === 'duravit' || key === 'respontech') {
    return { texto: 'Precio de lista directo', color: 'neutral' };
  }
  if (key === 'lumilagro') {
    return { texto: '−30% + IVA completo (21%)', color: 'iva' };
  }
  if (SUBLISTAS_MAFERSA.includes(key)) {
    return { texto: '−30% + medio IVA (10,5%)', color: 'descuento' };
  }
  if (key === 'rigolleau') {
    return { texto: '−10% + IVA completo (21%)', color: 'iva' };
  }
  if (key === 'lema' || key === 'rodeca') {
    return { texto: '+ medio IVA (10,5%)', color: 'medio-iva' };
  }
  if (key === 'difplast') {
    return { texto: '−20% + medio IVA (10,5%)', color: 'descuento' };
  }
  return null;
}

/**
 * Mapeo completo de todas las listas a su formato de archivo requerido.
 * Usado para mostrar en el tooltip del modal de subir archivo.
 */
export const MAPEO_FORMATOS = [
  { lista: 'DURAVIT',                   formato: 'PDF'  },
  { lista: 'RODECA',                    formato: 'PDF'  },
  { lista: 'MAFERSA (todas las listas)',formato: 'PDF'  },
  { lista: 'RESPONTECH',                formato: 'XLSX' },
  { lista: 'RIGOLLEAU',                 formato: 'XLSX' },
  { lista: 'DIFPLAST',                  formato: 'XLSX' },
  { lista: 'LEMA',                      formato: 'XLS'  },
];
