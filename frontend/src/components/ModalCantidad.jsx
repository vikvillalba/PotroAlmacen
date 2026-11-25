import { useState } from 'react'
import { Check } from 'lucide-react'
import Modal from './Modal.jsx'
import ControlCantidad from './ControlCantidad.jsx'

export default function ModalCantidad({ material, inicial, onAnadir, onCerrar }) {
    const maxDisponible = Number(material.cantidad ?? material.existencia ?? 0)
    const [cantidad, setCantidad] = useState(() =>
        Math.min(inicial?.cantidad ?? 1, Math.max(1, maxDisponible)),
    )
    const [avisoStock, setAvisoStock] = useState(null)

    const presentacion =
        inicial?.presentacion ??
        [material.presentacion, material.contenidoPresentacion].filter(Boolean).join(' · ')

    const cambiarCantidad = (nueva) => {
        if (nueva < 1) return
        if (nueva > maxDisponible) {
            setCantidad(maxDisponible)
            setAvisoStock(`Solo hay ${maxDisponible} ${material.unidadMedida ?? 'unidades'} disponibles.`)
            return
        }
        setAvisoStock(null)
        setCantidad(nueva)
    }

    const invalida = maxDisponible <= 0 || cantidad < 1 || cantidad > maxDisponible

    return (
        <Modal
            titulo="Seleccionar material"
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note">
                        {avisoStock ? (
                            <span className="red">{avisoStock}</span>
                        ) : (
                            <>Disponible: {maxDisponible} {material.unidadMedida ?? ''}</>
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
                    <tr>
                        <td className="muted">{material.codigo}</td>
                        <td><strong>{material.nombre}</strong></td>
                        <td>{presentacion || '—'}</td>
                        <td>
                            <ControlCantidad
                                valor={cantidad}
                                max={maxDisponible}
                                onCambio={cambiarCantidad}
                            />
                        </td>
                    </tr>
                </tbody>
            </table>
        </Modal>
    )
}