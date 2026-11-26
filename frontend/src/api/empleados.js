export async function listarEmpleados() {
    const res = await fetch('/api/empleados')
    if (!res.ok) throw new Error('No se pudo cargar el catálogo de personal')
    return res.json()
}

export async function listarEmpleadosAlmacen() {
    const res = await fetch('/api/empleados/almacen')
    if (!res.ok) throw new Error('No se pudo cargar el personal de almacén')
    return res.json()
}