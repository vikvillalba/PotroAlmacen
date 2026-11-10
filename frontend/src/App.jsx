import { Routes, Route } from 'react-router-dom'
import Dashboard from './pages/DashboardAD.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Dashboard />} />
    </Routes>
  )
}