import { useState } from 'react'
import { Check, ChevronDown } from 'lucide-react'
import Modal from './Modal.jsx'

const MAX_DIGITOS = 9
const esEntero = (texto) => /^\d+$/.test(String(texto).trim())
const soloDigitos = (texto) => texto.replace(/\D/g, '').slice(0, MAX_DIGITOS).replace(/^0+(?=\d)/, '')

export default function ModalArea({ areasDisponibles, onAnadir, onCerrar }) {
    const [areaId, setAreaId] = useState('')
    const [cantidad, setCantidad] = useState('1')

    const cantidadNum = Number(cantidad)
    const valido = Boolean(areaId) && esEntero(cantidad) && cantidadNum > 0

    const confirmar = () => {
        if (!valido) return
        const id = Number(areaId)
        const encontrada = areasDisponibles.find((a) => a.id === id)
        if (!encontrada) return

        onAnadir({
            id,
            nombreArea: encontrada.nombreArea,
            cantidad: cantidadNum,
        })
    }

    return (
        <Modal
            titulo="Agregar área"
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note">
                        Asigna el límite permitido para el área.
                    </small>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onCerrar}>
                            Volver
                        </button>
                        <button
                            type="button"
                            className="btn primary"
                            disabled={!valido}
                            onClick={confirmar}
                        >
                            <Check size={16} /> Añadir
                        </button>
                    </div>
                </>
            }
        >
            <div className="form">
                <label className="field">
                    <span>Área</span>
                    <span className="input select">
                        <select value={areaId} onChange={(e) => setAreaId(e.target.value)}>
                            <option value="" disabled>
                                {areasDisponibles.length > 0
                                    ? 'Selecciona un área'
                                    : 'No hay más áreas disponibles'}
                            </option>
                            {areasDisponibles.map((a) => (
                                <option key={a.id} value={a.id}>
                                    {a.nombreArea}
                                </option>
                            ))}
                        </select>
                        <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                    </span>
                </label>

                <label className="field">
                    <span>Cantidad asignada</span>
                    <span className="input">
                        <input
                            type="text"
                            inputMode="numeric"
                            autoComplete="off"
                            value={cantidad}
                            onChange={(e) => setCantidad(soloDigitos(e.target.value))}
                            placeholder="1"
                        />
                    </span>
                </label>
            </div>
        </Modal>
    )
}