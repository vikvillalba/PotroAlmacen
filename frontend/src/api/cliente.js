export const API_URL = import.meta.env.VITE_API_URL ?? ''

export class ApiError extends Error {
    constructor({ status, codigo, mensaje, campos = {} }) {
        super(mensaje)
        this.name = 'ApiError'
        this.status = status
        this.codigo = codigo
        this.mensaje = mensaje
        this.campos = campos
    }
}

const MENSAJES_POR_STATUS = {
    400: 'La solicitud no es válida.',
    401: 'Tu sesión no es válida o expiró. Inicia sesión de nuevo.',
    403: 'No tienes permiso para esta operación.',
    404: 'El recurso solicitado no existe.',
    409: 'El registro ya existe.',
}

function aQueryString(params) {
    const q = new URLSearchParams()
    for (const [k, v] of Object.entries(params ?? {})) {
        if (v !== undefined && v !== null && v !== '') q.set(k, v)
    }
    const s = q.toString()
    return s ? `?${s}` : ''
}

async function leerError(res) {
    let cuerpo
    try {
        cuerpo = await res.json()
    } catch {
        cuerpo = null
    }
    const campos = Object.fromEntries((cuerpo?.campos ?? []).map((c) => [c.campo, c.mensaje]))
    return new ApiError({
        status: res.status,
        codigo: cuerpo?.codigo ?? 'DESCONOCIDO',
        mensaje:
            cuerpo?.mensaje ??
            MENSAJES_POR_STATUS[res.status] ??
            (res.status >= 500 ? 'Ocurrió un error en el servidor. Intenta de nuevo.' : 'No se pudo procesar la solicitud.'),
        campos,
    })
}

export async function request(path, { method = 'GET', body, params, signal } = {}) {
    let res
    try {
        res = await fetch(`${API_URL}${path}${aQueryString(params)}`, {
            method,
            headers: {
                Accept: 'application/json',
                ...(body !== undefined && { 'Content-Type': 'application/json' }),
            },
            body: body !== undefined ? JSON.stringify(body) : undefined,
            signal,
        })
    } catch (e) {
        if (e.name === 'AbortError') throw e
        throw new ApiError({
            status: 0,
            codigo: 'RED',
            mensaje: 'No se pudo conectar con el servidor. Revisa tu conexión e intenta de nuevo.',
        })
    }

    if (!res.ok) throw await leerError(res)
    if (res.status === 204) return undefined
    const texto = await res.text()
    return texto ? JSON.parse(texto) : undefined
}

export const get = (path, opciones) => request(path, { ...opciones, method: 'GET' })
export const post = (path, body, opciones) => request(path, { ...opciones, method: 'POST', body })