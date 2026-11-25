import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import Pasos from '../components/Pasos.jsx'
import CampoBusqueda from '../components/CampoBusqueda.jsx'
import CampoSelect from '../components/CampoSelect.jsx'
import PieAcciones from '../components/PieAcciones.jsx'
import { normalizar } from '../utils/texto.js'

// TEMPORAL: el backend aún no tiene GET de personal ni de áreas. Los ids y nombres coinciden con
// V202611192300__datos_mock_personal.sql para que el POST de la boleta encuentre al empleado.
const SOLICITANTES = [
    { id: 1, nombre: 'María López Hernández', puesto: 'Intendente', area: 'Edificio Académico A', tipo: 'ITSON', estado: 'Activo' },
    { id: 2, nombre: 'José Ramírez Torres', puesto: 'Intendente', area: 'Edificio Académico A', tipo: 'ITSON', estado: 'Activo' },
    { id: 3, nombre: 'Ana Beltrán', puesto: 'Auxiliar de mantenimiento', area: 'Laboratorio de Cómputo', tipo: 'ITSON', estado: 'Activo' },
    { id: 4, nombre: 'Luis Castro Valenzuela', puesto: 'Jardinero', area: 'Gimnasio Universitario', tipo: 'ITSON', estado: 'En préstamo' },
    { id: 5, nombre: 'Carmen Ortega Félix', puesto: 'Coordinadora de limpieza', area: 'Oficinas Administrativas', tipo: 'ITSON', estado: 'Activo' },
    { id: 8, nombre: 'Daniel Acosta Peña', puesto: 'Contratista de limpieza', area: 'Edificio Académico A', tipo: 'Externo', estado: 'Activo' },
]
const AREAS = [...new Set(SOLICITANTES.map((s) => s.area))].map((a) => ({ id: a, nombre: a }))
const BADGE = { Activo: 'ok', 'En préstamo': 'warn' }

export default function EntregaSolicitante() {
    const navigate = useNavigate()
    const [busqueda, setBusqueda] = useState('')
    const [area, setArea] = useState('')
    const [seleccionado, setSeleccionado] = useState(null)

    const texto = normalizar(busqueda.trim())
    const visibles = SOLICITANTES.filter(
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
                            opciones={AREAS}
                        />
                    </form>

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
                                {visibles.map((s) => (
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
                                        <td><span className={`badge ${BADGE[s.estado]}`}>{s.estado}</span></td>
                                    </tr>
                                ))}
                                {visibles.length === 0 && (
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