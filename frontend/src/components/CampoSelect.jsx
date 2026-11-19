import { ChevronDown } from 'lucide-react'

export default function CampoSelect({ etiqueta, name, valor, onCambio, todas, opciones }) {
    return (
        <label className="field">
            <span>{etiqueta}</span>
            <span className="input select">
                <select name={name} value={valor} onChange={(e) => onCambio(e.target.value)}>
                    <option value="">{todas}</option>
                    {opciones.map((o) => <option key={o.id} value={o.id}>{o.nombre}</option>)}
                </select>
                <ChevronDown size={16} color="#6b7280" aria-hidden="true" />
            </span>
        </label>
    )
}