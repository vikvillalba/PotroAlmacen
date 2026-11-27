import { Link, useNavigate } from 'react-router-dom'
import { Box, Wrench, Shield, Bell, ChevronDown, ChevronRight, ChevronLeft } from 'lucide-react'

const TABS = [
  { id: 'servicios', label: 'Servicios Generales', icon: Box, to: '/' },
  { id: 'mantenimiento', label: 'Mantenimiento', icon: Wrench },
  { id: 'admin', label: 'Administración', icon: Shield, to: '/admin' },
]

export default function Layout({ active, crumbs = [], title, subtitle, back, backState, actions, children }) {
  const navigate = useNavigate()
  return (
      <>
        <header className="topbar">
          <div className="container topbar-in">
            <nav className="tabs" aria-label="Departamentos">
              {TABS.map(({ id, label, icon: Icon, to }) => {
                const propiedades = {
                  className: `tab ${active === id ? 'on' : ''}`,
                  'aria-current': active === id ? 'page' : undefined,
                }
                // Mantenimiento aún no tiene pantalla: se queda como botón sin destino.
                return to ? (
                    <Link key={id} to={to} {...propiedades}>
                      <Icon size={16} /> {label}
                    </Link>
                ) : (
                    <button key={id} type="button" {...propiedades}>
                      <Icon size={16} /> {label}
                    </button>
                )
              })}
            </nav>
            <button className="icon-btn bell" aria-label="Notificaciones">
              <Bell size={16} />
              <span className="dot" aria-hidden="true" />
            </button>
            <button className="user" aria-haspopup="menu" aria-label="Menú de usuario">
              <span className="avatar" aria-hidden="true">MR</span>
              <span className="user-info">
              <strong>Marisol Ruiz</strong>
              <small>Almacén · Campus Nainari</small>
            </span>
              <ChevronDown size={16} color="#6b7280" />
            </button>
          </div>
        </header>

        <main className="container page">
          {crumbs.length > 0 && (
          <nav className="crumbs" aria-label="Ruta de navegación">
            {crumbs.map((c, i) => {
              const ultimo = i === crumbs.length - 1
              return (
                  <span key={c.label} className="crumb">
                {ultimo ? (
                    <b aria-current="page">{c.label}</b>
                ) : c.to ? (
                    <Link to={c.to}>{c.label}</Link>
                ) : (
                    <span>{c.label}</span>
                )}
                    {!ultimo && <ChevronRight size={12} aria-hidden="true" />}
              </span>
              )
            })}
          </nav>
          )}
          {title && (
          <div className="page-head">
            <div className="page-title">
              {back && (
                  <button
                      className="icon-btn"
                      onClick={() => navigate(back, { state: backState })}
                      aria-label="Regresar"
                  >
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
          )}
          {children}
        </main>
      </>
  )
}