# Biblioteca Horizonte — Frontend

Frontend en React + TypeScript + Vite para el sistema de préstamo de equipos
de la biblioteca escolar Horizonte. Pensado para conectarse al backend Spring
Boot (`biblioteca-horizonte`) ya existente, sin cambiar ningún contrato de API.

## Qué cambia respecto a la versión anterior

- Diseño propio (ficha de préstamo, carnet de biblioteca, sellos de estado)
  en lugar de clases sueltas de Tailwind sin compilar.
- Reemplaza `alert()` y `prompt()` por notificaciones (toasts) y un diálogo
  de confirmación real para el rechazo con motivo.
- Manejo de errores centralizado en `services/api.ts`, incluyendo el caso de
  "no se pudo conectar al backend".
- Estados de carga y estados vacíos en ambas pantallas.
- Mostrador de la bibliotecaria con contadores (pendientes / confirmadas /
  rechazadas) e historial separado de las solicitudes pendientes.
- URL del backend configurable por variable de entorno (`VITE_API_URL`), no
  hardcodeada.

## Requisitos

- Node 18 o superior.
- El backend `biblioteca-horizonte` corriendo (por defecto en
  `http://localhost:8080`), con CORS abierto como ya está configurado.

## Uso

```bash
npm install
cp .env.example .env   # opcional, si tu backend no está en localhost:8080
npm run dev
```

La app queda disponible en `http://localhost:5173`.

## Usuarios de prueba

Los mismos que carga el backend (`DataInitializer`):

| Rol          | Correo                  | Contraseña |
|--------------|--------------------------|------------|
| Docente      | docente@horizonte.com   | 1234       |
| Bibliotecaria| lucia@horizonte.com     | 1234       |

## Estructura

```
src/
  components/   LoginCard, AppHeader, StampBadge, RechazoDialog
  pages/        DocentePage, BibliotecariaPage
  context/      ToastContext (notificaciones)
  services/     api.ts (cliente HTTP hacia el backend)
  types.ts      Tipos compartidos (Usuario, Equipo, Reserva)
```

## Nota

Este frontend no agrega autenticación real (JWT/hash): sigue usando el mismo
endpoint `/api/auth/login` del backend, que compara contraseña en texto
plano. Eso es una limitación del backend, no de esta capa.
