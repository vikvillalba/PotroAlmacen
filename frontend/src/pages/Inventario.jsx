import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
    Box, AlertTriangle, X, Search, Download, Plus,
    ChevronDown, ChevronRight,
} from 'lucide-react'
import Layout from '../components/Layout.jsx'
import { listarMateriales } from '../api/materiales.js'
import { useCatalogos } from '../hooks/useCatalogos.js'

const BADGE = { Disponible: 'ok', 'Stock bajo': 'warn', Agotado: 'bad' }
const ESTADOS = ['Disponible', 'Stock bajo', 'Agotado']
const ESPERA_BUSQUEDA_MS = 300

export default function Inventario() {
    const { catalogos } = useCatalogos()
    const [busqueda, setBusqueda] = useState('')
    const [categoria, setCategoria] = useState('')
    const [campus, setCampus] = useState('')
    const [estado, setEstado] = useState('')
    const [intento, setIntento] = useState(0)
    const [resultado, setResultado] = useState(null)

    const clave = `${busqueda}|${categoria}|${campus}|${intento}`

    useEffect(() => {
        const controlador = new AbortController()
        const espera = setTimeout(() => {
            listarMateriales({ busqueda: busqueda.trim(), categoria, campus }, controlador.signal)
                .then((datos) => setResultado({ clave, datos }))
                .catch((error) => {
                    if (error.name !== 'AbortError') setResultado({ clave, error })
                })
        }, ESPERA_BUSQUEDA_MS)
        return () => {
            clearTimeout(espera)
            controlador.abort()
        }
    }, [busqueda, categoria, campus, intento, clave])

    const cargando = resultado?.clave !== clave
    const error = cargando ? null : resultado.error
    const materiales = cargando || error ? [] : resultado.datos
    const visibles = estado ? materiales.filter((m) => m.estado === estado) : materiales
    const conteo = (nombre) => materiales.filter((m) => m.estado === nombre).length

    const filtros = [
        {
            label: 'Categoría', name: 'categoria', valor: categoria, cambiar: setCategoria, todas: 'Todas',
            opciones: catalogos?.categorias ?? [],
        },
        {
            label: 'Existencia', name: 'existencia', valor: estado, cambiar: setEstado, todas: 'Todas',
            opciones: ESTADOS.map((e) => ({ id: e, nombre: e })),
        },
        {
            label: 'Campus', name: 'campus', valor: campus, cambiar: setCampus, todas: 'Todos',
            opciones: catalogos?.campus ?? [],
        },
    ]

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
                    <span className="stat-value">{materiales.length}</span>
                </button>
                <button className="card stat">
                    <span className="stat-top">
                        <span className="label">Con stock bajo</span>
                        <span className="chip-icon amber" aria-hidden="true"><AlertTriangle size={18} /></span>
                    </span>
                    <span className="stat-value">{conteo('Stock bajo')}</span>
                </button>
                <button className="card stat">
                    <span className="stat-top">
                        <span className="label">Agotados</span>
                        <span className="chip-icon red" aria-hidden="true"><X size={18} /></span>
                    </span>
                    <span className="stat-value">{conteo('Agotado')}</span>
                </button>
            </section>

            <section className="card">
                <form className="filters" role="search" onSubmit={(e) => e.preventDefault()}>
                    <label className="field grow">
                        <span>Buscar</span>
                        <span className="input">
                            <Search size={16} color="#9ca3af" aria-hidden="true" />
                            <input
                                type="search"
                                name="buscar"
                                placeholder="Código o nombre"
                                value={busqueda}
                                onChange={(e) => setBusqueda(e.target.value)}
                            />
                        </span>
                    </label>
                    {filtros.map(({ label, name, valor, cambiar, todas, opciones }) => (
                        <label className="field" key={name}>
                            <span>{label}</span>
                            <span className="input select">
                                <select name={name} value={valor} onChange={(e) => cambiar(e.target.value)}>
                                    <option value="">{todas}</option>
                                    {opciones.map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                </select>
                                <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                            </span>
                        </label>
                    ))}
                </form>

                {error && (
                    <div className="alert error" role="alert">
                        {error.mensaje}{' '}
                        <button type="button" className="btn" onClick={() => setIntento((n) => n + 1)}>
                            Reintentar
                        </button>
                    </div>
                )}

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
                            {visibles.map((p) => (
                                <tr key={p.id}>
                                    <td className="muted">{p.codigo}</td>
                                    <td>
                                        <strong>{p.nombre}</strong>
                                        <small>{[p.presentacion, p.contenidoPresentacion].filter(Boolean).join(' · ')}</small>
                                    </td>
                                    <td>{p.categoria}</td>
                                    <td>{p.campus}</td>
                                    <td><strong>{p.cantidad} {p.unidadMedida}</strong><small>Mínimo {p.stockMinimo}</small></td>
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
                    <small role="status">
                        {cargando
                            ? 'Cargando productos…'
                            : error
                                ? ''
                                : visibles.length === 0
                                    ? 'No hay productos que coincidan con la búsqueda.'
                                    : `Mostrando ${visibles.length} ${visibles.length === 1 ? 'producto' : 'productos'}`}
                    </small>
                </div>
            </section>
        </Layout>
    )
}