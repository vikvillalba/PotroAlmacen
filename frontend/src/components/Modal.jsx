import { useEffect } from 'react'
import { X } from 'lucide-react'

export default function Modal({ titulo, subtitulo, onCerrar, pie, children }) {
    useEffect(() => {
        const onKey = (e) => e.key === 'Escape' && onCerrar()
        document.addEventListener('keydown', onKey)
        const previo = document.body.style.overflow
        document.body.style.overflow = 'hidden'
        return () => {
            document.removeEventListener('keydown', onKey)
            document.body.style.overflow = previo
        }
    }, [onCerrar])

    return (
        <div className="modal-overlay" onMouseDown={(e) => e.target === e.currentTarget && onCerrar()}>
            <div className="modal" role="dialog" aria-modal="true" aria-label={titulo}>
                <header className="modal-head">
                    <div>
                        <h2>{titulo}</h2>
                        {subtitulo && <small>{subtitulo}</small>}
                    </div>
                    <button type="button" className="icon-btn" onClick={onCerrar} aria-label="Cerrar">
                        <X size={16} />
                    </button>
                </header>
                <div className="table-wrap">{children}</div>
                <footer className="modal-foot">{pie}</footer>
            </div>
        </div>
    )
}