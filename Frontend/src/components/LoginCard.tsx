import { useState, type FormEvent } from 'react'
import { api, ApiError } from '../services/api'
import type { Usuario } from '../types'
import { useToast } from '../context/ToastContext'

export function LoginCard({ onLogin }: { onLogin: (usuario: Usuario) => void }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const { notify } = useToast()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const usuario = await api.login(email, password)
      onLogin(usuario)
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'Correo o contraseña inválidos', 'error')
    } finally {
      setLoading(false)
    }
  }

  const fillDemo = (demoEmail: string) => {
    setEmail(demoEmail)
    setPassword('1234')
  }

  return (
    <div className="login-stage">
      <div className="library-card">
        <div className="library-card__tab">Biblioteca Horizonte</div>
        <header className="library-card__header">
          <h1>Carnet de acceso</h1>
          <p>Solicitá y gestioná el préstamo de proyectores y notebooks.</p>
        </header>

        <form onSubmit={handleSubmit} className="login-form">
          <label htmlFor="email">Correo electrónico</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="docente@horizonte.com"
            required
          />

          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit" className="btn btn--teal login-form__submit" disabled={loading}>
            {loading ? 'Ingresando…' : 'Ingresar'}
          </button>
        </form>

        <div className="demo-access">
          <span>Acceso de prueba</span>
          <div className="demo-access__buttons">
            <button type="button" onClick={() => fillDemo('docente@horizonte.com')}>
              Docente
            </button>
            <button type="button" onClick={() => fillDemo('lucia@horizonte.com')}>
              Bibliotecaria
            </button>
          </div>
          <p>Contraseña para ambos: 1234</p>
        </div>
      </div>
    </div>
  )
}
