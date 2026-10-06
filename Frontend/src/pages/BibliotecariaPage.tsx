import { useEffect, useMemo, useState } from 'react'
import { AppHeader } from '../components/AppHeader'
import { StampBadge } from '../components/StampBadge'
import { RechazoDialog } from '../components/RechazoDialog'
import { api, ApiError } from '../services/api'
import type { Reserva, Usuario } from '../types'
import { useToast } from '../context/ToastContext'

export function BibliotecariaPage({ usuario, onLogout }: { usuario: Usuario; onLogout: () => void }) {
  const [reservas, setReservas] = useState<Reserva[]>([])
  const [loadingPage, setLoadingPage] = useState(true)
  const [actioningId, setActioningId] = useState<number | null>(null)
  const [rechazoObjetivo, setRechazoObjetivo] = useState<Reserva | null>(null)

  const { notify } = useToast()

  const cargar = async () => {
    const res = await api.getReservas()
    setReservas(res)
  }

  useEffect(() => {
    cargar()
      .catch((err) => notify(err instanceof ApiError ? err.message : 'No se pudieron cargar las solicitudes', 'error'))
      .finally(() => setLoadingPage(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const pendientes = useMemo(
    () => reservas.filter((r) => r.estado === 'PENDIENTE').sort((a, b) => (a.fecha < b.fecha ? -1 : 1)),
    [reservas],
  )
  const historial = useMemo(
    () =>
      reservas
        .filter((r) => r.estado !== 'PENDIENTE')
        .sort((a, b) => (a.fecha < b.fecha ? 1 : -1)),
    [reservas],
  )
  const contadores = useMemo(
    () => ({
      pendientes: pendientes.length,
      confirmadas: reservas.filter((r) => r.estado === 'CONFIRMADA').length,
      rechazadas: reservas.filter((r) => r.estado === 'RECHAZADA').length,
    }),
    [reservas, pendientes],
  )

  const handleConfirmar = async (r: Reserva) => {
    setActioningId(r.id)
    try {
      await api.confirmarReserva(r.id)
      notify(`Reserva confirmada: ${r.equipo.nombre} para ${r.docente.nombre}`)
      await cargar()
    } catch (err) {
      notify(
        err instanceof ApiError
          ? err.message
          : 'No se pudo confirmar. Puede que ya exista otra reserva confirmada para ese equipo, fecha y módulo.',
        'error',
      )
    } finally {
      setActioningId(null)
    }
  }

  const handleRechazar = async (motivo: string) => {
    if (!rechazoObjetivo) return
    setActioningId(rechazoObjetivo.id)
    try {
      await api.rechazarReserva(rechazoObjetivo.id, motivo)
      notify('Solicitud rechazada')
      setRechazoObjetivo(null)
      await cargar()
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'No se pudo rechazar la solicitud', 'error')
    } finally {
      setActioningId(null)
    }
  }

  return (
    <div className="page">
      <AppHeader usuario={usuario} subtitle="Mostrador de préstamos" onLogout={onLogout} />

      <main className="page__content">
        <section className="stat-row">
          <div className="stat-pill stat-pill--amber">
            <span className="stat-pill__value">{contadores.pendientes}</span>
            <span className="stat-pill__label">Pendientes</span>
          </div>
          <div className="stat-pill stat-pill--forest">
            <span className="stat-pill__value">{contadores.confirmadas}</span>
            <span className="stat-pill__label">Confirmadas</span>
          </div>
          <div className="stat-pill stat-pill--brick">
            <span className="stat-pill__value">{contadores.rechazadas}</span>
            <span className="stat-pill__label">Rechazadas</span>
          </div>
        </section>

        <section className="list-card">
          <h2>Pendientes de resolución</h2>

          {loadingPage && <p className="muted">Cargando solicitudes…</p>}

          {!loadingPage && pendientes.length === 0 && (
            <div className="empty-state">
              <p>No hay solicitudes esperando resolución.</p>
              <p className="muted">Las nuevas fichas de préstamo van a aparecer acá.</p>
            </div>
          )}

          <ul className="pending-list">
            {pendientes.map((r) => (
              <li key={r.id} className="pending-row">
                <div className="pending-row__info">
                  <span className="pending-row__docente">{r.docente.nombre}</span>
                  <span className="pending-row__meta">
                    {r.equipo.nombre} · {r.fecha} · {r.moduloHorario}
                  </span>
                </div>
                <div className="pending-row__actions">
                  <button
                    className="btn btn--forest"
                    onClick={() => handleConfirmar(r)}
                    disabled={actioningId === r.id}
                  >
                    {actioningId === r.id ? 'Sellando…' : 'Confirmar'}
                  </button>
                  <button
                    className="btn btn--ghost"
                    onClick={() => setRechazoObjetivo(r)}
                    disabled={actioningId === r.id}
                  >
                    Rechazar
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </section>

        <section className="list-card">
          <h2>Historial</h2>
          {!loadingPage && historial.length === 0 && <p className="muted">Todavía no hay resoluciones registradas.</p>}

          {historial.length > 0 && (
            <table className="history-table">
              <thead>
                <tr>
                  <th>Docente</th>
                  <th>Equipo</th>
                  <th>Fecha / Módulo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {historial.map((r) => (
                  <tr key={r.id}>
                    <td>{r.docente.nombre}</td>
                    <td>{r.equipo.nombre}</td>
                    <td>
                      {r.fecha} · {r.moduloHorario}
                    </td>
                    <td>
                      <StampBadge estado={r.estado} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </main>

      {rechazoObjetivo && (
        <RechazoDialog
          reserva={rechazoObjetivo}
          loading={actioningId === rechazoObjetivo.id}
          onCancel={() => setRechazoObjetivo(null)}
          onConfirm={handleRechazar}
        />
      )}
    </div>
  )
}
