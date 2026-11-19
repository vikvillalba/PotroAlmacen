import { Minus, Plus } from 'lucide-react'

export default function ControlCantidad({ valor, onCambio, min = 1 }) {
    return (
        <div className="qty">
            <button
                type="button"
                className="qty-btn minus"
                onClick={() => onCambio(Math.max(min, valor - 1))}
                disabled={valor <= min}
                aria-label="Disminuir cantidad"
            >
                <Minus size={14} />
            </button>
            <span className="qty-val" aria-live="polite">{valor}</span>
            <button
                type="button"
                className="qty-btn plus"
                onClick={() => onCambio(valor + 1)}
                aria-label="Aumentar cantidad"
            >
                <Plus size={14} />
            </button>
        </div>
    )
}