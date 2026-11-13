import { useEffect, useState } from 'react'
import { obtenerCatalogos } from '../api/materiales.js'

export function useCatalogos() {
    const [estado, setEstado] = useState({ catalogos: null, error: null })

    useEffect(() => {
        const controlador = new AbortController()
        obtenerCatalogos(controlador.signal)
            .then((catalogos) => setEstado({ catalogos, error: null }))
            .catch((error) => {
                if (error.name !== 'AbortError') setEstado({ catalogos: null, error })
            })
        return () => controlador.abort()
    }, [])

    return estado
}