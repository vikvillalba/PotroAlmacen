import { useEffect, useState } from 'react'
import { listarMateriales } from '../api/materiales.js'

/** Adapta un material del API conservando los campos del backend y compatibilidad con modales. */
const paraEntrega = (m) => {
    const presentacionCompleta = [m.presentacion, m.contenidoPresentacion]
        .filter(Boolean)
        .join(' · ')

    return {
        id: m.id,
        codigo: m.codigo,
        nombre: m.nombre,
        categoria: m.categoria,
        estado: m.estado,
        cantidad: m.cantidad,
        existencia: m.cantidad,
        unidadMedida: m.unidadMedida,
        presentacion: m.presentacion,
        contenidoPresentacion: m.contenidoPresentacion,
        presentaciones: presentacionCompleta ? [presentacionCompleta] : [],
    }
}

export function useMateriales(busqueda) {
    const [estado, setEstado] = useState({ clave: null, materiales: [], error: null })
    const termino = busqueda.trim()

    useEffect(() => {
        const controlador = new AbortController()
        const espera = setTimeout(() => {
            listarMateriales({ busqueda: termino }, controlador.signal)
                .then((datos) => setEstado({ clave: termino, materiales: datos.map(paraEntrega), error: null }))
                .catch((error) => {
                    if (error.name !== 'AbortError') setEstado({ clave: termino, materiales: [], error })
                })
        }, 300)
        return () => {
            clearTimeout(espera)
            controlador.abort()
        }
    }, [termino])

    return { materiales: estado.materiales, error: estado.error, cargando: estado.clave !== termino }
}