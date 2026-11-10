import { Link, useNavigate } from 'react-router-dom'
import { Box, Wrench, Shield, Bell, ChevronDown, ChevronRight, ChevronLeft } from 'lucide-react'

const TABS = [
  { id: 'servicios', label: 'Servicios Generales', icon: Box },
  { id: 'mantenimiento', label: 'Mantenimiento', icon: Wrench },
  { id: 'admin', label: 'Administracion', icon: Shield },
]

export default function Layout({ active, crumbs = [], title, subtitle, back, actions, children }) {
  const navigate = useNavigate()
  return (
    <>
      <header className="topbar">
        <div className="container topbar-in">
          <nav className="tabs">
            {TABS.map(({ id, label, icon: Icon }) => (
              <button key={id} className={`tab ${active === id ? 'on' : ''}`}>
                <Icon size={16} /> {label}
              </button>
            ))}
          </nav>
          <button className="icon-btn bell" aria-label="Notificaciones">
            <Bell size={16} />
            <span className="dot" />
          </button>
          <div className="user">
            <span className="avatar">MR</span>
            <div>
              <strong>Marisol Ruiz</strong>
              <small>Almacén · Campus Nainari</small>
            </div>
            <ChevronDown size={16} color="#6b7280" />
          </div>
        </div>
      </header>

      <main className="container page">
        <div className="crumbs">
          {crumbs.map((c, i) => (
            <span key={c.label} className="crumb">
              {c.to ? <Link to={c.to}>{c.label}</Link> : <b>{c.label}</b>}
              {i < crumbs.length - 1 && <ChevronRight size={12} />}
            </span>
          ))}
        </div>
        <div className="page-head">
          <div className="page-title">
            {back && (
              <button className="icon-btn" onClick={() => navigate(back)} aria-label="Regresar">
                <ChevronLeft size={18} />
              </button>
            )}
            <div>
              <h1>{title}</h1>
              <p>{subtitle}</p>
            </div>
          </div>
          <div className="actions">{actions}</div>
        </div>
        {children}
      </main>
    </>
  )
}