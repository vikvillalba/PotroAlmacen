import { Link } from 'react-router-dom'
import {
  BarChart3, Inbox, Truck, Wrench, AlertTriangle, DollarSign, Box, FileText,
  MapPin, Users, Shield, History, ChevronRight,
} from 'lucide-react'
import Layout from '../components/Layout.jsx'

const STATS = [
  { label: 'Entregas del mes', value: '214', note: '+8 % vs. septiembre', noteClass: 'green', icon: Truck, tone: 'green' },
  { label: 'Préstamos activos', value: '13', note: '5 vencen esta semana', icon: Wrench, tone: 'blue' },
  { label: 'Material con stock bajo', value: '9', note: 'Requiere reabastecimiento', noteClass: 'red', icon: AlertTriangle, tone: 'red', to: '/inventario' },
  { label: 'Consumo del mes', value: '$ 48,320', note: '72 % del presupuesto', icon: DollarSign, tone: 'blue' },
]

const ACCESOS = [
  { title: 'Inventario de productos', sub: 'Existencias por campus', icon: Box, to: '/inventario' },
  { title: 'Fichas técnicas', sub: 'Especificaciones de material', icon: FileText },
  { title: 'Áreas y espacios', sub: 'Edificios y consumo', icon: MapPin },
  { title: 'Reportes', sub: 'Indicadores mensuales', icon: BarChart3 },
  { title: 'Empleados / Personal', sub: 'Padrón de intendencia', icon: Users },
  { title: 'Seguridad y usuarios', sub: 'Roles y permisos', icon: Shield },
  { title: 'Auditoría', sub: 'Bitácora de movimientos', icon: History },
  { title: 'Recepción de material', sub: 'Entradas con factura', icon: Inbox },
]

export default function Dashboard() {
  return (
    <Layout
      active="admin"
      crumbs={[{ label: 'Administración' }, { label: 'Inicio' }]}
      title="Panel de administración"
      subtitle="Octubre 2026 · Resumen general de ambos departamentos"
      actions={
        <>
          <button className="btn"><BarChart3 size={16} /> Ver reportes</button>
          <button className="btn primary"><Inbox size={16} /> Recepción de material</button>
        </>
      }
    >
      <section className="stats s4" aria-label="Resumen del mes">
        {STATS.map(({ label, value, note, noteClass, icon: Icon, tone, to }) => {
          const Tag = to ? Link : 'button'
          return (
            <Tag className="card stat" key={label} {...(to ? { to } : {})}>
              <span className="stat-top">
                <span className="label">{label}</span>
                <span className={`chip-icon ${tone}`} aria-hidden="true"><Icon size={18} /></span>
              </span>
              <span className="stat-value">{value}</span>
              <small className={noteClass || 'muted'}>{note}</small>
            </Tag>
          )
        })}
      </section>

      <section className="card panel">
        <h2 className="panel-title">Accesos de administración</h2>
        <div className="accesos">
          {ACCESOS.map(({ title, sub, icon: Icon, to }) => {
            const Tag = to ? Link : 'button'
            return (
              <Tag className="acceso" key={title} {...(to ? { to } : {})}>
                <span className="chip-icon blue lg" aria-hidden="true"><Icon size={18} /></span>
                <span className="grow">
                  <strong>{title}</strong>
                  <small>{sub}</small>
                </span>
                <ChevronRight size={16} color="#9ca3af" aria-hidden="true" />
              </Tag>
            )
          })}
        </div>
      </section>
    </Layout>
  )
}