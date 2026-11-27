export const RUTA_PANEL = '/'

const pad = (n) => String(n).padStart(2, '0')

/** "2026-10-03T10:42:00" -> "03/10/2026" */
export const formatoFechaBoleta = (iso) => {
    const f = new Date(iso)
    return `${pad(f.getDate())}/${pad(f.getMonth() + 1)}/${f.getFullYear()}`
}

/** "2026-10-03T10:42:00" -> "10:42" */
export const formatoHoraBoleta = (iso) => {
    const f = new Date(iso)
    return `${pad(f.getHours())}:${pad(f.getMinutes())}`
}