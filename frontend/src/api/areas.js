import { get, post } from './cliente.js'

const BASE = '/api/areas'

export const listarAreas = (signal) => get(BASE, { signal })

export const asignarMaterialArea = (idArea, { codigoMaterial, nombreMaterial, cantidad }) =>
    post(`${BASE}/${idArea}/materiales`, { codigoMaterial, nombreMaterial, cantidad })/*  */