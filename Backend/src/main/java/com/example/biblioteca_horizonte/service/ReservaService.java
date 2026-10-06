package com.example.biblioteca_horizonte.service;

import com.example.biblioteca_horizonte.model.Reserva;
import com.example.biblioteca_horizonte.model.enums.EstadoReserva;
import com.example.biblioteca_horizonte.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public Reserva crearReserva(Reserva reserva) {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        return reservaRepository.save(reserva);
    }

    public Reserva confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar la regla de oro: verificar si ya hay otra confirmada para el mismo equipo, fecha y módulo
        boolean yaExiste = reservaRepository.existsByEquipoIdAndFechaAndModuloHorarioAndEstado(
                reserva.getEquipo().getId(),
                reserva.getFecha(),
                reserva.getModuloHorario(),
                EstadoReserva.CONFIRMADA
        );

        if (yaExiste) {
            throw new RuntimeException("Error: Ya existe una reserva CONFIRMADA para este equipo, fecha y módulo.");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    public Reserva rechazarReserva(Long id, String motivo) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(EstadoReserva.RECHAZADA);
        reserva.setMotivoRechazo(motivo);
        return reservaRepository.save(reserva);
    }
}