import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, Check, ChevronDown, Plus, X } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import ModalArea from '../components/ModalArea.jsx'
import { registrarMaterial } from '../api/materiales.js'
import { asignarMaterialArea, listarAreas } from '../api/areas.js'
import { useCatalogos } from '../hooks/useCatalogos.js'

const VALORES_INICIALES = {
    codigo: '',
    nombre: '',
    categoria: '',
    campus: '',
    unidadMedida: '',
    presentacion: '',
    contenidoPresentacion: '',
    precioUnitario: '',
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
const MAX_CONTENIDO = 100
const MAX_DIGITOS = 9

const esEntero = (texto) => /^\d+$/.test(String(texto).trim())
const esDecimalValido = (texto) => /^\d{1,8}(\.\d{1,2})?$/.test(String(texto).trim())
const soloDigitos = (texto) => texto.replace(/\D/g, '').slice(0, MAX_DIGITOS).replace(/^0+(?=\d)/, '')
const soloDecimal = (texto) => {
    const limpio = texto.replace(/[^0-9.]/g, '')
    const partes = limpio.split('.')
    if (partes.length <= 2) return limpio
    return `${partes[0]}.${partes.slice(1).join('')}`
}

const CAMPOS_NUMERICOS = ['stockMinimo', 'stockMaximo', 'existenciaInicial']

const DEPENDIENTES = {
    stockMinimo: ['stockMinimo', 'stockMaximo', 'existenciaInicial'],
    stockMaximo: ['stockMaximo', 'existenciaInicial'],
    existenciaInicial: ['existenciaInicial'],
    usoPrecauciones: ['usoPrecauciones'],
    contenidoPresentacion: ['contenidoPresentacion'],
    precioUnitario: ['precioUnitario'],
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

    if (v.contenidoPresentacion.trim().length > MAX_CONTENIDO) {
        e.contenidoPresentacion = `Máximo ${MAX_CONTENIDO} caracteres.`
    }

    const precio = String(v.precioUnitario).trim()
    if (precio !== '' && !esDecimalValido(precio)) {
        e.precioUnitario = 'Ingresa un precio válido (ej. 125.50).'
    }

    for (const [campo, etiqueta] of [
        ['stockMinimo', 'El stock mínimo'],
        ['existenciaInicial', 'La existencia inicial'],
    ]) {
        const texto = String(v[campo]).trim()
        if (texto === '') e[campo] = `${etiqueta} es obligatorio.`
        else if (!esEntero(texto)) e[campo] = `${etiqueta} debe ser un número entero mayor o igual a 0.`
    }

    const textoMax = String(v.stockMaximo).trim()
    if (textoMax !== '') {
        if (!esEntero(textoMax)) {
            e.stockMaximo = 'El stock máximo debe ser un número entero mayor o igual a 0.'
        } else {
            if (!e.stockMinimo && Number(v.stockMinimo) >= Number(textoMax)) {
                e.stockMaximo = `El stock máximo debe ser mayor que el stock mínimo (${v.stockMinimo}).`
            }
            if (!e.existenciaInicial && Number(v.existenciaInicial) > Number(textoMax)) {
                e.existenciaInicial = `La existencia inicial no puede superar el stock máximo (${textoMax}).`
            }
        }
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

    const [areasDisponibles, setAreasDisponibles] = useState([])
    const [areasAsignadas, setAreasAsignadas] = useState([])
    const [modalAreaAbierto, setModalAreaAbierto] = useState(false)

    useEffect(() => () => clearTimeout(temporizador.current), [])

    useEffect(() => {
        const controlador = new AbortController()
        listarAreas(controlador.signal)
            .then((datos) => setAreasDisponibles(datos ?? []))
            .catch((e) => {
                if (e.name !== 'AbortError') console.error('Error cargando áreas:', e)
            })
        return () => controlador.abort()
    }, [])

    const cambiar = (e) => {
        const { name } = e.target
        let value = e.target.value
        let aviso = null

        if (CAMPOS_NUMERICOS.includes(name)) value = soloDigitos(value)
        if (name === 'precioUnitario') value = soloDecimal(value)
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

    const agregarArea = (nuevaArea) => {
        setAreasAsignadas((prev) => {
            const existe = prev.some((item) => item.id === nuevaArea.id)
            if (existe) {
                return prev.map((item) => (item.id === nuevaArea.id ? nuevaArea : item))
            }
            return [...prev, nuevaArea]
        })
        setModalAreaAbierto(false)
    }

    const quitarArea = (id) => {
        setAreasAsignadas((prev) => prev.filter((a) => a.id !== id))
    }

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
            const codigoLimpio = valores.codigo.trim()
            const nombreLimpio = valores.nombre.trim()

            await registrarMaterial({
                codigo: codigoLimpio,
                nombre: nombreLimpio,
                categoria: valores.categoria,
                campus: valores.campus,
                unidadMedida: valores.unidadMedida,
                presentacion: valores.presentacion,
                contenidoPresentacion: valores.contenidoPresentacion.trim() || null,
                precioUnitario: aNumero(valores.precioUnitario.trim()),
                existenciaInicial: aNumero(valores.existenciaInicial),
                stockMinimo: aNumero(valores.stockMinimo),
                stockMaximo: aNumero(valores.stockMaximo),
                usoPrecauciones: valores.usoPrecauciones.trim() || null,
            })

            if (areasAsignadas.length > 0) {
                try {
                    await Promise.all(
                        areasAsignadas.map((area) =>
                            asignarMaterialArea(area.id, {
                                codigoMaterial: codigoLimpio,
                                nombreMaterial: nombreLimpio,
                                cantidad: area.cantidad,
                            }),
                        ),
                    )
                } catch (errArea) {
                    console.error('No se pudieron asociar las áreas:', errArea)
                }
            }

            setMensajeExito(`La ficha "${nombreLimpio}" se registró correctamente.`)
            setValores(VALORES_INICIALES)
            setAreasAsignadas([])
            setErrores({})
            temporizador.current = setTimeout(() => navigate('/inventario'), 1800)
            return
        } catch (error) {
            if (error.campos && Object.keys(error.campos).length > 0) {
                setErrores(error.campos)
            } else if (error.status === 409) {
                setErrores({ codigo: error.mensaje })
            }
            setMensajeError(error.mensaje)
        }
        setEnviando(false)
    }

    const areasNoAgregadas = areasDisponibles.filter(
        (a) => !areasAsignadas.some((asig) => asig.id === a.id),
    )

    return (
        <Layout
            active="admin"
            crumbs={[{ label: 'Administración', to: '/admin' }, { label: 'Fichas técnicas' }, { label: 'Registrar' }]}            back="/inventario"
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
                            <Campo nombre="contenidoPresentacion" etiqueta="Contenido de la presentación (opcional)" error={errores.contenidoPresentacion} ayuda="Ej. 20 litros, 12 piezas, 5 kg">
                                {(d) => <input {...propsCampo('contenidoPresentacion')} aria-describedby={d} maxLength={MAX_CONTENIDO} placeholder="Ej. Galón de 4 L" />}
                            </Campo>
                            <Campo nombre="precioUnitario" etiqueta="Precio unitario (opcional)" error={errores.precioUnitario} ayuda="Monto en pesos mexicanos (ej. 150.00)">
                                {(d) => <input {...propsCampo('precioUnitario')} aria-describedby={d} type="text" inputMode="decimal" autoComplete="off" placeholder="0.00" />}
                            </Campo>
                        </div>

                        <div className="row2">
                            <Campo nombre="stockMinimo" etiqueta="Stock mínimo" error={errores.stockMinimo}>
                                {(d) => <input {...propsCampo('stockMinimo')} aria-describedby={d} type="text" inputMode="numeric" autoComplete="off" placeholder="0" />}
                            </Campo>
                            <Campo nombre="stockMaximo" etiqueta="Stock máximo (opcional)" error={errores.stockMaximo}>
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
                        {areasAsignadas.length === 0 && (
                            <li className="muted">Aún no has agregado áreas.</li>
                        )}
                        {areasAsignadas.map((a) => (
                            <li key={a.id} className="chip">
                                <span>{a.nombreArea} · {a.cantidad}</span>
                                <button
                                    type="button"
                                    onClick={() => quitarArea(a.id)}
                                    aria-label={`Quitar ${a.nombreArea}`}
                                >
                                    <X size={14} aria-hidden="true" />
                                </button>
                            </li>
                        ))}
                        <li>
                            <button
                                type="button"
                                className="chip-add"
                                onClick={() => setModalAreaAbierto(true)}
                            >
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

            {modalAreaAbierto && (
                <ModalArea
                    areasDisponibles={areasNoAgregadas}
                    onAnadir={agregarArea}
                    onCerrar={() => setModalAreaAbierto(false)}
                />
            )}

            {mensajeExito && (
                <div className="toast success" role="status">
                    <Check size={18} aria-hidden="true" /> {mensajeExito}
                </div>
            )}
        </Layout>
    )
}