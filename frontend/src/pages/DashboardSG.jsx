import { useState } from 'react'
import { Link } from 'react-router-dom'
import { AlertTriangle, ArrowRight, CalendarDays, ChevronRight, Inbox, MapPin, Plus, Truck, Users } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import { useResumenSG } from '../hooks/useResumenSG.js'
import { fechaLarga, haceCuanto, mismoDia } from '../utils/fechas.js'

const CAMPUS = 'Campus Nainari'
const RECIENTES = 5

// Aviso fijo mientras el backend no tenga calendario de cortes de inventario.
const PROXIMO_CORTE = { fecha: 'Viernes 9 de octubre · 13:00 h', detalle: 'Verifica existencias de líquidos y bolsas.' }

const MODULOS = [
    { title: 'Entregar material', sub: 'Registrar salida de material a un área', icon: Truck, to: '/entregas/nueva' },
    { title: 'Empleados / Personal', sub: 'Consulta y alta de personal', icon: Users },
    { title: 'Áreas y espacios', sub: 'Edificios, espacios y consumo', icon: MapPin },
]

const unidades = (n) => `${n} ${n === 1 ? 'unidad' : 'unidades'}`

/** Una boleta como fila de "entregas recientes": primer material (+ cuántos más) y total de unidades. */
function filaDeBoleta(boleta) {
    const [primero, ...resto] = boleta.materiales
    const total = boleta.materiales.reduce((suma, m) => suma + m.cantidad, 0)
    return {
        id: boleta.id,
        material: primero ? primero.nombreMaterial + (resto.length > 0 ? ` y ${resto.length} más` : '') : 'Sin materiales',
        detalle: unidades(total),
        solicitante: boleta.nombreSolicitante,
        folio: boleta.folio,
        fecha: new Date(boleta.fechaHora),
    }
}

export default function DashboardSG() {
    const [ahora] = useState(() => new Date())
    const { materiales, boletas, cargando, error } = useResumenSG()

    const agotados = materiales.filter((m) => m.estado === 'Agotado').length
    const stockBajo = materiales.filter((m) => m.estado === 'Stock bajo').length

    const entregas = boletas.filter((b) => b.estado !== 'CANCELADA').map(filaDeBoleta)
    const ayer = new Date(ahora)
    ayer.setDate(ayer.getDate() - 1)
    const entregasHoy = entregas.filter((e) => mismoDia(e.fecha, ahora)).length
    const entregasAyer = entregas.filter((e) => mismoDia(e.fecha, ayer)).length
    const diferencia = entregasHoy - entregasAyer
    const recientes = [...entregas].sort((a, b) => b.fecha - a.fecha).slice(0, RECIENTES)

    const valor = (n) => (cargando || error ? '—' : n)

    const stats = [
        {
            label: 'Productos agotados',
            value: valor(agotados),
            note: agotados > 0 ? 'Revisa el inventario' : 'Todo en existencia',
            noteClass: agotados > 0 ? 'red' : 'green',
            icon: AlertTriangle,
            tone: 'red',
            to: '/inventario',
        },
        {
            label: 'Entregas de hoy',
            value: valor(entregasHoy),
            note:
                diferencia === 0
                    ? 'Igual que ayer'
                    : `${diferencia > 0 ? '+' : ''}${diferencia} respecto a ayer`,
            noteClass: 'muted',
            icon: Truck,
            tone: 'blue',
        },
        {
            label: 'Productos stock bajo',
            value: valor(stockBajo),
            note: stockBajo > 0 ? 'Revisa el inventario' : 'Sin alertas de stock',
            noteClass: stockBajo > 0 ? 'amber' : 'green',
            icon: AlertTriangle,
            tone: 'amber',
            to: '/inventario',
        },
    ]

    return (
        <Layout
            active="servicios"
            crumbs={[{ label: 'Servicios Generales' }, { label: 'Inicio' }]}
            title="Panel de Servicios Generales"
            subtitle={`${fechaLarga(ahora)} · ${CAMPUS}`}
            actions={
                <>
                    <button type="button" className="btn">
                        <Inbox size={16} /> Recibir material
                    </button>
                    <Link to="/entregas/nueva" className="btn primary">
                        <Plus size={16} /> Nueva entrega
                    </Link>
                </>
            }
        >
            {error && (
                <p className="alert error" role="alert">
                    {error.mensaje}
                </p>
            )}

            <section className="stats s3" aria-label="Resumen del día">
                {stats.map(({ label, value, note, noteClass, icon: Icon, tone, to }) => {
                    const Tag = to ? Link : 'div'
                    return (
                        <Tag className="card stat" key={label} {...(to ? { to } : {})}>
                            <span className="stat-top">
                                <span className="label">{label}</span>
                                <span className={`chip-icon ${tone}`} aria-hidden="true">
                                    <Icon size={18} />
                                </span>
                            </span>
                            <span className="stat-value">{value}</span>
                            <small className={noteClass}>{note}</small>
                        </Tag>
                    )
                })}
            </section>

            <div className="sg-grid">
                <section className="card" aria-labelledby="titulo-recientes">
                    <div className="card-head split">
                        <h2 id="titulo-recientes">Entregas de material recientes</h2>
                        <button type="button" className="link-more">
                            Ver todas <ArrowRight size={16} aria-hidden="true" />
                        </button>
                    </div>
                    <div className="table-wrap">
                        <table className="table">
                            <thead>
                            <tr>
                                <th scope="col">Material</th>
                                <th scope="col">Solicitante</th>
                                <th scope="col">Recibida</th>
                            </tr>
                            </thead>
                            <tbody>
                            {recientes.map((e) => (
                                <tr key={e.id}>
                                    <td>
                                        <strong>{e.material}</strong>
                                        <small>{e.detalle}</small>
                                    </td>
                                    <td>
                                        <strong>{e.solicitante}</strong>
                                        <small>Folio {e.folio}</small>
                                    </td>
                                    <td className="muted">{haceCuanto(e.fecha, ahora)}</td>
                                </tr>
                            ))}
                            {cargando && (
                                <tr>
                                    <td colSpan={3} className="muted center">Cargando entregas…</td>
                                </tr>
                            )}
                            {!cargando && !error && recientes.length === 0 && (
                                <tr>
                                    <td colSpan={3} className="muted center">Aún no hay entregas registradas.</td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </section>

                <aside className="stack" aria-label="Módulos y avisos">
                    <section className="card">
                        <h2 className="panel-title">Módulos</h2>
                        <ul className="modulos">
                            {MODULOS.map(({ title, sub, icon: Icon, to }) => {
                                const Tag = to ? Link : 'button'
                                return (
                                    <li key={title}>
                                        <Tag className="modulo" {...(to ? { to } : { type: 'button' })}>
                                            <span className="chip-icon blue lg" aria-hidden="true">
                                                <Icon size={18} />
                                            </span>
                                            <span className="grow">
                                                <strong>{title}</strong>
                                                <small>{sub}</small>
                                            </span>
                                            <ChevronRight size={16} color="#9ca3af" aria-hidden="true" />
                                        </Tag>
                                    </li>
                                )
                            })}
                        </ul>
                    </section>

                    <section className="corte" aria-label="Próximo corte de inventario">
                        <span className="corte-label">
                            <CalendarDays size={16} aria-hidden="true" /> Próximo corte de inventario
                        </span>
                        <p>
                            {PROXIMO_CORTE.fecha}. {PROXIMO_CORTE.detalle}
                        </p>
                    </section>
                </aside>
            </div>
        </Layout>
    )
}