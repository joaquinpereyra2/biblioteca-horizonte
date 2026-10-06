package com.example.biblioteca_horizonte.repository;

import com.example.biblioteca_horizonte.model.Reserva;
import com.example.biblioteca_horizonte.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByDocenteId(Long docenteId);
    List<Reserva> findByEstado(EstadoReserva estado);

    // La regla de oro: verifica si ya existe una reserva CONFIRMADA para el mismo equipo, fecha y módulo
    boolean existsByEquipoIdAndFechaAndModuloHorarioAndEstado(
            Long equipoId, LocalDate fecha, String moduloHorario, EstadoReserva estado
    );
}