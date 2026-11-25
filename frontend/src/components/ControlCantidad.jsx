import { Minus, Plus } from 'lucide-react'

export default function ControlCantidad({ valor, max, onCambio }) {
    const enMinimo = valor <= 1
    const enMaximo = typeof max === 'number' && valor >= max

    return (
        <div className="qty">
            <button
                type="button"
                className="qty-btn minus"
                disabled={enMinimo}
                onClick={() => onCambio(valor - 1)}
                aria-label="Disminuir cantidad"
            >
                <Minus size={14} />
            </button>
            <span className="qty-val">{valor}</span>
            <button
                type="button"
                className="qty-btn plus"
                disabled={enMaximo}
                onClick={() => onCambio(valor + 1)}
                aria-label="Aumentar cantidad"
            >
                <Plus size={14} />
            </button>
        </div>
    )
}