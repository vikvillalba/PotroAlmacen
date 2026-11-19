import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
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

function sinClave(objeto, clave) {
    const copia = { ...objeto }
    delete copia[clave]
    return copia
}

const MAX_USO = 500
const MAX_DIGITOS = 9

const esEntero = (texto) => /^\d+$/.test(String(texto).trim())
const soloDigitos = (texto) => texto.replace(/\D/g, '').slice(0, MAX_DIGITOS).replace(/^0+(?=\d)/, '')
const CAMPOS_NUMERICOS = ['stockMinimo', 'stockMaximo', 'existenciaInicial']

const DEPENDIENTES = {
    stockMinimo: ['stockMinimo', 'stockMaximo', 'existenciaInicial'],
    stockMaximo: ['stockMaximo', 'existenciaInicial'],
    existenciaInicial: ['existenciaInicial'],
    usoPrecauciones: ['usoPrecauciones'],
}

function validar(v) {
    const e = {}

    const codigo = v.codigo.trim()
    if (!codigo) e.codigo = 'El código es obligatorio.'
    else if (!/^[A-Za-z0-9-]+$/.test(codigo)) e.codigo = 'Usa solo letras, números y guiones (sin espacios).'
    else if (codigo.length < 3 || codigo.length > 20) e.codigo = 'El código debe tener entre 3 y 20 caracteres.'

    const nombre = v.nombre.trim()
    if (!nombre) e.nombre = 'El nombre del producto es obligatorio.'
    else if (nombre.length < 3) e.nombre = 'El nombre debe tener al menos 3 caracteres.'
    else if (nombre.length > 100) e.nombre = 'El nombre no puede superar los 100 caracteres.'

    if (!v.categoria) e.categoria = 'Selecciona una categoría.'
    if (!v.campus) e.campus = 'Selecciona un campus.'
    if (!v.unidadMedida) e.unidadMedida = 'Selecciona una unidad de medida.'
    if (!v.presentacion) e.presentacion = 'Selecciona una presentación.'

    for (const [campo, etiqueta] of [
        ['stockMinimo', 'El stock mínimo'],
        ['stockMaximo', 'El stock máximo'],
        ['existenciaInicial', 'La existencia inicial'],
    ]) {
        const texto = String(v[campo]).trim()
        if (texto === '') e[campo] = `${etiqueta} es obligatorio.`
        else if (!esEntero(texto)) e[campo] = `${etiqueta} debe ser un número entero mayor o igual a 0.`
    }

    if (!e.stockMinimo && !e.stockMaximo && Number(v.stockMinimo) >= Number(v.stockMaximo)) {
        e.stockMaximo = `El stock máximo debe ser mayor que el stock mínimo (${v.stockMinimo}).`
    }
    if (!e.existenciaInicial && !e.stockMaximo && Number(v.existenciaInicial) > Number(v.stockMaximo)) {
        e.existenciaInicial = `La existencia inicial no puede superar el stock máximo (${v.stockMaximo}).`
    }

    if (v.usoPrecauciones.trim().length > MAX_USO) e.usoPrecauciones = `Máximo ${MAX_USO} caracteres.`

    return e
}

function Campo({ nombre, etiqueta, error, ayuda, tipo = '', children }) {
    const idError = `error-${nombre}`
    return (
        <label className={`field${error ? ' invalid' : ''}`}>
            <span>{etiqueta}</span>
            <span className={`input ${tipo}`.trim()}>{children(error ? idError : undefined)}</span>
            {error && <small id={idError} className="field-error" role="alert">{error}</small>}
            {ayuda && <small className="field-hint">{ayuda}</small>}
        </label>
    )
}

