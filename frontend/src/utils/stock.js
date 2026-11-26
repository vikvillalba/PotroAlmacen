export const MENSAJE_EXCEDE = 'Cantidad excede el stock disponible'

/** Existencia actual de un material, sin importar si viene como `cantidad` o `existencia`. */
export const stockDisponible = (material) => Number(material?.cantidad ?? material?.existencia ?? 0)

/** Unidades que sobran respecto al stock; 0 si la cantidad cabe. */
export const excedente = ({ material, cantidad }) => Math.max(0, Number(cantidad) - stockDisponible(material))

export const excede = (item) => excedente(item) > 0

/** Items del carrito cuya cantidad supera el stock disponible. */
export const itemsExcedidos = (items) => items.filter(excede)