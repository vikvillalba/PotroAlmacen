import { useMemo, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { ArrowRight, List, Plus } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import Pasos from '../components/Pasos.jsx'
import CampoBusqueda from '../components/CampoBusqueda.jsx'
import PieAcciones from '../components/PieAcciones.jsx'
import ModalCantidad from '../components/ModalCantidad.jsx'
import ModalSeleccionados from '../components/ModalSeleccionados.jsx'
import AlertaExcedente from '../components/AlertaExcedente.jsx'
import { useMaterialesArea } from '../hooks/useMaterialesArea.js'
import { excede, itemsExcedidos, stockDisponible } from '../utils/stock.js'

const BADGE = { Disponible: 'ok', 'Stock bajo': 'warn', Agotado: 'bad' }

export default function EntregaMateriales() {
    const { state } = useLocation()
    const navigate = useNavigate()
    const solicitante = state?.solicitante
    const [busqueda, setBusqueda] = useState('')
    const [seleccion, setItems] = useState(state?.items ?? [])
    const [materialActivo, setMaterialActivo] = useState(null)
    const [verSeleccionados, setVerSeleccionados] = useState(false)

    const { materiales: visibles, fueraDeArea, error, cargando, sinArea } =
        useMaterialesArea(solicitante?.idArea, busqueda)

    // Si el inventario cambió, los materiales ya elegidos toman la existencia más reciente y se vuelven a validar.
    const items = useMemo(
        () =>
            seleccion.map((i) => {
                const fresco = visibles.find((m) => m.id === i.material.id)
                if (!fresco || stockDisponible(fresco) === stockDisponible(i.material)) return i
                return {
                    ...i,
                    material: { ...i.material, cantidad: fresco.cantidad, existencia: fresco.existencia, estado: fresco.estado },
                }
            }),
        [seleccion, visibles],
    )

    const anadir = (item) => {
        setItems((prev) => {
            const existe = prev.some((i) => i.material.id === item.material.id)
            return existe
                ? prev.map((i) => (i.material.id === item.material.id ? item : i))
                : [...prev, item]
        })
        setMaterialActivo(null)
    }

    const guardarSeleccion = (nuevos) => {
        setItems(nuevos)
        setVerSeleccionados(false)
    }

    const total = items.length
    const hayExceso = itemsExcedidos(items).length > 0
    const hayBusqueda = busqueda.trim().length > 0
    const noCatalogadoEnArea = !error && !cargando && hayBusqueda && visibles.length === 0 && fueraDeArea.length > 0

    return (
        <Layout
            active="servicios"
            crumbs={[{ label: 'Servicios Generales', to: '/' }, { label: 'Entrega de material' }]}
            back="/entregas/nueva"
            title="Entrega de material y/o equipo"
            subtitle={`Solo se muestran los materiales catalogados para ${solicitante?.area ?? 'el área'}.`}
            actions={
                <button type="button" className="btn soft" onClick={() => setVerSeleccionados(true)}>
                    <List size={16} /> Materiales seleccionados ({total})
                </button>
            }
        >
            <div className="stack">
                <Pasos actual={2} />

                <section className="card">
                    <div className="card-head">
                        <h2>Materiales de limpieza</h2>
                        <small className="solicita">
                            Solicita a: {solicitante ? `${solicitante.nombre} · ${solicitante.area}` : 'Sin solicitante'}
                        </small>
                    </div>
                </section>

                <section className="card">
                    <div className="card-head">
                        <h2>Material</h2>
                        <small>Agrega uno o más materiales a la orden</small>
                    </div>

                    <form className="toolbar" role="search" onSubmit={(e) => e.preventDefault()}>
                        <CampoBusqueda
                            etiqueta="Material"
                            placeholder="Busca por nombre o código"
                            valor={busqueda}
                            onCambio={setBusqueda}
                        />
                        <button type="button" className="btn soft">
                            <Plus size={16} /> Préstamo
                        </button>
                    </form>

                    {sinArea && (
                        <p className="alert error" role="alert">
                            El solicitante no tiene un área asignada, no es posible filtrar materiales.
                        </p>
                    )}

                    <div className="table-wrap">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th scope="col">Material</th>
                                    <th scope="col">Código</th>
                                    <th scope="col">Existencias</th>
                                    <th scope="col">Disponibilidad</th>
                                </tr>
                            </thead>
                            <tbody>
                                {visibles.map((m) => (
                                    <tr
                                        key={m.id}
                                        className={`pick ${items.some((i) => i.material.id === m.id) ? 'selected' : ''} ${items.some((i) => i.material.id === m.id && excede(i)) ? 'excede' : ''}`}
                                        tabIndex={0}
                                        onClick={() => m.estado !== 'Agotado' && setMaterialActivo(m)}
                                        onKeyDown={(e) => e.key === 'Enter' && m.estado !== 'Agotado' && setMaterialActivo(m)}
                                    >
                                        <td>
                                            <strong>{m.nombre}</strong>
                                            <small>
                                                {[m.categoria, m.presentacion, m.contenidoPresentacion].filter(Boolean).join(' · ')}
                                            </small>
                                        </td>
                                        <td className="muted">{m.codigo}</td>
                                        <td>{m.cantidad} {m.unidadMedida}</td>
                                        <td><span className={`badge ${BADGE[m.estado]}`}>{m.estado}</span></td>
                                    </tr>
                                ))}
                                {error && (
                                    <tr>
                                        <td colSpan={4} className="muted center" role="alert">
                                            {error.mensaje}
                                        </td>
                                    </tr>
                                )}
                                {!error && cargando && visibles.length === 0 && (
                                    <tr>
                                        <td colSpan={4} className="muted center">Cargando materiales…</td>
                                    </tr>
                                )}
                                {noCatalogadoEnArea && (
                                    <tr>
                                        <td colSpan={4} className="center" role="alert">
                                            <p className="alert error" style={{ margin: 0 }}>
                                                El material no está catalogado para esta área ({fueraDeArea.map((m) => `${m.nombre} (${m.codigo})`).join(', ')}).
                                            </p>
                                        </td>
                                    </tr>
                                )}
                                {!error && !cargando && !noCatalogadoEnArea && visibles.length === 0 && (
                                    <tr>
                                        <td colSpan={4} className="muted center">
                                            No hay materiales que coincidan con la búsqueda.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </section>

                <AlertaExcedente items={items} />

                <PieAcciones volver="/entregas/nueva">
                    <small role="status">
                        {total} {total === 1 ? 'material seleccionado' : 'materiales seleccionados'}
                    </small>
                    <button
                        type="button"
                        className="btn primary"
                        disabled={total === 0 || hayExceso}
                        title={hayExceso ? 'Corrige las cantidades que exceden el stock' : undefined}
                        onClick={() => navigate('/entregas/nueva/confirmacion', { state: { solicitante, items } })}
                    >
                        Siguiente <ArrowRight size={16} />
                    </button>
                </PieAcciones>
            </div>

            {materialActivo && (
                <ModalCantidad
                    key={materialActivo.id}
                    material={materialActivo}
                    inicial={items.find((i) => i.material.id === materialActivo.id)}
                    onAnadir={anadir}
                    onCerrar={() => setMaterialActivo(null)}
                />
            )}

            {verSeleccionados && (
                <ModalSeleccionados
                    items={items}
                    solicitante={solicitante}
                    onGuardar={guardarSeleccion}
                    onCerrar={() => setVerSeleccionados(false)}
                />
            )}
        </Layout>
    )
}