import { useState } from 'react'
import { Minus, Plus } from 'lucide-react'

/**
 * Control de cantidad con botones y captura directa.
 * Permite escribir un valor mayor a `max` para que la pantalla lo marque en rojo (`invalido`) en vez de recortarlo.
 */
export default function ControlCantidad({ valor, max, onCambio, invalido = false, describedBy }) {
    // Mientras se escribe se conserva el texto tal cual (incluido el campo vacío); fuera de eso se muestra `valor`.
    const [escrito, setEscrito] = useState(null)
    const texto = escrito ?? String(valor)

    const enMinimo = valor <= 1
    const enMaximo = typeof max === 'number' && valor >= max

    const escribir = (e) => {
        const digitos = e.target.value.replace(/\D/g, '').slice(0, 6)
        setEscrito(digitos)
        onCambio(digitos === '' ? 0 : parseInt(digitos, 10))
    }

    return (
        <div className={`qty${invalido ? ' invalid' : ''}`}>
            <button
                type="button"
                className="qty-btn minus"
                disabled={enMinimo}
                onClick={() => {
                    setEscrito(null)
                    onCambio(valor - 1)
                }}
                aria-label="Disminuir cantidad"
            >
                <Minus size={14} />
            </button>
            <input
                className="qty-val qty-input"
                type="text"
                inputMode="numeric"
                value={texto}
                onChange={escribir}
                onBlur={() => setEscrito(null)}
                aria-label="Cantidad"
                aria-invalid={invalido}
                aria-describedby={describedBy}
            />
            <button
                type="button"
                className="qty-btn plus"
                disabled={enMaximo}
                onClick={() => {
                    setEscrito(null)
                    onCambio(valor + 1)
                }}
                aria-label="Aumentar cantidad"
            >
                <Plus size={14} />
            </button>
        </div>
    )
}