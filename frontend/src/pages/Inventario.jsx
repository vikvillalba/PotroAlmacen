import { Link } from 'react-router-dom'
import {
    Box, AlertTriangle, X, Search, Download, Plus,
    ChevronDown, ChevronRight, ChevronLeft,
} from 'lucide-react'
import Layout from '../components/Layout.jsx'

const PRODUCTOS = [
    { codigo: '001', nombre: 'Limpiador multiusos', detalle: 'Presentación 1 L', categoria: 'Líquidos', campus: 'Nainari', existencia: '70 L', minimo: '20 L', estado: 'Disponible' },
    { codigo: '007', nombre: 'Fabuloso lavanda', detalle: 'Presentación 1 L', categoria: 'Líquidos', campus: 'Nainari', existencia: '8 L', minimo: '15 L', estado: 'Stock bajo' },
    { codigo: '014', nombre: 'Rollo de papel higiénico', detalle: 'Paquete con 4', categoria: 'Papeles', campus: 'Nainari', existencia: '240 pzas', minimo: '80', estado: 'Disponible' },
    { codigo: '022', nombre: 'Bolsa negra jumbo', detalle: '90 × 120 cm', categoria: 'Bolsas', campus: 'Nainari', existencia: '0 pzas', minimo: '100', estado: 'Agotado' },
    { codigo: '031', nombre: 'Guantes de limpieza', detalle: 'Talla mediana', categoria: 'Guantes', campus: 'Nainari', existencia: '35 pares', minimo: '10', estado: 'Disponible' },
    { codigo: '040', nombre: 'Escoba de fibra', detalle: 'Uso rudo', categoria: 'Escobas', campus: 'Nainari', existencia: '12 pzas', minimo: '15', estado: 'Stock bajo' },
]

const BADGE = { Disponible: 'ok', 'Stock bajo': 'warn', Agotado: 'bad' }

const FILTROS = [
    { label: 'Categoría', opciones: ['Todas', 'Líquidos', 'Papeles', 'Bolsas', 'Guantes', 'Escobas'] },
    { label: 'Existencia', opciones: ['Todas', 'Disponible', 'Stock bajo', 'Agotado'] },
    { label: 'Campus', opciones: ['Nainari', 'Centro'] },
]

export default function Inventario() {
    return (
        <Layout
            active="admin"
            crumbs={[{ label: 'Administración', to: '/' }, { label: 'Inventario de productos' }]}
            back="/"
            title="Inventario de productos"
            subtitle="Existencias actuales del almacén por campus."
            actions={
                <>
                    <button className="btn"><Download size={16} /> Exportar</button>
                    <Link to="/fichas/nueva" className="btn primary"><Plus size={16} /> Nuevo producto</Link>
                </>
            }
        >
            <section className="stats s3" aria-label="Resumen del inventario">
                <button className="card stat">
                    <span className="stat-top">
                        <span className="label">Productos registrados</span>
                        <span className="chip-icon blue" aria-hidden="true"><Box size={18} /></span>
                    </span>
                    <span className="stat-value">128</span>
                </button>
                <button className="card stat">
                    <span className="stat-top">
                        <span className="label">Con stock bajo</span>
                        <span className="chip-icon amber" aria-hidden="true"><AlertTriangle size={18} /></span>
                    </span>
                    <span className="stat-value">9</span>
                </button>
                <button className="card stat">
                    <span className="stat-top">
                        <span className="label">Agotados</span>
                        <span className="chip-icon red" aria-hidden="true"><X size={18} /></span>
                    </span>
                    <span className="stat-value">2</span>
                </button>
            </section>

            <section className="card">
                <form className="filters" role="search" onSubmit={(e) => e.preventDefault()}>
                    <label className="field grow">
                        <span>Buscar</span>
                        <span className="input">
                            <Search size={16} color="#9ca3af" aria-hidden="true" />
                            <input type="search" name="buscar" placeholder="Código, nombre o categoría" />
                        </span>
                    </label>
                    {FILTROS.map(({ label, opciones }) => (
                        <label className="field" key={label}>
                            <span>{label}</span>
                            <span className="input select">
                                <select name={label.toLowerCase()}>
                                    {opciones.map((o) => <option key={o}>{o}</option>)}
                                </select>
                                <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                            </span>
                        </label>
                    ))}
                </form>

                <div className="table-wrap">
                    <table className="table">
                        <thead>
                            <tr>
                                <th scope="col">Código</th>
                                <th scope="col">Producto</th>
                                <th scope="col">Categoría</th>
                                <th scope="col">Campus</th>
                                <th scope="col">Existencias</th>
                                <th scope="col">Estado</th>
                                <th scope="col"><span className="sr-only">Acciones</span></th>
                            </tr>
                        </thead>
                        <tbody>
                            {PRODUCTOS.map((p) => (
                                <tr key={p.codigo}>
                                    <td className="muted">{p.codigo}</td>
                                    <td><strong>{p.nombre}</strong><small>{p.detalle}</small></td>
                                    <td>{p.categoria}</td>
                                    <td>{p.campus}</td>
                                    <td><strong>{p.existencia}</strong><small>Mínimo {p.minimo}</small></td>
                                    <td><span className={`badge ${BADGE[p.estado]}`}>{p.estado}</span></td>
                                    <td>
                                        <button className="row-btn" aria-label={`Ver detalle de ${p.nombre}`}>
                                            <ChevronRight size={16} color="#9ca3af" />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                <div className="pager">
                    <small>Mostrando 6 de 128 productos</small>
                    <nav className="pages" aria-label="Paginación">
                        <button className="pg" aria-label="Página anterior"><ChevronLeft size={14} /></button>
                        <button className="pg on" aria-label="Página 1" aria-current="page">1</button>
                        <button className="pg" aria-label="Página 2">2</button>
                        <button className="pg" aria-label="Página 3">3</button>
                        <span className="muted" aria-hidden="true">…</span>
                        <button className="pg" aria-label="Página 22">22</button>
                        <button className="pg" aria-label="Página siguiente"><ChevronRight size={14} /></button>
                    </nav>
                </div>
            </section>
        </Layout>
    )
}