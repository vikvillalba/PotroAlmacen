import { get, post } from './cliente.js'

const BASE = '/api/salidas'

/**
 * Registra una entrega y genera la boleta.
 * idEntregador es el id de un EMPLEADO (el backend lo busca en Personal), no el id de usuario.
 */
export const registrarEntrega = ({ idSolicitante, idEntregador, materiales }) =>
    post(`${BASE}/boletas`, { idSolicitante, idEntregador, materiales })

export const registrarEntregaArea = ({ idArea, idSolicitante, idEntregador, materiales }) =>
    post(`${BASE}/areas/${idArea}/entregas`, { idSolicitante, idEntregador, materiales })

export const listarBoletas = (signal) => get(`${BASE}/boletas`, { signal })

export const obtenerBoleta = (id, signal) => get(`${BASE}/boletas/${id}`, { signal })

/** Convierte los items del carrito de la pantalla de entrega al formato del API. */
export const lineasDeEntrega = (items) =>
    items.map((i) => ({ codigoMaterial: i.material.codigo, cantidadAEntregar: i.cantidad }))