export default function RegistrarFicha() {
    const navigate = useNavigate()
    const temporizador = useRef(null)
    const { catalogos, error: errorCatalogos } = useCatalogos()
    const [valores, setValores] = useState(VALORES_INICIALES)
    const [errores, setErrores] = useState({})
    const [mensajeError, setMensajeError] = useState(null)
    const [mensajeExito, setMensajeExito] = useState(null)
    const [enviando, setEnviando] = useState(false)

    // Evita navegar si el usuario sale de la pantalla antes de tiempo
    useEffect(() => () => clearTimeout(temporizador.current), [])

    const cambiar = (e) => {
        const { name } = e.target
        let value = e.target.value
        let aviso = null

        // Los campos de stock solo aceptan dígitos
        if (CAMPOS_NUMERICOS.includes(name)) value = soloDigitos(value)
        if (name === 'codigo') value = value.replace(/\s+/g, '')
        if (name === 'existenciaInicial' && esEntero(valores.stockMaximo) && esEntero(value) && Number(value) > Number(valores.stockMaximo)) {
            value = String(valores.stockMaximo)
            aviso = `Máximo permitido: ${valores.stockMaximo} (stock máximo).`
        }

        const nuevos = { ...valores, [name]: value }
        setValores(nuevos)
        setMensajeExito(null)

        const encontrados = validar(nuevos)
        const revisar = DEPENDIENTES[name]

        setErrores((anteriores) => {
            const siguientes = { ...anteriores }
            if (!revisar) {
                delete siguientes[name]
                return siguientes
            }

            for (const campo of revisar) {
                if (encontrados[campo]) siguientes[campo] = encontrados[campo]
                else delete siguientes[campo]
            }
            if (aviso) siguientes[name] = aviso
            return siguientes
        })
    }

    // Valida un campo al salir de él
    const alSalir = (e) => {
        const { name } = e.target
        const encontrado = validar(valores)[name]
        setErrores((anteriores) => {
            if (encontrado) return { ...anteriores, [name]: encontrado }
            if (!anteriores[name]) return anteriores
            return sinClave(anteriores, name)
        })
    }

    const propsCampo = (nombre) => ({
        name: nombre,
        value: valores[nombre],
        onChange: cambiar,
        onBlur: alSalir,
        'aria-invalid': errores[nombre] ? true : undefined,
    })

    const enviar = async (e) => {
        e.preventDefault()
        setMensajeError(null)
        setMensajeExito(null)

        const encontrados = validar(valores)
        setErrores(encontrados)
        if (Object.keys(encontrados).length > 0) {
            setMensajeError('Revisa los campos marcados en rojo antes de continuar.')
            requestAnimationFrame(() => {
                document.querySelector('[aria-invalid="true"]')?.focus()
            })
            return
        }

        setEnviando(true)
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
            setMensajeExito(`La ficha "${valores.nombre.trim()}" se registró correctamente.`)
            setValores(VALORES_INICIALES)
            setErrores({})
            // Se deja ver el mensaje un momento y se regresa al inventario
            temporizador.current = setTimeout(() => navigate('/inventario'), 1800)
            return
        } catch (error) {
            // Errores por campo devueltos por el servidor (p. ej. código duplicado)
            if (error.campos && Object.keys(error.campos).length > 0) setErrores(error.campos)
            setMensajeError(error.mensaje)
        }
        setEnviando(false)
    }

    return (
        <Layout
            active="admin"
            crumbs={[{ label: 'Administración', to: '/' }, { label: 'Fichas técnicas' }, { label: 'Registrar' }]}
            back="/inventario"
            title="Registrar ficha técnica"
            subtitle="Da de alta un nuevo producto en el catálogo."
        >
            <form className="stack" onSubmit={enviar} noValidate>
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
                            <Campo nombre="codigo" etiqueta="Código" error={errores.codigo} ayuda="De 3 a 20 caracteres: solo letras, números y guiones (sin espacios).">
                                {(d) => <input {...propsCampo('codigo')} aria-describedby={d} maxLength={20} placeholder="Ej. PIN-0001" />}
                            </Campo>
                            <Campo nombre="nombre" etiqueta="Nombre del producto" error={errores.nombre}>
                                {(d) => <input {...propsCampo('nombre')} aria-describedby={d} placeholder="Ej. Limpiador multiusos" />}
                            </Campo>
                        </div>

                        <div className="row2">
                            <Campo nombre="categoria" etiqueta="Categoría" error={errores.categoria} tipo="select">
                                {(d) => (
                                    <>
                                        <select {...propsCampo('categoria')} aria-describedby={d} disabled={!catalogos}>
                                            <option value="" disabled>{catalogos ? 'Selecciona una categoría' : 'Cargando…'}</option>
                                            {(catalogos?.categorias ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                        </select>
                                        <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                    </>
                                )}
                            </Campo>
                            <Campo nombre="campus" etiqueta="Campus" error={errores.campus} tipo="select">
                                {(d) => (
                                    <>
                                        <select {...propsCampo('campus')} aria-describedby={d} disabled={!catalogos}>
                                            <option value="" disabled>{catalogos ? 'Selecciona un campus' : 'Cargando…'}</option>
                                            {(catalogos?.campus ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                        </select>
                                        <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                    </>
                                )}
                            </Campo>
                        </div>

                        <div className="row2">
                            <Campo nombre="unidadMedida" etiqueta="Unidad de medida" error={errores.unidadMedida} tipo="select">
                                {(d) => (
                                    <>
                                        <select {...propsCampo('unidadMedida')} aria-describedby={d} disabled={!catalogos}>
                                            <option value="" disabled>{catalogos ? 'Selecciona una unidad' : 'Cargando…'}</option>
                                            {(catalogos?.unidadesMedida ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                        </select>
                                        <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                    </>
                                )}
                            </Campo>
                            <Campo nombre="presentacion" etiqueta="Presentación" error={errores.presentacion} tipo="select">
                                {(d) => (
                                    <>
                                        <select {...propsCampo('presentacion')} aria-describedby={d} disabled={!catalogos}>
                                            <option value="" disabled>{catalogos ? 'Selecciona una presentación' : 'Cargando…'}</option>
                                            {(catalogos?.presentaciones ?? []).map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                                        </select>
                                        <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
                                    </>
                                )}
                            </Campo>
                        </div>

                        <div className="row2">
                            <Campo nombre="stockMinimo" etiqueta="Stock mínimo" error={errores.stockMinimo}>
                                {(d) => <input {...propsCampo('stockMinimo')} aria-describedby={d} type="text" inputMode="numeric" autoComplete="off" placeholder="0" />}
                            </Campo>
                            <Campo nombre="stockMaximo" etiqueta="Stock máximo" error={errores.stockMaximo}>
                                {(d) => <input {...propsCampo('stockMaximo')} aria-describedby={d} type="text" inputMode="numeric" autoComplete="off" placeholder="0" />}
                            </Campo>
                        </div>

                        <Campo nombre="existenciaInicial" etiqueta="Existencia inicial" error={errores.existenciaInicial} ayuda={esEntero(valores.stockMaximo) ? `Máximo permitido: ${valores.stockMaximo}` : undefined}>
                            {(d) => <input {...propsCampo('existenciaInicial')} aria-describedby={d} type="text" inputMode="numeric" autoComplete="off" placeholder="0" />}
                        </Campo>

                        <Campo nombre="usoPrecauciones" etiqueta="Uso y precauciones (opcional)" error={errores.usoPrecauciones} ayuda={`${valores.usoPrecauciones.length}/${MAX_USO}`}>
                            {(d) => <textarea {...propsCampo('usoPrecauciones')} aria-describedby={d} rows={3} maxLength={MAX_USO} placeholder="Indica cómo se usa el producto y las precauciones a tomar" />}
                        </Campo>
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

            {mensajeExito && (
                <div className="toast success" role="status">
                    <Check size={18} aria-hidden="true" /> {mensajeExito}
                </div>
            )}
        </Layout>
    )
}