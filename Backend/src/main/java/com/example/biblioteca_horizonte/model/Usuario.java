package com.example.biblioteca_horizonte.model;

import com.example.biblioteca_horizonte.model.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String legajo;
    private String nombre;

    @Column(unique = true)
    private String correoElectronico;

    private String password;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;
}