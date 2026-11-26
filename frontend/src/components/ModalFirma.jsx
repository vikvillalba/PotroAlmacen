import { useEffect, useRef, useState } from 'react'
import { Info } from 'lucide-react'
import Modal from './Modal.jsx'

export default function ModalFirma({ nombre, inicial, onGuardar, onCerrar }) {
    const canvasRef = useRef(null)
    const dibujando = useRef(false)
    const [hayTrazo, setHayTrazo] = useState(false)

    // Si ya había una firma guardada, se vuelve a pintar para conservarla.
    useEffect(() => {
        if (!inicial) return
        const canvas = canvasRef.current
        const img = new Image()
        img.onload = () => {
            canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height)
            setHayTrazo(true)
        }
        img.src = inicial
    }, [inicial])

    const punto = (e) => {
        const canvas = canvasRef.current
        const r = canvas.getBoundingClientRect()
        return {
            x: (e.clientX - r.left) * (canvas.width / r.width),
            y: (e.clientY - r.top) * (canvas.height / r.height),
        }
    }

    const iniciar = (e) => {
        const canvas = canvasRef.current
        canvas.setPointerCapture(e.pointerId)
        const ctx = canvas.getContext('2d')
        ctx.lineWidth = 4
        ctx.lineCap = 'round'
        ctx.lineJoin = 'round'
        ctx.strokeStyle = '#1a2332'
        const { x, y } = punto(e)
        ctx.beginPath()
        ctx.moveTo(x, y)
        dibujando.current = true
    }

    const mover = (e) => {
        if (!dibujando.current) return
        const ctx = canvasRef.current.getContext('2d')
        const { x, y } = punto(e)
        ctx.lineTo(x, y)
        ctx.stroke()
        setHayTrazo(true)
    }

    const terminar = () => {
        dibujando.current = false
    }

    return (
        <Modal
            titulo="Firma de Entregado"
            onCerrar={onCerrar}
            pie={
                <div className="modal-actions firma-acciones">
                    <button type="button" className="btn" onClick={onCerrar}>Cancelar</button>
                    <button
                        type="button"
                        className="btn primary"
                        disabled={!hayTrazo}
                        onClick={() => onGuardar(canvasRef.current.toDataURL('image/png'))}
                    >
                        Guardar Firma
                    </button>
                </div>
            }
        >
            <div className="firma-area">
                <p className="firma-nombre">
                    Nombre: <strong>{nombre}</strong>
                </p>
                <canvas
                    ref={canvasRef}
                    className="firma-canvas"
                    width={1184}
                    height={280}
                    onPointerDown={iniciar}
                    onPointerMove={mover}
                    onPointerUp={terminar}
                    onPointerCancel={terminar}
                    aria-label="Área para firmar"
                />
                <p className="alert info">
                    <Info size={16} aria-hidden="true" />
                    Firme en la Pantalla
                </p>
            </div>
        </Modal>
    )
}