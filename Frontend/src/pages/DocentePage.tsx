import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { AppHeader } from '../components/AppHeader'
import { StampBadge } from '../components/StampBadge'
import { api, ApiError } from '../services/api'
import type { Equipo, Reserva, Usuario } from '../types'
import { useToast } from '../context/ToastContext'

const MODULOS = ['Módulo 1', 'Módulo 2', 'Módulo 3', 'Módulo 4']

export function DocentePage({ usuario, onLogout }: { usuario: Usuario; onLogout: () => void }) {
  const [equipos, setEquipos] = useState<Equipo[]>([])
  const [reservas, setReservas] = useState<Reserva[]>([])
  const [loadingPage, setLoadingPage] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  const [equipoId, setEquipoId] = useState('')
  const [fecha, setFecha] = useState('')
  const [modulo, setModulo] = useState(MODULOS[0])

  const { notify } = useToast()

  const cargar = async () => {
    const [eq, res] = await Promise.all([api.getEquipos(), api.getReservas(usuario.id)])
    setEquipos(eq)
    if (eq.length > 0) setEquipoId((prev) => prev || String(eq[0].id))
    setReservas(res)
  }

  useEffect(() => {
    cargar()
      .catch((err) => notify(err instanceof ApiError ? err.message : 'No se pudieron cargar los datos', 'error'))
      .finally(() => setLoadingPage(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const ordenadas = useMemo(
    () =>
      [...reservas].sort((a, b) => (a.fecha < b.fecha ? 1 : a.fecha > b.fecha ? -1 : 0)),
    [reservas],
  )

  const handleSolicitar = async (e: FormEvent) => {
    e.preventDefault()
    if (!equipoId || !fecha) return
    setSubmitting(true)
    try {
      await api.crearReserva({
        equipoId: Number(equipoId),
        docenteId: usuario.id,
        fecha,
        moduloHorario: modulo,
      })
      notify('Solicitud registrada como pendiente')
      setFecha('')
      const res = await api.getReservas(usuario.id)
      setReservas(res)
    } catch (err) {
      notify(err instanceof ApiError ? err.message : 'No se pudo crear la solicitud', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <AppHeader usuario={usuario} subtitle="Panel docente" onLogout={onLogout} />

      <main className="page__content">
        <section className="slip-card">
          <div className="slip-card__tab">Ficha de préstamo</div>
          <h2>Solicitar un equipo</h2>
          <p className="muted">
            Elegí un equipo, una fecha y un módulo horario completo. Lucía confirmará o rechazará tu pedido.
          </p>

          <form onSubmit={handleSolicitar} className="request-form">
            <div className="field">
              <label htmlFor="equipo">Equipo</label>
              <select id="equipo" value={equipoId} onChange={(e) => setEquipoId(e.target.value)} disabled={equipos.length === 0}>
                {equipos.map((eq) => (
                  <option key={eq.id} value={eq.id}>
                    {eq.nombre} · {eq.tipo === 'PROYECTOR' ? 'Proyector' : 'Notebook'}
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="fecha">Fecha</label>
              <input id="fecha" type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} required />
            </div>

            <div className="field">
              <label htmlFor="modulo">Módulo horario</label>
              <select id="modulo" value={modulo} onChange={(e) => setModulo(e.target.value)}>
                {MODULOS.map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </div>

            <button type="submit" className="btn btn--teal" disabled={submitting || equipos.length === 0}>
              {submitting ? 'Enviando…' : 'Solicitar equipo'}
            </button>
          </form>
        </section>

        <section className="list-card">
          <h2>Mis solicitudes</h2>

          {loadingPage && <p className="muted">Cargando solicitudes…</p>}

          {!loadingPage && ordenadas.length === 0 && (
            <div className="empty-state">
              <p>Todavía no solicitaste ningún equipo.</p>
              <p className="muted">Cuando envíes una solicitud, va a aparecer acá como pendiente.</p>
            </div>
          )}

          <ul className="ticket-list">
            {ordenadas.map((r) => (
              <li key={r.id} className="ticket-row">
                <div className="ticket-row__main">
                  <span className="ticket-row__equipo">{r.equipo.nombre}</span>
                  <span className="ticket-row__meta">
                    {r.fecha} · {r.moduloHorario}
                  </span>
                  {r.estado === 'RECHAZADA' && r.motivoRechazo && (
                    <span className="ticket-row__motivo">Motivo: {r.motivoRechazo}</span>
                  )}
                </div>
                <StampBadge estado={r.estado} />
              </li>
            ))}
          </ul>
        </section>
      </main>
    </div>
  )
}
