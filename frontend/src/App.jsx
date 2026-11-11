import { Routes, Route } from 'react-router-dom'
import Dashboard from './pages/DashboardAD.jsx'
import Inventario from './pages/Inventario.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Dashboard />} />
      <Route path="/inventario" element={<Inventario />} />
    </Routes>
  )
}