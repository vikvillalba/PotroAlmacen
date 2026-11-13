import { get, post } from './cliente.js'

const BASE = '/api/materiales'

export const listarMateriales = (filtros, signal) => get(BASE, { params: filtros, signal })

export const obtenerMaterial = (id, signal) => get(`${BASE}/${id}`, { signal })

export const registrarMaterial = (material) => post(BASE, material)

export const obtenerCatalogos = (signal) => get(`${BASE}/catalogos`, { signal })