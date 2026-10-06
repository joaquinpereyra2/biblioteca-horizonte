package com.example.biblioteca_horizonte.model;

import com.example.biblioteca_horizonte.model.enums.TipoEquipo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "equipos")
@Data
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEquipo tipo;

    private boolean activo = true;
}