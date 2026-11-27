import { useEffect, useState } from 'react'
import { listarMateriales } from '../api/materiales.js'
import { listarBoletas } from '../api/salidas.js'

/** Datos del panel de Servicios Generales: todos los materiales (para los contadores) y las boletas registradas. */
export function useResumenSG() {
    const [estado, setEstado] = useState({ cargando: true, error: null, materiales: [], boletas: [] })

    useEffect(() => {
        const controlador = new AbortController()
        Promise.all([listarMateriales({}, controlador.signal), listarBoletas(controlador.signal)])
            .then(([materiales, boletas]) => setEstado({ cargando: false, error: null, materiales, boletas }))
            .catch((error) => {
                if (error.name !== 'AbortError') {
                    setEstado({ cargando: false, error, materiales: [], boletas: [] })
                }
            })
        return () => controlador.abort()
    }, [])

    return estado
}