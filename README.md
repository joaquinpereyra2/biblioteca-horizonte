# Biblioteca Horizonte

Sistema de reservas de proyectores y notebooks para la Biblioteca Escolar Horizonte.
Proyecto Integrador - Ingeniería de Software 2026.

## Equipo

- Joaquin Pereyra
- Santiago Torretta
- Santiago Bruna
- Geronimo Dominguez

## Qué hace

- El docente inicia sesión, elige un equipo, una fecha y un módulo, y envía una solicitud (queda PENDIENTE).
- La bibliotecaria (Lucía) ve las solicitudes y las confirma o rechaza.
- No pueden existir dos reservas CONFIRMADAS para el mismo equipo, fecha y módulo.

## Tecnologías

- Backend: Java 21, Spring Boot, Spring Data JPA, base de datos H2 en memoria
- Frontend: React, TypeScript, Vite y CSS propio

## Estructura

- `Backend/`: API REST (Spring Boot)
- `Frontend/`: aplicación web (React)
- `docs/`: documentación de la entrega

## Cómo ejecutarlo

Requisitos: Java 21 y Node 18 o superior.

1. Backend (desde la carpeta `Backend`):

       mvnw.cmd spring-boot:run

   En Mac o Linux: `./mvnw spring-boot:run`. Queda en http://localhost:8080

2. Frontend (desde la carpeta `Frontend`):

       npm install
       npm run dev

   Queda en http://localhost:5173

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Docente | docente@horizonte.com | 1234 |
| Bibliotecaria | lucia@horizonte.com | 1234 |

Los datos se guardan en memoria: se pierden al reiniciar el backend.