package com.example.biblioteca_horizonte.model;

import com.example.biblioteca_horizonte.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private String moduloHorario;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    private String motivoRechazo;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docente;

    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;
}