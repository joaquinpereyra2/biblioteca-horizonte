package com.example.biblioteca_horizonte.service;

import com.example.biblioteca_horizonte.exception.RecursoNoEncontradoException;
import com.example.biblioteca_horizonte.exception.ReglaNegocioException;
import com.example.biblioteca_horizonte.model.Equipo;
import com.example.biblioteca_horizonte.model.Reserva;
import com.example.biblioteca_horizonte.model.Usuario;
import com.example.biblioteca_horizonte.model.enums.EstadoReserva;
import com.example.biblioteca_horizonte.model.enums.RolUsuario;
import com.example.biblioteca_horizonte.repository.EquipoRepository;
import com.example.biblioteca_horizonte.repository.ReservaRepository;
import com.example.biblioteca_horizonte.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final List<String> MODULOS_VALIDOS =
            List.of("Módulo 1", "Módulo 2", "Módulo 3", "Módulo 4");

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public Reserva crearReserva(Reserva reserva) {
        if (reserva.getFecha() == null) {
            throw new ReglaNegocioException("La fecha es obligatoria");
        }
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha no puede ser anterior a hoy");
        }
        if (reserva.getModuloHorario() == null || !MODULOS_VALIDOS.contains(reserva.getModuloHorario())) {
            throw new ReglaNegocioException("El módulo horario no es válido");
        }
        if (reserva.getEquipo() == null || reserva.getEquipo().getId() == null) {
            throw new ReglaNegocioException("Falta indicar el equipo");
        }
        if (reserva.getDocente() == null || reserva.getDocente().getId() == null) {
            throw new ReglaNegocioException("Falta indicar el docente");
        }

        Equipo equipo = equipoRepository.findById(reserva.getEquipo().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El equipo no existe"));
        if (!equipo.isActivo()) {
            throw new ReglaNegocioException("El equipo no está disponible");
        }

        Usuario docente = usuarioRepository.findById(reserva.getDocente().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El docente no existe"));
        if (docente.getRol() != RolUsuario.DOCENTE) {
            throw new ReglaNegocioException("Solo un docente puede solicitar un equipo");
        }

        reserva.setEquipo(equipo);
        reserva.setDocente(docente);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMotivoRechazo(null);
        return reservaRepository.save(reserva);
    }

    public Reserva confirmarReserva(Long id) {
        Reserva reserva = buscarPorId(id);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReglaNegocioException(
                    "Solo se pueden confirmar solicitudes pendientes. Estado actual: " + reserva.getEstado());
        }

        boolean yaExiste = reservaRepository.existsByEquipoIdAndFechaAndModuloHorarioAndEstado(
                reserva.getEquipo().getId(),
                reserva.getFecha(),
                reserva.getModuloHorario(),
                EstadoReserva.CONFIRMADA
        );

        if (yaExiste) {
            throw new ReglaNegocioException("Error: Ya existe una reserva CONFIRMADA para este equipo, fecha y módulo.");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    public Reserva rechazarReserva(Long id, String motivo) {
        Reserva reserva = buscarPorId(id);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReglaNegocioException(
                    "Solo se pueden rechazar solicitudes pendientes. Estado actual: " + reserva.getEstado());
        }

        reserva.setEstado(EstadoReserva.RECHAZADA);
        reserva.setMotivoRechazo(motivo);
        return reservaRepository.save(reserva);
    }

    private Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
    }
}