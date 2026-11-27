const MINUTO = 60 * 1000
const HORA = 60 * MINUTO

const mismoDia = (a, b) =>
    a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate()

/** Fecha larga con el día de la semana en mayúscula inicial: "Sábado 3 de octubre de 2026". */
export function fechaLarga(fecha) {
    const partes = Object.fromEntries(
        new Intl.DateTimeFormat('es-MX', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
            .formatToParts(fecha)
            .map((p) => [p.type, p.value]),
    )
    const dia = partes.weekday.charAt(0).toUpperCase() + partes.weekday.slice(1)
    return `${dia} ${partes.day} de ${partes.month} de ${partes.year}`
}

/** Tiempo transcurrido en lenguaje natural: "Hace 15 min", "Hace 2 h", "Ayer" o la fecha. */
export function haceCuanto(fecha, ahora) {
    const diferencia = ahora - fecha
    if (diferencia < MINUTO) return 'Hace un momento'
    if (diferencia < HORA) return `Hace ${Math.floor(diferencia / MINUTO)} min`
    if (mismoDia(fecha, ahora)) return `Hace ${Math.floor(diferencia / HORA)} h`
    const ayer = new Date(ahora)
    ayer.setDate(ayer.getDate() - 1)
    if (mismoDia(fecha, ayer)) return 'Ayer'
    return fecha.toLocaleDateString('es-MX', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

export { mismoDia }