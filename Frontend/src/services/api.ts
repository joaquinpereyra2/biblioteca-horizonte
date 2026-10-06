import type { Equipo, Reserva, Usuario } from '../types'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

class ApiError extends Error {
  status: number
  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

async function parseErrorBody(res: Response): Promise<string> {
  const raw = await res.text()
  if (!raw) return `Error ${res.status}`
  try {
    const parsed = JSON.parse(raw)
    return parsed?.message ?? parsed?.error ?? raw
  } catch {
    return raw
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  let res: Response
  try {
    res = await fetch(`${API_URL}${path}`, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    })
  } catch {
    throw new ApiError(
      'No se pudo conectar con el servidor. Verificá que el backend esté corriendo en ' + API_URL,
      0,
    )
  }
  if (!res.ok) {
    throw new ApiError(await parseErrorBody(res), res.status)
  }
  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

export const api = {
  login: (email: string, password: string) =>
    request<Usuario>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  getEquipos: () => request<Equipo[]>('/equipos'),

  getReservas: (docenteId?: number) =>
    request<Reserva[]>(docenteId ? `/reservas?docenteId=${docenteId}` : '/reservas'),

  crearReserva: (data: { equipoId: number; docenteId: number; fecha: string; moduloHorario: string }) =>
    request<Reserva>('/reservas', {
      method: 'POST',
      body: JSON.stringify({
        equipo: { id: data.equipoId },
        docente: { id: data.docenteId },
        fecha: data.fecha,
        moduloHorario: data.moduloHorario,
      }),
    }),

  confirmarReserva: (id: number, usuarioId: number) =>
    request<Reserva>(`/reservas/${id}/confirmar`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'X-Usuario-Id': String(usuarioId) },
    }),

  rechazarReserva: (id: number, motivo: string, usuarioId: number) =>
    request<Reserva>(`/reservas/${id}/rechazar`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'X-Usuario-Id': String(usuarioId) },
      body: JSON.stringify({ motivo }),
    }),
}

export { ApiError }
