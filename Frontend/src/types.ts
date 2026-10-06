export type RolUsuario = 'DOCENTE' | 'BIBLIOTECARIA'
export type TipoEquipo = 'PROYECTOR' | 'NOTEBOOK'
export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'RECHAZADA'

export interface Usuario {
  id: number
  nombre: string
  legajo?: string
  correoElectronico: string
  rol: RolUsuario
}

export interface Equipo {
  id: number
  nombre: string
  tipo: TipoEquipo
  activo: boolean
}

export interface Reserva {
  id: number
  fecha: string
  moduloHorario: string
  estado: EstadoReserva
  motivoRechazo?: string | null
  docente: Usuario
  equipo: Equipo
}
