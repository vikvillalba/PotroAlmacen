import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'

export default function PieAcciones({ volver, estadoVolver, children }) {
    return (
        <div className="card footbar">
            <Link to={volver} state={estadoVolver} className="btn">
                <ArrowLeft size={16} /> Volver
            </Link>
            <div className="footbar-end">{children}</div>
        </div>
    )
}