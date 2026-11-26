import { useEffect, useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { Check, ChevronDown, Info, Pencil } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import Pasos from '../components/Pasos.jsx'
import PieAcciones from '../components/PieAcciones.jsx'
import ModalFirma from '../components/ModalFirma.jsx'
import { registrarEntrega, lineasDeEntrega } from '../api/salidas.js'
import { listarEmpleadosAlmacen } from '../api/empleados.js'

const CAMPUS = 'Nainari'
const FIRMA_OBLIGATORIA = false

const formatoFecha = (fecha) =>
    fecha.toLocaleDateString('es-MX', { day: '2-digit', month: '2-digit', year: 'numeric' })

export default function EntregaConfirmacion() {
    const { state } = useLocation()
    const navigate = useNavigate()
    const solicitante = state?.solicitante
    const items = state?.items ?? []

    const [fecha] = useState(() => new Date())
    const [entregadores, setEntregadores] = useState([])
    const [cargandoEntregadores, setCargandoEntregadores] = useState(true)
    const [idEntregador, setIdEntregador] = useState('')
    const [firma, setFirma] = useState(null)
    const [verFirma, setVerFirma] = useState(false)
    const [enviando, setEnviando] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        listarEmpleadosAlmacen()
            .then((lista) => {
                setEntregadores(lista)
                if (lista.length > 0) {
                    setIdEntregador(String(lista[0].id))
                }
            })
            .catch((e) => setError({ mensaje: e.message }))
            .finally(() => setCargandoEntregadores(false))
    }, [])

    if (!solicitante || items.length === 0) {
        return <Navigate to="/entregas/nueva" replace />
    }

    const estadoPrevio = { solicitante, items }

    const confirmar = async () => {
        setEnviando(true)
        setError(null)
        try {
            await registrarEntrega({
                idSolicitante: solicitante.id,
                idEntregador: Number(idEntregador),
                materiales: lineasDeEntrega(items),
            })
        } catch (e) {
            setError(e)
        } finally {
            setEnviando(false)
        }
    }

    const crumbs = [{ label: 'Servicios Generales', to: '/' }, { label: 'Entrega de material' }]

    return (
        <Layout
            active="servicios"
            crumbs={crumbs}
            back="/entregas/nueva/materiales"
            backState={estadoPrevio}
            title="Entrega de material y/o equipo"
            subtitle="Revisa la información antes de registrar la entrega."
        >
            <div className="stack">
                <Pasos actual={3} />

                <div className="confirm">
                    <section className="card">
                        <div className="card-head">
                            <h2>Resumen de la entrega</h2>
                        </div>
                        <dl className="resumen-lista">
                            <div className="resumen-fila">
                                <dt>Recibió</dt>
                                <dd>{solicitante.nombre}</dd>
                            </div>
                            <div className="resumen-fila">
                                <dt>Área de trabajo</dt>
                                <dd>{solicitante.area}</dd>
                            </div>
                            <div className="resumen-fila">
                                <dt>Campus</dt>
                                <dd>{CAMPUS}</dd>
                            </div>
                            <div className="resumen-fila">
                                <dt><label htmlFor="entregador">Entregado por</label></dt>
                                <dd className="entregador">
                                    <span className="input select">
                                        <select
                                            id="entregador"
                                            value={idEntregador}
                                            onChange={(e) => setIdEntregador(e.target.value)}
                                            disabled={cargandoEntregadores || entregadores.length === 0}
                                        >
                                            {cargandoEntregadores && (
                                                <option value="">Cargando personal de almacén...</option>
                                            )}
                                            {!cargandoEntregadores && entregadores.length === 0 && (
                                                <option value="">Sin personal de almacén disponible</option>
                                            )}
                                            {entregadores.map((p) => (
                                                <option key={p.id} value={p.id}>
                                                    {p.nombre} ({p.puesto})
                                                </option>
                                            ))}
                                        </select>
                                        <ChevronDown size={16} aria-hidden="true" />
                                    </span>
                                </dd>
                            </div>
                            <div className="resumen-fila">
                                <dt>Fecha y hora</dt>
                                <dd>{formatoFecha(fecha)} · Ahora</dd>
                            </div>
                        </dl>

                        <div className="firma-bloque">
                            <strong>Firma</strong>
                            {firma && <img className="firma-vista" src={firma} alt="Firma capturada" />}
                            <div>
                                <button type="button" className="btn primary" onClick={() => setVerFirma(true)}>
                                    {firma ? 'Cambiar firma' : 'Agregar Firma'}
                                </button>
                            </div>
                        </div>
                    </section>

                    <div className="stack">
                        <section className="card">
                            <div className="card-head split">
                                <div>
                                    <h2>Materiales solicitados</h2>
                                    <small>
                                        {items.length} {items.length === 1 ? 'producto' : 'productos'}
                                    </small>
                                </div>
                                <button
                                    type="button"
                                    className="btn soft"
                                    onClick={() => navigate('/entregas/nueva/materiales', { state: estadoPrevio })}
                                >
                                    <Pencil size={16} /> Editar
                                </button>
                            </div>
                            <div className="table-wrap">
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th scope="col">Código</th>
                                            <th scope="col">Producto</th>
                                            <th scope="col">Categoría</th>
                                            <th scope="col" className="num">Cantidad</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {items.map(({ material, cantidad }) => (
                                            <tr key={material.id}>
                                                <td className="muted">{material.codigo}</td>
                                                <td><strong>{material.nombre}</strong></td>
                                                <td>{material.categoria}</td>
                                                <td className="num">
                                                    <strong>{cantidad} {material.unidadMedida}</strong>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </section>

                        <p className="alert info">
                            <Info size={16} aria-hidden="true" />
                            Al confirmar, el inventario se descuenta automáticamente y se genera un comprobante con folio.
                        </p>

                        {error && <p className="alert error" role="alert">{error.mensaje}</p>}
                    </div>
                </div>

                <PieAcciones volver="/entregas/nueva/materiales" estadoVolver={estadoPrevio}>
                    <button
                        type="button"
                        className="btn primary"
                        disabled={enviando || !idEntregador || (FIRMA_OBLIGATORIA && !firma)}
                        onClick={confirmar}
                    >
                        <Check size={16} /> {enviando ? 'Registrando…' : 'Confirmar entrega'}
                    </button>
                </PieAcciones>
            </div>

            {verFirma && (
                <ModalFirma
                    nombre={solicitante.nombre}
                    inicial={firma}
                    onGuardar={(dataUrl) => {
                        setFirma(dataUrl)
                        setVerFirma(false)
                    }}
                    onCerrar={() => setVerFirma(false)}
                />
            )}
        </Layout>
    )
}