import { Check } from 'lucide-react'

const PASOS = ['Solicitante', 'Materiales', 'Confirmación']

export default function Pasos({ actual }) {
    return (
        <nav className="card steps" aria-label="Progreso de la entrega">
            <ol>
                {PASOS.map((nombre, i) => {
                    const n = i + 1
                    const estado = n < actual ? 'done' : n === actual ? 'on' : ''
                    return (
                        <li
                            key={nombre}
                            className={`step ${estado}`}
                            aria-current={n === actual ? 'step' : undefined}
                        >
                            <span className="step-num" aria-hidden="true">
                                {estado === 'done' ? <Check size={16} /> : n}
                            </span>
                            <span>
                                <small>Paso {n}</small>
                                <strong>{nombre}</strong>
                            </span>
                            {n < PASOS.length && <span className="step-line" aria-hidden="true" />}
                        </li>
                    )
                })}
            </ol>
        </nav>
    )
}