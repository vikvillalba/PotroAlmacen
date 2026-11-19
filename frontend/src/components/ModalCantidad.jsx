import { useState } from 'react'
import { Check, ChevronDown } from 'lucide-react'
import Modal from './Modal.jsx'
import ControlCantidad from './ControlCantidad.jsx'

export default function ModalCantidad({ material, inicial, onAnadir, onCerrar }) {
    const [cantidad, setCantidad] = useState(inicial?.cantidad ?? 1)
    const [presentacion, setPresentacion] = useState(
        inicial?.presentacion ?? material.presentaciones[0],
    )

    return (
        <Modal
            titulo="Seleccionar material"
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note">
                        {material.refill ? `Refill: ${material.refill}` : ''}
                    </small>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onCerrar}>Volver</button>
                        <button
                            type="button"
                            className="btn primary"
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
                        <th scope="col">Cantidad</th>
                        <th scope="col">Presentación</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="muted">{material.codigo}</td>
                        <td><strong>{material.nombre}</strong></td>
                        <td><ControlCantidad valor={cantidad} onCambio={setCantidad} /></td>
                        <td>
                            <span className="input select">
                                <select
                                    value={presentacion}
                                    onChange={(e) => setPresentacion(e.target.value)}
                                    aria-label="Presentación"
                                >
                                    {material.presentaciones.map((p) => (
                                        <option key={p} value={p}>{p}</option>
                                    ))}
                                </select>
                                <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                            </span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </Modal>
    )
}