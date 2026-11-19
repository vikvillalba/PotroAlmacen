import { useState } from 'react'
import { Check, Trash2 } from 'lucide-react'
import Modal from './Modal.jsx'
import ControlCantidad from './ControlCantidad.jsx'

export default function ModalSeleccionados({ items, solicitante, onGuardar, onCerrar }) {
    // los cambios solo se aplican al dar "Guardar".
    const [borrador, setBorrador] = useState(items)

    const cambiarCantidad = (id, cantidad) =>
        setBorrador((prev) => prev.map((i) => (i.material.id === id ? { ...i, cantidad } : i)))

    const quitar = (id) => setBorrador((prev) => prev.filter((i) => i.material.id !== id))

    const n = borrador.length
    const subtitulo = `${n} ${n === 1 ? 'producto' : 'productos'}${solicitante ? ` para ${solicitante.nombre} · Área ${solicitante.area}` : ''
        }`

    return (
        <Modal
            titulo="Materiales seleccionados"
            subtitulo={subtitulo}
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note">Los cambios se aplicarán a la entrega en curso.</small>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onCerrar}>Volver</button>
                        <button type="button" className="btn primary" onClick={() => onGuardar(borrador)}>
                            <Check size={16} /> Guardar
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
                        <th scope="col">Categoría</th>
                        <th scope="col">Cantidad</th>
                        <th scope="col"><span className="sr-only">Quitar</span></th>
                    </tr>
                </thead>
                <tbody>
                    {borrador.map(({ material: m, cantidad, presentacion }) => (
                        <tr key={m.id}>
                            <td className="muted">{m.codigo}</td>
                            <td>
                                <strong>{m.nombre}</strong>
                                <small>{presentacion}</small>
                            </td>
                            <td>{m.categoria}</td>
                            <td>
                                <ControlCantidad
                                    valor={cantidad}
                                    onCambio={(c) => cambiarCantidad(m.id, c)}
                                />
                            </td>
                            <td>
                                <button
                                    type="button"
                                    className="row-btn danger"
                                    onClick={() => quitar(m.id)}
                                    aria-label={`Quitar ${m.nombre}`}
                                >
                                    <Trash2 size={16} />
                                </button>
                            </td>
                        </tr>
                    ))}
                    {n === 0 && (
                        <tr>
                            <td colSpan={5} className="muted center">No hay materiales seleccionados.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </Modal>
    )
}