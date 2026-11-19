import { useState } from 'react'
import { useLocation } from 'react-router-dom'
import { ArrowRight, List, Plus } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import Pasos from '../components/Pasos.jsx'
import CampoBusqueda from '../components/CampoBusqueda.jsx'
import PieAcciones from '../components/PieAcciones.jsx'
import ModalCantidad from '../components/ModalCantidad.jsx'
import ModalSeleccionados from '../components/ModalSeleccionados.jsx'
import { normalizar } from '../utils/texto.js'

// Datos de ejemplo mientras no se conecte /api/materiales.
const MATERIALES = [
    {
        id: 1, nombre: 'Limpiador Multiusos', categoria: 'Líquidos', codigo: 'LQ-0145',
        estado: 'Disponible', refill: '24 - Octubre',
        presentaciones: ['Botella de 1 L', 'Garrafa de 5 L', 'Galón'],
    },
    {
        id: 2, nombre: 'Aspiradora industrial', categoria: 'Equipo eléctrico', codigo: 'ACT-0421',
        estado: 'Disponible', presentaciones: ['Pieza'],
    },
    {
        id: 3, nombre: 'Rodillo para pintar', categoria: 'Pintura', codigo: 'ACT-0310',
        estado: 'Disponible', presentaciones: ['Pieza'],
    },
]
const BADGE = { Disponible: 'ok', 'Stock bajo': 'warn', Agotado: 'bad' }

export default function EntregaMateriales() {
    const { state } = useLocation()
    const solicitante = state?.solicitante
    const [busqueda, setBusqueda] = useState('')
    const [items, setItems] = useState([])
    const [materialActivo, setMaterialActivo] = useState(null)
    const [verSeleccionados, setVerSeleccionados] = useState(false)

    const texto = normalizar(busqueda.trim())
    const visibles = MATERIALES.filter(
        (m) => !texto || normalizar(`${m.nombre} ${m.codigo}`).includes(texto),
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

    return (
        <Layout
            active="servicios"
            crumbs={[{ label: 'Servicios Generales', to: '/' }, { label: 'Entrega de material' }]}
            back="/entregas/nueva"
            title="Entrega de material y/o equipo"
            subtitle="Indica el tipo y la cantidad de cada material a entregar."
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

                    <div className="table-wrap">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th scope="col">Material</th>
                                    <th scope="col">Código</th>
                                    <th scope="col">Disponibilidad</th>
                                </tr>
                            </thead>
                            <tbody>
                                {visibles.map((m) => (
                                    <tr
                                        key={m.id}
                                        className={`pick ${items.some((i) => i.material.id === m.id) ? 'selected' : ''}`}
                                        tabIndex={0}
                                        onClick={() => setMaterialActivo(m)}
                                        onKeyDown={(e) => e.key === 'Enter' && setMaterialActivo(m)}
                                    >
                                        <td><strong>{m.nombre}</strong><small>{m.categoria}</small></td>
                                        <td>{m.codigo}</td>
                                        <td><span className={`badge ${BADGE[m.estado]}`}>{m.estado}</span></td>
                                    </tr>
                                ))}
                                {visibles.length === 0 && (
                                    <tr>
                                        <td colSpan={3} className="muted center">
                                            No hay materiales que coincidan con la búsqueda.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </section>

                <PieAcciones volver="/entregas/nueva">
                    <small role="status">
                        {total} {total === 1 ? 'material seleccionado' : 'materiales seleccionados'}
                    </small>
                    {/* Navegar al paso 3 cuando exista */}
                    <button type="button" className="btn primary" disabled={total === 0}>
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