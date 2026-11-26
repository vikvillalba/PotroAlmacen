import { useState } from 'react'
import { Check } from 'lucide-react'
import Modal from './Modal.jsx'
import ControlCantidad from './ControlCantidad.jsx'
import { MENSAJE_EXCEDE, stockDisponible } from '../utils/stock.js'

export default function ModalCantidad({ material, inicial, onAnadir, onCerrar }) {
    const maxDisponible = stockDisponible(material)
    const [cantidad, setCantidad] = useState(() =>
        Math.min(inicial?.cantidad ?? 1, Math.max(1, maxDisponible)),
    )

    const presentacion =
        inicial?.presentacion ??
        [material.presentacion, material.contenidoPresentacion].filter(Boolean).join(' · ')

    // Validación en vivo: se puede escribir cualquier cantidad y se marca en rojo si no cabe.
    const excedido = cantidad > maxDisponible
    const excedente = Math.max(0, cantidad - maxDisponible)
    const invalida = maxDisponible <= 0 || cantidad < 1 || excedido
    const unidad = material.unidadMedida ?? ''

    return (
        <Modal
            titulo="Seleccionar material"
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note" id="aviso-cantidad" role={excedido ? 'alert' : undefined}>
                        {excedido ? (
                            <span className="red">
                                {MENSAJE_EXCEDE}: hay {maxDisponible} {unidad} (excedente: {excedente}).
                            </span>
                        ) : cantidad < 1 ? (
                            <span className="red">Indica una cantidad mayor a cero.</span>
                        ) : (
                            <>Disponible: {maxDisponible} {unidad}</>
                        )}
                    </small>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onCerrar}>Volver</button>
                        <button
                            type="button"
                            className="btn primary"
                            disabled={invalida}
                            onClick={() => onAnadir({ material, cantidad, presentacion })}
                        >
                            <Check size={16} /> Añadir
                        </button>
                    </div>
                </>
            }
        >
            <table className="table">
                <thead>
                <tr>
                    <th scope="col">Código</th>
                    <th scope="col">Producto</th>
                    <th scope="col">Presentación</th>
                    <th scope="col">Cantidad</th>
                </tr>
                </thead>
                <tbody>
                <tr className={excedido ? 'excede' : undefined}>
                    <td className="muted">{material.codigo}</td>
                    <td><strong>{material.nombre}</strong></td>
                    <td>{presentacion || '—'}</td>
                    <td>
                        <ControlCantidad
                            valor={cantidad}
                            max={maxDisponible}
                            onCambio={setCantidad}
                            invalido={excedido || cantidad < 1}
                            describedBy="aviso-cantidad"
                        />
                        {excedido && <small className="stock-error">{MENSAJE_EXCEDE}</small>}
                    </td>
                </tr>
                </tbody>
            </table>
        </Modal>
    )
}