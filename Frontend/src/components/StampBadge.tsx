import type { EstadoReserva } from '../types'

const LABEL: Record<EstadoReserva, string> = {
  PENDIENTE: 'Pendiente',
  CONFIRMADA: 'Confirmada',
  RECHAZADA: 'Rechazada',
}

export function StampBadge({ estado }: { estado: EstadoReserva }) {
  return (
    <span className={`stamp stamp--${estado.toLowerCase()}`}>
      {LABEL[estado]}
    </span>
  )
}
