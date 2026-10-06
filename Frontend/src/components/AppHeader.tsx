import type { Usuario } from '../types'

export function AppHeader({
  usuario,
  subtitle,
  onLogout,
}: {
  usuario: Usuario
  subtitle: string
  onLogout: () => void
}) {
  return (
    <header className="app-header">
      <div className="app-header__brand">
        <span className="app-header__mark" aria-hidden="true" />
        <div>
          <p className="app-header__title">Biblioteca Horizonte</p>
          <p className="app-header__subtitle">{subtitle}</p>
        </div>
      </div>
      <div className="app-header__user">
        <div className="app-header__user-info">
          <span className="app-header__user-name">{usuario.nombre}</span>
          <span className="app-header__user-role">
            {usuario.rol === 'DOCENTE' ? 'Docente' : 'Bibliotecaria'}
          </span>
        </div>
        <button className="btn btn--ghost" onClick={onLogout}>
          Cerrar sesión
        </button>
      </div>
    </header>
  )
}
