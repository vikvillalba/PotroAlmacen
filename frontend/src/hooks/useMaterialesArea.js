import { useEffect, useMemo, useState } from 'react'
import { materialesPermitidosArea } from '../api/areas.js'
import { useMateriales } from './useMateriales.js'

export function useMaterialesArea(idArea, busqueda) {
    const [permitidos, setPermitidos] = useState({ idArea: null, codigos: null, error: null })
    const catalogo = useMateriales(busqueda)
    const sinArea = idArea == null

    useEffect(() => {
        if (sinArea) return
        const controlador = new AbortController()
        materialesPermitidosArea(idArea, controlador.signal)
            .then((lista) =>
                setPermitidos({ idArea, codigos: new Set(lista.map((m) => m.codigoMaterial)), error: null }))
            .catch((error) => {
                if (error.name !== 'AbortError') setPermitidos({ idArea, codigos: null, error })
            })
        return () => controlador.abort()
    }, [idArea, sinArea])

    const listo = permitidos.idArea === idArea
    const { delArea, fueraDeArea } = useMemo(() => {
        if (!permitidos.codigos) return { delArea: [], fueraDeArea: [] }
        return {
            delArea: catalogo.materiales.filter((m) => permitidos.codigos.has(m.codigo)),
            fueraDeArea: catalogo.materiales.filter((m) => !permitidos.codigos.has(m.codigo)),
        }
    }, [catalogo.materiales, permitidos.codigos])

    return {
        materiales: delArea,
        fueraDeArea,
        error: (listo && permitidos.error) || catalogo.error,
        cargando: !sinArea && (!listo || catalogo.cargando),
        sinArea,
    }
}