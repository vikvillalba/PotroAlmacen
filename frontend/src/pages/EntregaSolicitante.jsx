import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import Pasos from '../components/Pasos.jsx'
import CampoBusqueda from '../components/CampoBusqueda.jsx'
import CampoSelect from '../components/CampoSelect.jsx'
import PieAcciones from '../components/PieAcciones.jsx'
import { normalizar } from '../utils/texto.js'
import { listarEmpleados } from '../api/empleados.js'

const BADGE = { Activo: 'ok', 'En préstamo': 'warn', Inactivo: 'err' }

export default function EntregaSolicitante() {
    const navigate = useNavigate()
    const [solicitantes, setSolicitantes] = useState([])
    const [cargando, setCargando] = useState(true)
    const [error, setError] = useState(null)
    const [busqueda, setBusqueda] = useState('')
    const [area, setArea] = useState('')
    const [seleccionado, setSeleccionado] = useState(null)

    useEffect(() => {
        listarEmpleados()
            .then((data) => setSolicitantes(data))
            .catch((err) => setError(err.message))
            .finally(() => setCargando(false))
    }, [])

    const areas = [...new Set(solicitantes.map((s) => s.area))].map((a) => ({ id: a, nombre: a }))
    const texto = normalizar(busqueda.trim())
    const visibles = solicitantes.filter(
        (s) =>
            (!area || s.area === area) &&
            (!texto || normalizar(`${s.nombre} ${s.area}`).includes(texto)),
    )

    return (
        <Layout
            active="servicios"
            crumbs={[{ label: 'Servicios Generales', to: '/' }, { label: 'Entrega de material' }]}
            back="/"
            title="Entrega de material y/o equipo"
            subtitle="Selecciona a la persona o área que solicita el material."
        >
            <div className="stack">
                <Pasos actual={1} />

                <section className="card">
                    <div className="card-head split">
                        <div>
                            <h2>¿Quién solicita?</h2>
                            <small>Busca por apellido, nombre o área</small>
                        </div>
                        <button type="button" className="btn primary">Entrega especial</button>
                    </div>

                    <form className="filters" role="search" onSubmit={(e) => e.preventDefault()}>
                        <CampoBusqueda
                            etiqueta="Buscar solicitante"
                            placeholder="Apellido, nombre o área"
                            valor={busqueda}
                            onCambio={setBusqueda}
                        />
                        <CampoSelect
                            etiqueta="Área"
                            name="area"
                            valor={area}
                            onCambio={setArea}
                            todas="Todas las áreas"
                            opciones={areas}
                        />
                    </form>

                    {error && <p className="alert error" role="alert">{error}</p>}

                    <div className="table-wrap">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th scope="col"><span className="sr-only">Seleccionar</span></th>
                                    <th scope="col">Nombre</th>
                                    <th scope="col">Área</th>
                                    <th scope="col">Tipo de empleado</th>
                                    <th scope="col">Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {cargando && (
                                    <tr>
                                        <td colSpan={5} className="muted center">Cargando personal...</td>
                                    </tr>
                                )}
                                {!cargando && visibles.map((s) => (
                                    <tr
                                        key={s.id}
                                        className={`pick ${seleccionado?.id === s.id ? 'selected' : ''}`}
                                        onClick={() => setSeleccionado(s)}
                                    >
                                        <td className="pick-cell">
                                            <input
                                                type="radio"
                                                name="solicitante"
                                                checked={seleccionado?.id === s.id}
                                                onChange={() => setSeleccionado(s)}
                                                aria-label={`Seleccionar a ${s.nombre}`}
                                            />
                                        </td>
                                        <td><strong>{s.nombre}</strong><small>{s.puesto}</small></td>
                                        <td>{s.area}</td>
                                        <td>{s.tipo}</td>
                                        <td><span className={`badge ${BADGE[s.estado] || ''}`}>{s.estado}</span></td>
                                    </tr>
                                ))}
                                {!cargando && visibles.length === 0 && (
                                    <tr>
                                        <td colSpan={5} className="muted center">
                                            No hay solicitantes que coincidan con la búsqueda.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </section>

                <PieAcciones volver="/">
                    {seleccionado && (
                        <small role="status">
                            Seleccionado: {seleccionado.nombre} · {seleccionado.area}
                        </small>
                    )}
                    <button
                        type="button"
                        className="btn primary"
                        disabled={!seleccionado}
                        onClick={() => navigate('/entregas/nueva/materiales', { state: { solicitante: seleccionado } })}
                    >
                        Siguiente <ArrowRight size={16} />
                    </button>
                </PieAcciones>
            </div>
        </Layout>
    )
}