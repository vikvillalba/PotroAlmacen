import { AlertTriangle } from 'lucide-react'
import { excedente, itemsExcedidos, stockDisponible } from '../utils/stock.js'

/** Alerta que lista los materiales cuya cantidad supera el stock y cuánto se pasan. No muestra nada si todo cabe. */
export default function AlertaExcedente({ items }) {
    const excedidos = itemsExcedidos(items)
    if (excedidos.length === 0) return null

    return (
        <div className="alert error alert-excedente" role="alert">
            <AlertTriangle size={16} aria-hidden="true" />
            <div>
                <strong>
                    {excedidos.length === 1
                        ? 'Un material excede el stock disponible.'
                        : `${excedidos.length} materiales exceden el stock disponible.`}
                </strong>
                <ul>
                    {excedidos.map((item) => (
                        <li key={item.material.id}>
                            {item.material.nombre}: solicitas {item.cantidad}, hay {stockDisponible(item.material)}{' '}
                            {item.material.unidadMedida ?? ''} (excedente: {excedente(item)})
                        </li>
                    ))}
                </ul>
                <small>Ajusta las cantidades para continuar.</small>
            </div>
        </div>
    )
}