import { useState, type FormEvent } from 'react'
import type { Reserva } from '../types'

interface Props {
  reserva: Reserva
  onCancel: () => void
  onConfirm: (motivo: string) => void
  loading: boolean
}

export function RechazoDialog({ reserva, onCancel, onConfirm, loading }: Props) {
  const [motivo, setMotivo] = useState('')

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault()
    onConfirm(motivo.trim())
  }

  return (
    <div className="dialog-backdrop" onClick={onCancel}>
      <form
        className="dialog-card"
        onClick={(e) => e.stopPropagation()}
        onSubmit={handleSubmit}
      >
        <h3>Rechazar solicitud</h3>
        <p className="dialog-context">
          {reserva.docente.nombre} · {reserva.equipo.nombre} · {reserva.fecha} · {reserva.moduloHorario}
        </p>
        <label htmlFor="motivo">Motivo (opcional)</label>
        <textarea
          id="motivo"
          value={motivo}
          onChange={(e) => setMotivo(e.target.value)}
          placeholder="Ej: el equipo ya tiene mantenimiento programado ese día"
          rows={3}
          autoFocus
        />
        <div className="dialog-actions">
          <button type="button" className="btn btn--ghost" onClick={onCancel} disabled={loading}>
            Cancelar
          </button>
          <button type="submit" className="btn btn--brick" disabled={loading}>
            {loading ? 'Rechazando…' : 'Confirmar rechazo'}
          </button>
        </div>
      </form>
    </div>
  )
}
