import { useState } from 'react'
import { Link } from 'react-router-dom'
import { ArrowLeft, Check, ChevronDown, Plus } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import { registrarMaterial } from '../api/materiales.js'
import { useCatalogos } from '../hooks/useCatalogos.js'

const VALORES_INICIALES = {
    codigo: '',
    nombre: '',
    categoria: '',
    campus: '',
    unidadMedida: '',
    presentacion: '',
    existenciaInicial: '',
    stockMinimo: '',
    stockMaximo: '',
    usoPrecauciones: '',
}

const aNumero = (texto) => (texto === '' ? null : Number(texto))

export default function RegistrarFicha() {
    const { catalogos, error: errorCatalogos } = useCatalogos()
    const [valores, setValores] = useState(VALORES_INICIALES)
    const [mensajeError, setMensajeError] = useState(null)
    const [enviando, setEnviando] = useState(false)

    const cambiar = (e) => {
        const { name, value } = e.target
        setValores((anteriores) => ({ ...anteriores, [name]: value }))
    }

    const enviar = async (e) => {
        e.preventDefault()
        setEnviando(true)
        setMensajeError(null)
        try {
            await registrarMaterial({
                codigo: valores.codigo.trim(),
                nombre: valores.nombre.trim(),
                categoria: valores.categoria,
                campus: valores.campus,
                unidadMedida: valores.unidadMedida,
                presentacion: valores.presentacion,
                existenciaInicial: aNumero(valores.existenciaInicial),
                stockMinimo: aNumero(valores.stockMinimo),
                stockMaximo: aNumero(valores.stockMaximo),
                usoPrecauciones: valores.usoPrecauciones.trim() || null,
            })
            setValores(VALORES_INICIALES)
        } catch (error) {
            setMensajeError(error.mensaje)
        } finally {
            setEnviando(false)
        }
    }

    return (
        <Layout
            active="admin"
            crumbs={[{ label: 'Administración', to: '/' }, { label: 'Fichas técnicas' }, { label: 'Registrar' }]}
            back="/inventario"
            title="Registrar ficha técnica"
            subtitle="Da de alta un nuevo producto en el catálogo."
        >
            <form className="stack" onSubmit={enviar}>
                {mensajeError && <div className="alert error" role="alert">{mensajeError}</div>}
                {errorCatalogos && (
                    <div className="alert error" role="alert">
                        No se pudieron cargar las opciones del formulario. {errorCatalogos.mensaje}
                    </div>
                )}

                <section className="card">
                    <div className="card-head">
                        <h2>Información general</h2>
                        <small>Datos del producto en el catálogo</small>
                    </div>
                    <div className="form">
                        <div className="row2">
                            <label className="field">
                                <span>Código</span>
                                <span className="input">
                                    <input name="codigo" placeholder="Ej. PIN-0001" value={valores.codigo} onChange={cambiar} />
                                </span>
                            </label>
                            <label className="field">
                                <span>Nombre del producto</span>
                                <span className="input">
                                    <input name="nombre" required placeholder="Ej. Limpiador multiusos" value={valores.nombre} onChange={cambiar} />
                                </span>
                            </label>
                        </div>

                        <div className="row2">
                            <label className="field">
                                <span>Categoría</span>
                                <span className="input select">
                                    <select name="categoria" value={valores.categoria} onChange={cambiar} disabled={!catalogos}>
                                        <option value="" disabled>{catalogos ? 'Selecciona una categoría' : 'Cargando…'}</option>
                                        {(catalogos?.categorias ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                            <label className="field">
                                <span>Campus</span>
                                <span className="input select">
                                    <select name="campus" value={valores.campus} onChange={cambiar} disabled={!catalogos}>
                                        <option value="" disabled>{catalogos ? 'Selecciona un campus' : 'Cargando…'}</option>
                                        {(catalogos?.campus ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                        </div>

                        <div className="row2">
                            <label className="field">
                                <span>Unidad de medida</span>
                                <span className="input select">
                                    <select name="unidadMedida" required value={valores.unidadMedida} onChange={cambiar} disabled={!catalogos}>
                                        <option value="" disabled>{catalogos ? 'Selecciona una unidad' : 'Cargando…'}</option>
                                        {(catalogos?.unidadesMedida ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                            <label className="field">
                                <span>Presentación</span>
                                <span className="input select">
                                    <select name="presentacion" required value={valores.presentacion} onChange={cambiar} disabled={!catalogos}>
                                        <option value="" disabled>{catalogos ? 'Selecciona una presentación' : 'Cargando…'}</option>
                                        {(catalogos?.presentaciones ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                        </div>

                        <div className="row2">
                            <label className="field">
                                <span>Stock mínimo</span>
                                <span className="input">
                                    <input name="stockMinimo" type="number" min="0" inputMode="numeric" required placeholder="0" value={valores.stockMinimo} onChange={cambiar} />
                                </span>
                            </label>
                            <label className="field">
                                <span>Stock máximo</span>
                                <span className="input">
                                    <input name="stockMaximo" type="number" min="0" inputMode="numeric" required placeholder="0" value={valores.stockMaximo} onChange={cambiar} />
                                </span>
                            </label>
                        </div>

                        <label className="field">
                            <span>Existencia inicial</span>
                            <span className="input">
                                <input name="existenciaInicial" type="number" min="0" inputMode="numeric" required placeholder="0" value={valores.existenciaInicial} onChange={cambiar} />
                            </span>
                        </label>

                        <label className="field">
                            <span>Uso y precauciones</span>
                            <span className="input">
                                <textarea name="usoPrecauciones" rows={3} placeholder="Indica cómo se usa el producto y las precauciones a tomar" value={valores.usoPrecauciones} onChange={cambiar} />
                            </span>
                        </label>
                    </div>
                </section>

                <section className="card">
                    <div className="card-head">
                        <h2>Áreas donde se utiliza</h2>
                        <small>Selecciona las áreas que consumen este producto</small>
                    </div>
                    <ul className="chips">
                        <li className="muted">Aún no has agregado áreas.</li>
                        <li>
                            <button type="button" className="chip-add">
                                <Plus size={14} aria-hidden="true" /> Agregar área
                            </button>
                        </li>
                    </ul>
                </section>

                <div className="card footbar">
                    <Link to="/inventario" className="btn">
                        <ArrowLeft size={16} /> Volver
                    </Link>
                    <button type="submit" className="btn primary" disabled={enviando}>
                        <Check size={16} /> {enviando ? 'Registrando…' : 'Registrar ficha'}
                    </button>
                </div>
            </form>
        </Layout>
    )
}