import { useState } from 'react'
import { Check, Trash2 } from 'lucide-react'
import Modal from './Modal.jsx'
import ControlCantidad from './ControlCantidad.jsx'
import AlertaExcedente from './AlertaExcedente.jsx'
import { MENSAJE_EXCEDE, excede, excedente, itemsExcedidos, stockDisponible } from '../utils/stock.js'

export default function ModalSeleccionados({ items, solicitante, onGuardar, onCerrar }) {
    // los cambios solo se aplican al dar "Guardar".
    const [borrador, setBorrador] = useState(items)

    // Validación en vivo: la cantidad se guarda tal cual se escribe y la fila se marca en rojo si excede el stock.
    const cambiarCantidad = (id, nuevaCantidad) =>
        setBorrador((prev) =>
            prev.map((i) => (i.material.id === id ? { ...i, cantidad: Math.max(0, nuevaCantidad) } : i)),
        )

    const quitar = (id) => setBorrador((prev) => prev.filter((i) => i.material.id !== id))

    const n = borrador.length
    const hayExceso = itemsExcedidos(borrador).length > 0
    const hayCantidadInvalida = borrador.some((i) => i.cantidad < 1)
    const subtitulo = `${n} ${n === 1 ? 'producto' : 'productos'}${solicitante ? ` para ${solicitante.nombre} · Área ${solicitante.area}` : ''
    }`

    return (
        <Modal
            titulo="Materiales seleccionados"
            subtitulo={subtitulo}
            onCerrar={onCerrar}
            pie={
                <>
                    <small className="modal-note">
                        {hayExceso
                            ? 'Corrige las cantidades en rojo para poder guardar.'
                            : 'Los cambios se aplicarán a la entrega en curso.'}
                    </small>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onCerrar}>Volver</button>
                        <button
                            type="button"
                            className="btn primary"
                            disabled={hayExceso || hayCantidadInvalida}
                            onClick={() => onGuardar(borrador)}
                        >
                            <Check size={16} /> Guardar
                        </button>
                    </div>
                </>
            }
        >
            <AlertaExcedente items={borrador} />
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
                {borrador.map((item) => {
                    const { material: m, cantidad, presentacion } = item
                    const maxDisponible = stockDisponible(m)
                    const excedido = excede(item)
                    return (
                        <tr key={m.id} className={excedido ? 'excede' : undefined}>
                            <td className="muted">{m.codigo}</td>
                            <td>
                                <strong>{m.nombre}</strong>
                                <small>{presentacion}</small>
                            </td>
                            <td>{m.categoria}</td>
                            <td>
                                <ControlCantidad
                                    valor={cantidad}
                                    max={maxDisponible}
                                    onCambio={(c) => cambiarCantidad(m.id, c)}
                                    invalido={excedido || cantidad < 1}
                                    describedBy={`stock-${m.id}`}
                                />
                                <small id={`stock-${m.id}`} className={excedido ? 'stock-error' : undefined}>
                                    {excedido
                                        ? `${MENSAJE_EXCEDE} (excedente: ${excedente(item)})`
                                        : `Disp.: ${maxDisponible} ${m.unidadMedida ?? ''}`}
                                </small>
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
                    )
                })}
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