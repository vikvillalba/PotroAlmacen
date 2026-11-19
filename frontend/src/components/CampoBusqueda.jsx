import { Search } from 'lucide-react'

export default function CampoBusqueda({ etiqueta, name = 'buscar', placeholder, valor, onCambio }) {
    return (
        <label className="field grow">
            <span>{etiqueta}</span>
            <span className="input">
                <Search size={16} color="#9ca3af" aria-hidden="true" />
                <input
                    type="search"
                    name={name}
                    placeholder={placeholder}
                    value={valor}
                    onChange={(e) => onCambio(e.target.value)}
                />
            </span>
        </label>
    )
}