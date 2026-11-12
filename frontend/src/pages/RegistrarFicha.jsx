import { Link } from 'react-router-dom'
import { ArrowLeft, Check, ChevronDown, Plus } from 'lucide-react'
import Layout from '../components/Layout.jsx'

const UNIDADES = ['Litro (L)', 'Mililitro (ml)', 'Kilogramo (kg)', 'Pieza (pza)', 'Paquete', 'Par']
const PRESENTACIONES = ['Botella de 1 L', 'Galón de 4 L', 'Garrafa de 20 L', 'Paquete con 4', 'Caja', 'Pieza individual']

export default function RegistrarFicha() {
    return (
        <Layout
            active="admin"
            crumbs={[{ label: 'Administración', to: '/' }, { label: 'Fichas técnicas' }, { label: 'Registrar' }]}
            back="/inventario"
            title="Registrar ficha técnica"
            subtitle="Da de alta un nuevo producto en el catálogo."
        >
            <form className="stack" onSubmit={(e) => e.preventDefault()}>
                <section className="card">
                    <div className="card-head">
                        <h2>Información general</h2>
                        <small>Datos del producto en el catálogo</small>
                    </div>
                    <div className="form">
                        <label className="field">
                            <span>Nombre del producto</span>
                            <span className="input">
                                <input name="nombre" required placeholder="Ej. Limpiador multiusos" />
                            </span>
                        </label>

                        <div className="row2">
                            <label className="field">
                                <span>Unidad de medida</span>
                                <span className="input select">
                                    <select name="unidad" required defaultValue="">
                                        <option value="" disabled>Selecciona una unidad</option>
                                        {UNIDADES.map((u) => <option key={u}>{u}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                            <label className="field">
                                <span>Presentación</span>
                                <span className="input select">
                                    <select name="presentacion" required defaultValue="">
                                        <option value="" disabled>Selecciona una presentación</option>
                                        {PRESENTACIONES.map((p) => <option key={p}>{p}</option>)}
                                    </select>
                                    <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                </span>
                            </label>
                        </div>

                        <div className="row2">
                            <label className="field">
                                <span>Stock mínimo</span>
                                <span className="input">
                                    <input name="stockMinimo" type="number" min="0" inputMode="numeric" required placeholder="0" />
                                </span>
                            </label>
                            <label className="field">
                                <span>Stock máximo</span>
                                <span className="input">
                                    <input name="stockMaximo" type="number" min="0" inputMode="numeric" required placeholder="0" />
                                </span>
                            </label>
                        </div>

                        <label className="field">
                            <span>Stock actual</span>
                            <span className="input">
                                <input name="stockActual" type="number" min="0" inputMode="numeric" required placeholder="0" />
                            </span>
                        </label>

                        <label className="field">
                            <span>Uso y precauciones</span>
                            <span className="input">
                                <textarea name="uso" rows={3} placeholder="Indica cómo se usa el producto y las precauciones a tomar" />
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
                    <button type="submit" className="btn primary">
                        <Check size={16} /> Registrar ficha
                    </button>
                </div>
            </form>
        </Layout>
    )
}