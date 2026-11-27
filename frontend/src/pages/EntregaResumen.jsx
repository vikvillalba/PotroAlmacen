import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import { Check, Plus } from 'lucide-react'
import Layout from '../components/Layout.jsx'
import { obtenerBoleta } from '../api/salidas.js'
import { RUTA_PANEL, formatoFechaBoleta, formatoHoraBoleta } from '../utils/boletas.js'

export default function EntregaResumen() {
    const { id } = useParams()
    const { state } = useLocation()
    const area = state?.area
    const [intento, setIntento] = useState(0)
    const [resultado, setResultado] = useState(null)

    const clave = `${id}|${intento}`

    useEffect(() => {
        const controlador = new AbortController()
        obtenerBoleta(id, controlador.signal)
            .then((boleta) => setResultado({ clave, boleta }))
            .catch((error) => {
                if (error.name !== 'AbortError') setResultado({ clave, error })
            })
        return () => controlador.abort()
    }, [id, clave])

    const cargando = resultado?.clave !== clave
    const error = cargando ? null : resultado.error
    const boleta = cargando || error ? null : resultado.boleta

    if (cargando) {
        return (
            <Layout active="servicios">
                <p className="exito-estado" role="status">Cargando comprobante…</p>
            </Layout>
        )
    }

    if (error) {
        return (
            <Layout active="servicios">
                <div className="card exito">
                    <p className="alert error" role="alert">{error.mensaje}</p>
                    <div className="exito-acciones">
                        <button type="button" className="btn" onClick={() => setIntento((n) => n + 1)}>
                            Reintentar
                        </button>
                        <Link to={RUTA_PANEL} className="btn primary">Volver al panel</Link>
                    </div>
                </div>
            </Layout>
        )
    }

    const productos = boleta.materiales.length

    return (
        <Layout active="servicios">
            <section className="card exito" aria-labelledby="exito-titulo">
                <span className="exito-icono" aria-hidden="true">
                    <span><Check size={30} strokeWidth={3} /></span>
                </span>
                <h1 id="exito-titulo">Entrega registrada</h1>
                <p>
                    Se {productos === 1 ? 'entregó 1 producto' : `entregaron ${productos} productos`} a{' '}
                    {boleta.nombreSolicitante}
                    {area ? ` (${area})` : ''}. El inventario ya fue actualizado.
                </p>

                <dl className="exito-datos">
                    <div>
                        <dt>Folio</dt>
                        <dd>{boleta.folio}</dd>
                    </div>
                    <div>
                        <dt>Fecha</dt>
                        <dd>{formatoFechaBoleta(boleta.fechaHora)} · {formatoHoraBoleta(boleta.fechaHora)}</dd>
                    </div>
                    <div>
                        <dt>Entregó</dt>
                        <dd>{boleta.nombreEntregador}</dd>
                    </div>
                </dl>

                <div className="exito-acciones">
                    <Link to={RUTA_PANEL} className="btn primary">Volver al panel</Link>
                </div>

                <div className="exito-enlaces">
                    <Link to="/entregas/nueva" className="btn-link"><Plus size={16} /> Registrar otra entrega</Link>
                </div>
            </section>
        </Layout>
    )
}