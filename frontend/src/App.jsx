import { Routes, Route } from 'react-router-dom'
import DashboardSG from './pages/DashboardSG.jsx'
import DashboardAD from './pages/DashboardAD.jsx'
import Inventario from './pages/Inventario.jsx'
import RegistrarFicha from './pages/RegistrarFicha.jsx'
import EntregaSolicitante from './pages/EntregaSolicitante.jsx'
import EntregaMateriales from './pages/EntregaMateriales.jsx'
import EntregaConfirmacion from './pages/EntregaConfirmacion.jsx'
import EntregaResumen from './pages/EntregaResumen.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<DashboardSG />} />
      <Route path="/admin" element={<DashboardAD />} />
      <Route path="/inventario" element={<Inventario />} />
      <Route path="/fichas/nueva" element={<RegistrarFicha />} />
      <Route path="/entregas/nueva" element={<EntregaSolicitante />} />
      <Route path="/entregas/nueva/materiales" element={<EntregaMateriales />} />
      <Route path="/entregas/nueva/confirmacion" element={<EntregaConfirmacion />} />
      <Route path="/entregas/:id/resumen" element={<EntregaResumen />} />
    </Routes>
  )
}