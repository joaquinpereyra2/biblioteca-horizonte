import { useState } from 'react'
import { LoginCard } from './components/LoginCard'
import { DocentePage } from './pages/DocentePage'
import { BibliotecariaPage } from './pages/BibliotecariaPage'
import { ToastProvider } from './context/ToastContext'
import type { Usuario } from './types'

function AppShell() {
  const [usuario, setUsuario] = useState<Usuario | null>(null)

  if (!usuario) {
    return <LoginCard onLogin={setUsuario} />
  }

  if (usuario.rol === 'DOCENTE') {
    return <DocentePage usuario={usuario} onLogout={() => setUsuario(null)} />
  }

  return <BibliotecariaPage usuario={usuario} onLogout={() => setUsuario(null)} />
}

export default function App() {
  return (
    <ToastProvider>
      <AppShell />
    </ToastProvider>
  )
}
